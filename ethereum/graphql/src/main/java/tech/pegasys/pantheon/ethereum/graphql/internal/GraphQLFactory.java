/*
 * Copyright 2019 ConsenSys AG.
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
package tech.pegasys.pantheon.ethereum.graphql.internal;

import tech.pegasys.pantheon.config.GenesisConfigOptions;
import tech.pegasys.pantheon.ethereum.blockcreation.MiningCoordinator;
import tech.pegasys.pantheon.ethereum.chain.Blockchain;
import tech.pegasys.pantheon.ethereum.core.PrivacyParameters;
import tech.pegasys.pantheon.ethereum.core.Synchronizer;
import tech.pegasys.pantheon.ethereum.eth.transactions.TransactionPool;
import tech.pegasys.pantheon.ethereum.graphql.GraphQLRpcConfiguration;
import tech.pegasys.pantheon.ethereum.graphql.internal.queries.BlockchainQueries;
import tech.pegasys.pantheon.ethereum.mainnet.ProtocolSchedule;
import tech.pegasys.pantheon.ethereum.p2p.api.P2PNetwork;
import tech.pegasys.pantheon.ethereum.p2p.wire.Capability;
import tech.pegasys.pantheon.ethereum.permissioning.AccountWhitelistController;
import tech.pegasys.pantheon.ethereum.permissioning.NodeLocalConfigPermissioningController;
import tech.pegasys.pantheon.ethereum.worldstate.WorldStateArchive;
import tech.pegasys.pantheon.metrics.MetricsSystem;
import tech.pegasys.pantheon.metrics.prometheus.MetricsConfiguration;
import tech.pegasys.pantheon.ethereum.jsonrpc.RpcApi;


import java.io.File;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

public class GraphQLFactory {

  public GraphQL graphQL(
      final String clientVersion,
      final int networkId,
      final GenesisConfigOptions genesisConfigOptions,
      final P2PNetwork peerNetworkingService,
      final Blockchain blockchain,
      final WorldStateArchive worldStateArchive,
      final Synchronizer synchronizer,
      final TransactionPool transactionPool,
      final ProtocolSchedule<?> protocolSchedule,
      final MiningCoordinator miningCoordinator,
      final MetricsSystem metricsSystem,
      final Set<Capability> supportedCapabilities,
      final Collection<RpcApi> rpcApis,
      final Optional<AccountWhitelistController> accountsWhitelistController,
      final Optional<NodeLocalConfigPermissioningController> nodeWhitelistController,
      final PrivacyParameters privacyParameters,
      final GraphQLRpcConfiguration graphQLRpcConfiguration,
      final MetricsConfiguration metricsConfiguration) {
    final BlockchainQueries blockchainQueries =
        new BlockchainQueries(blockchain, worldStateArchive);
    RuntimeWiring wiring = GraphQLTypeWiringFactory.getRunTimeWiring(blockchainQueries);
    return graphQL(wiring);
  }

  public GraphQL graphQL(final RuntimeWiring wiring) {
    SchemaParser schemaParser = new SchemaParser();
    SchemaGenerator schemaGenerator = new SchemaGenerator();
    File schemaFile =
        new File(getClass().getClassLoader().getResource("schema.graphqls").getFile());
    TypeDefinitionRegistry typeRegistry = schemaParser.parse(schemaFile);
    GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, wiring);
    return GraphQL.newGraphQL(graphQLSchema).build();
  }
}