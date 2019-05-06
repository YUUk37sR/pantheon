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
package tech.pegasys.pantheon.ethereum.graphql.internal.typewirings;

import tech.pegasys.pantheon.ethereum.graphql.internal.Scalars;
import tech.pegasys.pantheon.ethereum.graphql.internal.methods.BlockDataFetcher;
import tech.pegasys.pantheon.ethereum.graphql.internal.methods.BlockListFetcher;
import tech.pegasys.pantheon.ethereum.graphql.internal.methods.MinerDataFetcher;
import tech.pegasys.pantheon.ethereum.graphql.internal.queries.BlockchainQueries;

import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeRuntimeWiring;

public class GraphQLTypeWiringFactory {

  public static RuntimeWiring getRunTimeWiring(final BlockchainQueries blockchainQueries) {

    return RuntimeWiring.newRuntimeWiring()
        .scalar(Scalars.Address)
        .scalar(Scalars.Bytes)
        .scalar(Scalars.Bytes32)
        .scalar(Scalars.BigInt)
        .scalar(Scalars.Long)
        .type(
            TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("block", new BlockDataFetcher(blockchainQueries))
                .dataFetcher("blocks", new BlockListFetcher(blockchainQueries))
                .build())
        .type(
            TypeRuntimeWiring.newTypeWiring("Block")
                .dataFetcher("miner", new MinerDataFetcher(blockchainQueries))
                .build())
        .build();
  }
}
