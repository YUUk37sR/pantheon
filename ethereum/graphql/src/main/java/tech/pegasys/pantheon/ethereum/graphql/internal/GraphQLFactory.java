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

import static graphql.schema.idl.RuntimeWiring.Builder;
import static java.nio.charset.StandardCharsets.UTF_8;

import tech.pegasys.pantheon.ethereum.graphql.internal.methods.GraphQLRpcFetcher;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;

public class GraphQLFactory {

  public static String SCHEMA = getSchemaFromResources();

  @SuppressWarnings("rawtypes")
  public GraphQL buildGraphQL(final Map<String, GraphQLRpcFetcher> fetchers) {
    SchemaParser schemaParser = new SchemaParser();
    SchemaGenerator schemaGenerator = new SchemaGenerator();
    /*File schemaFile =
    new File(getClass().getClassLoader().getResource("schema.graphqls").getFile());*/
    TypeDefinitionRegistry typeRegistry = schemaParser.parse(SCHEMA);
    RuntimeWiring wiring = buildRunTimeWiring(fetchers);
    GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, wiring);
    return GraphQL.newGraphQL(graphQLSchema).build();
  }

  @SuppressWarnings("rawtypes")
  private RuntimeWiring buildRunTimeWiring(final Map<String, GraphQLRpcFetcher> fetchers) {

    Builder runTimeWiringBuilder = RuntimeWiring.newRuntimeWiring();
    runTimeWiringBuilder
        .scalar(Scalars.AddressScalar)
        .scalar(Scalars.BytesScalar)
        .scalar(Scalars.Bytes32Scalar)
        .scalar(Scalars.BigIntScalar)
        .scalar(Scalars.LongScalar);
    fetchers.forEach(
        (typeName, fetcher) ->
            runTimeWiringBuilder.type(
                TypeRuntimeWiring.newTypeWiring(typeName)
                    .dataFetcher(fetcher.getField(), (DataFetcher) fetcher)));
    return runTimeWiringBuilder.build();
  }

  private static String getSchemaFromResources() {
    ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    InputStream is = classLoader.getResourceAsStream("schema2.graphqls");
    if (is != null) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(is, UTF_8));
      return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    }
    return null;
  }
}
