/*
 * Copyright 2018 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package tech.pegasys.pantheon.ethereum.graphql;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Streams.stream;
import static java.util.stream.Collectors.toList;
import static tech.pegasys.pantheon.util.NetworkUtility.urlForSocketAddress;

import tech.pegasys.pantheon.ethereum.graphql.internal.GraphQLRpcRequest;
import tech.pegasys.pantheon.ethereum.graphql.internal.exception.InvalidGraphQLRpcRequestException;
import tech.pegasys.pantheon.ethereum.graphql.internal.response.GraphQLRpcError;
import tech.pegasys.pantheon.ethereum.graphql.internal.response.GraphQLRpcErrorResponse;
import tech.pegasys.pantheon.ethereum.graphql.internal.response.GraphQLRpcResponse;
import tech.pegasys.pantheon.ethereum.graphql.internal.response.GraphQLRpcResponseType;
import tech.pegasys.pantheon.ethereum.graphql.internal.response.GraphQLRpcSuccessResponse;
import tech.pegasys.pantheon.metrics.LabelledMetric;
import tech.pegasys.pantheon.metrics.MetricCategory;
import tech.pegasys.pantheon.metrics.MetricsSystem;
import tech.pegasys.pantheon.metrics.OperationTimer;
import tech.pegasys.pantheon.metrics.OperationTimer.TimingContext;
import tech.pegasys.pantheon.util.NetworkUtility;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import graphql.ExecutionResult;
import graphql.GraphQL;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GraphQLRpcHttpService {

  private static final Logger LOG = LogManager.getLogger();

  private static final InetSocketAddress EMPTY_SOCKET_ADDRESS = new InetSocketAddress("0.0.0.0", 0);
  private static final String APPLICATION_GRAPHQL = "application/graphql";
  private static final String EMPTY_RESPONSE = "";

  private final Vertx vertx;
  private final GraphQLRpcConfiguration config;
  private final GraphQL graphQL;
  private final Path dataDir;
  private final LabelledMetric<OperationTimer> requestTimer;

  private HttpServer httpServer;

  /**
   * Construct a GraphQLRpcHttpService handler
   *
   * @param vertx The vertx process that will be running this service
   * @param dataDir The data directory where requests can be buffered
   * @param config Configuration for the graphql being loaded
   * @param metricsSystem The metrics service that activities should be reported to
   * @param grapQL GraphQL Object
   */
  public GraphQLRpcHttpService(
      final Vertx vertx,
      final Path dataDir,
      final GraphQLRpcConfiguration config,
      final MetricsSystem metricsSystem,
      final GraphQL graphQL) {
    this.dataDir = dataDir;
    requestTimer =
        metricsSystem.createLabelledTimer(
            MetricCategory.RPC,
            "request_time",
            "Time taken to process a GraphQL-RPC request",
            "methodName");
    validateConfig(config);
    this.config = config;
    this.vertx = vertx;
    this.graphQL = graphQL;
  }

  private void validateConfig(final GraphQLRpcConfiguration config) {
    checkArgument(
        config.getPort() == 0 || NetworkUtility.isValidPort(config.getPort()),
        "Invalid port configuration.");
    checkArgument(config.getHost() != null, "Required host is not configured.");
  }

  public CompletableFuture<?> start() {
    LOG.info("Starting GraphQL RPC service on {}:{}", config.getHost(), config.getPort());
    // Create the HTTP server and a router object.
    httpServer =
        vertx.createHttpServer(
            new HttpServerOptions().setHost(config.getHost()).setPort(config.getPort()));

    // Handle GraphQL requests
    final Router router = Router.router(vertx);

    // Verify Host header to avoid rebind attack.
    router.route().handler(checkWhitelistHostHeader());

    router
        .route()
        .handler(
            CorsHandler.create(buildCorsRegexFromConfig())
                .allowedHeader("*")
                .allowedHeader("content-type"));
    router
        .route()
        .handler(
            BodyHandler.create()
                .setUploadsDirectory(dataDir.resolve("uploads").toString())
                .setDeleteUploadedFilesOnEnd(true));
    router.route("/").method(HttpMethod.GET).handler(this::handleEmptyRequest);
    router
        .route("/")
        .method(HttpMethod.POST)
        .produces(APPLICATION_GRAPHQL)
        .handler(this::handleGraphQLRpcRequest);

    final CompletableFuture<?> resultFuture = new CompletableFuture<>();
    httpServer
        .requestHandler(router)
        .listen(
            res -> {
              if (!res.failed()) {
                resultFuture.complete(null);
                LOG.info(
                    "GraphQL RPC service started and listening on {}:{}",
                    config.getHost(),
                    httpServer.actualPort());
                return;
              }
              httpServer = null;
              final Throwable cause = res.cause();
              if (cause instanceof SocketException) {
                resultFuture.completeExceptionally(
                    new GraphQLRpcServiceException(
                        String.format(
                            "Failed to bind Ethereum GRAPHQL RPC listener to %s:%s: %s",
                            config.getHost(), config.getPort(), cause.getMessage())));
                return;
              }
              resultFuture.completeExceptionally(cause);
            });

    return resultFuture;
  }

  private Handler<RoutingContext> checkWhitelistHostHeader() {
    return event -> {
      final Optional<String> hostHeader = getAndValidateHostHeader(event);
      if (config.getHostsWhitelist().contains("*")
          || (hostHeader.isPresent() && hostIsInWhitelist(hostHeader.get()))) {
        event.next();
      } else {
        event
            .response()
            .setStatusCode(403)
            .putHeader("Content-Type", "application/json; charset=utf-8")
            .end("{\"message\":\"Host not authorized.\"}");
      }
    };
  }

  private Optional<String> getAndValidateHostHeader(final RoutingContext event) {
    final Iterable<String> splitHostHeader = Splitter.on(':').split(event.request().host());
    final long hostPieces = stream(splitHostHeader).count();
    if (hostPieces > 1) {
      // If the host contains a colon, verify the host is correctly formed - host [ ":" port ]
      if (hostPieces > 2 || !Iterables.get(splitHostHeader, 1).matches("\\d{1,5}+")) {
        return Optional.empty();
      }
    }
    return Optional.ofNullable(Iterables.get(splitHostHeader, 0));
  }

  private boolean hostIsInWhitelist(final String hostHeader) {
    return config.getHostsWhitelist().stream()
        .anyMatch(whitelistEntry -> whitelistEntry.toLowerCase().equals(hostHeader.toLowerCase()));
  }

  public CompletableFuture<?> stop() {
    if (httpServer == null) {
      return CompletableFuture.completedFuture(null);
    }

    final CompletableFuture<?> resultFuture = new CompletableFuture<>();
    httpServer.close(
        res -> {
          if (res.failed()) {
            resultFuture.completeExceptionally(res.cause());
          } else {
            httpServer = null;
            resultFuture.complete(null);
          }
        });
    return resultFuture;
  }

  public InetSocketAddress socketAddress() {
    if (httpServer == null) {
      return EMPTY_SOCKET_ADDRESS;
    }
    return new InetSocketAddress(config.getHost(), httpServer.actualPort());
  }

  @VisibleForTesting
  public String url() {
    if (httpServer == null) {
      return "";
    }
    return urlForSocketAddress("http", socketAddress());
  }

  private void handleGraphQLRpcRequest(final RoutingContext routingContext) {

    // Parse json
    try {
      final String json = routingContext.getBodyAsString().trim();
      if (!json.isEmpty() && json.charAt(0) == '{') {
        handleGraphQLSingleRequest(routingContext, new JsonObject(json));
      } else {
        final JsonArray array = new JsonArray(json);
        if (array.size() < 1) {
          handleGraphQLRpcError(routingContext, null, GraphQLRpcError.INVALID_REQUEST);
          return;
        }
        handleGraphQLBatchRequest(routingContext, array);
      }
    } catch (final DecodeException ex) {
      handleGraphQLRpcError(routingContext, null, GraphQLRpcError.PARSE_ERROR);
    }
  }

  // Facilitate remote health-checks in AWS, inter alia.
  private void handleEmptyRequest(final RoutingContext routingContext) {
    routingContext.response().setStatusCode(201).end();
  }

  private void handleGraphQLSingleRequest(
      final RoutingContext routingContext, final JsonObject request) {
    final HttpServerResponse response = routingContext.response();
    vertx.executeBlocking(
        future -> {
          final GraphQLRpcResponse graphQLRpcResponse = process(request);
          future.complete(graphQLRpcResponse);
        },
        false,
        (res) -> {
          if (res.failed()) {
            response.setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()).end();
            return;
          }

          final GraphQLRpcResponse graphQLRpcResponse = (GraphQLRpcResponse) res.result();
          response.setStatusCode(status(graphQLRpcResponse).code());
          response.putHeader("Content-Type", APPLICATION_GRAPHQL);
          response.end(serialise(graphQLRpcResponse));
        });
  }

  private HttpResponseStatus status(final GraphQLRpcResponse response) {

    switch (response.getType()) {
      case UNAUTHORIZED:
        return HttpResponseStatus.UNAUTHORIZED;
      case ERROR:
        return HttpResponseStatus.BAD_REQUEST;
      case SUCCESS:
      case NONE:
      default:
        return HttpResponseStatus.OK;
    }
  }

  private String serialise(final GraphQLRpcResponse response) {

    if (response.getType() == GraphQLRpcResponseType.NONE) {
      return EMPTY_RESPONSE;
    }

    return Json.encodePrettily(response);
  }

  @SuppressWarnings("rawtypes")
  private void handleGraphQLBatchRequest(
      final RoutingContext routingContext, final JsonArray jsonArray) {
    // Interpret json as graphql request
    final List<Future> responses =
        jsonArray.stream()
            .map(
                obj -> {
                  if (!(obj instanceof JsonObject)) {
                    return Future.succeededFuture(
                        errorResponse(null, GraphQLRpcError.INVALID_REQUEST));
                  }

                  final JsonObject req = (JsonObject) obj;
                  final Future<GraphQLRpcResponse> fut = Future.future();
                  vertx.executeBlocking(
                      future -> future.complete(process(req)),
                      false,
                      ar -> {
                        if (ar.failed()) {
                          fut.fail(ar.cause());
                        } else {
                          fut.complete((GraphQLRpcResponse) ar.result());
                        }
                      });
                  return fut;
                })
            .collect(toList());

    CompositeFuture.all(responses)
        .setHandler(
            (res) -> {
              if (res.failed()) {
                routingContext
                    .response()
                    .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                    .end();
                return;
              }
              final GraphQLRpcResponse[] completed =
                  res.result().list().stream()
                      .map(GraphQLRpcResponse.class::cast)
                      .filter(this::isNonEmptyResponses)
                      .toArray(GraphQLRpcResponse[]::new);

              routingContext.response().end(Json.encode(completed));
            });
  }

  private boolean isNonEmptyResponses(final GraphQLRpcResponse result) {
    return result.getType() != GraphQLRpcResponseType.NONE;
  }

  private GraphQLRpcResponse process(final JsonObject requestJson) {
    final GraphQLRpcRequest request;
    Object id = null;
    try {
      request = requestJson.mapTo(GraphQLRpcRequest.class);
    } catch (final IllegalArgumentException exception) {
      return errorResponse(id, GraphQLRpcError.INVALID_REQUEST);
    }

    LOG.debug("GRAPHQL-RPC query -> {}", request.getQuery());
    // Find method handler
    String query = request.getQuery();

    if (query == null) {
      LOG.info("Bad GET request: no query variable named \"query\" given");
      return errorResponse(id, GraphQLRpcError.INVALID_PARAMS);
    }
    //    if (AuthenticationUtils.isPermitted(authenticationService, user)) {
    // Generate response
    try (final TimingContext ignored = requestTimer.labels(request.getQuery()).startTimer()) {
      ExecutionResult result = graphQL.execute(query);
      LOG.info(Json.encodePrettily(result.getData()));
      LOG.info(Json.encodePrettily(result.getErrors()));
      return new GraphQLRpcSuccessResponse(result.getData(), result.getErrors());

    } catch (final InvalidGraphQLRpcRequestException e) {
      LOG.debug(e);
      return errorResponse(id, GraphQLRpcError.INVALID_PARAMS);
    } catch (final RuntimeException e) {
      LOG.error("Error processing GRAPHQL-RPC request", e);
      return errorResponse(id, GraphQLRpcError.INTERNAL_ERROR);
    }
  }

  private void handleGraphQLRpcError(
      final RoutingContext routingContext, final Object id, final GraphQLRpcError error) {
    routingContext
        .response()
        .setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
        .end(Json.encode(new GraphQLRpcErrorResponse(id, error)));
  }

  private GraphQLRpcResponse errorResponse(final Object id, final GraphQLRpcError error) {
    return new GraphQLRpcErrorResponse(id, error);
  }

  private String buildCorsRegexFromConfig() {
    if (config.getCorsAllowedDomains().isEmpty()) {
      return "";
    }
    if (config.getCorsAllowedDomains().contains("*")) {
      return "*";
    } else {
      final StringJoiner stringJoiner = new StringJoiner("|");
      config.getCorsAllowedDomains().stream().filter(s -> !s.isEmpty()).forEach(stringJoiner::add);
      return stringJoiner.toString();
    }
  }
}
