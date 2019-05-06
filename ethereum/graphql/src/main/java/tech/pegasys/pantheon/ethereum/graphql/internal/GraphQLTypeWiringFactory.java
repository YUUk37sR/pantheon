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

import tech.pegasys.pantheon.ethereum.graphql.internal.methods.BlockFetcher;
import tech.pegasys.pantheon.ethereum.graphql.internal.methods.BlocksFetcher;
import tech.pegasys.pantheon.ethereum.graphql.internal.methods.GraphQLRpcDataFetcherType;
import tech.pegasys.pantheon.ethereum.graphql.internal.methods.MinerDataFetcher;
import tech.pegasys.pantheon.ethereum.graphql.internal.queries.BlockchainQueries;

import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.RuntimeWiring.Builder;
import graphql.schema.idl.TypeRuntimeWiring;

public class GraphQLTypeWiringFactory {
	
  public GraphQLTypeWiringFactory() {
	  
  }

  public static RuntimeWiring getRunTimeWiring(final BlockchainQueries blockchainQueries) {

    Builder runTimeWiringBuilder =  RuntimeWiring.newRuntimeWiring()
        .scalar(Scalars.AddressScalar)
        .scalar(Scalars.BytesScalar)
        .scalar(Scalars.Bytes32Scalar)
        .scalar(Scalars.BigIntScalar)
        .scalar(Scalars.LongScalar);
    runTimeWiringBuilder.type(GraphQLRpcDataFetcherType.BLOCK.getType(), builder -> builder.dataFetcher(GraphQLRpcDataFetcherType.BLOCK.getField(), new BlockFetcher(blockchainQueries)));
    return runTimeWiringBuilder.type(
            TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("block", new BlockFetcher(blockchainQueries))
                .dataFetcher("blocks", new BlocksFetcher(blockchainQueries))
                .build())
        .type(
            TypeRuntimeWiring.newTypeWiring("Block")
                .dataFetcher("miner", new MinerDataFetcher(blockchainQueries))
                .build())
        .build();
  }
}
