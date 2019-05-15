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
package tech.pegasys.pantheon.ethereum.graphql.internal.methods;

import tech.pegasys.pantheon.ethereum.graphql.internal.exception.InvalidGraphQLRpcRequestException;
import tech.pegasys.pantheon.ethereum.graphql.internal.queries.BlockchainQueries;
import tech.pegasys.pantheon.ethereum.graphql.internal.response.GraphQLRpcError;
import tech.pegasys.pantheon.ethereum.graphql.internal.results.Block;
import tech.pegasys.pantheon.ethereum.graphql.internal.results.BlockResultFactory;

import java.util.ArrayList;
import java.util.List;

import com.google.common.primitives.UnsignedLong;
import graphql.schema.DataFetchingEnvironment;

public class QueryBlocksFetcher implements GraphQLRpcFetcher<List<Block>> {
  private final BlockchainQueries blockchain;
  private final BlockResultFactory blockResult = new BlockResultFactory();

  public QueryBlocksFetcher(final BlockchainQueries blockchain) {
    this.blockchain = blockchain;
  }

  @Override
  public List<Block> get(final DataFetchingEnvironment environment)
      throws InvalidGraphQLRpcRequestException {
    List<Block> resultList = new ArrayList<>();

    UnsignedLong to = environment.getArgument("to");
    if (to == null) to = UnsignedLong.valueOf(blockchain.headBlockNumber());

    UnsignedLong from = environment.getArgument("from");
    if (from == null || from.longValue() > to.longValue())
      throw new InvalidGraphQLRpcRequestException(
          GraphQLRpcError.BLOCK_LIST_PARAMETER_ERROR.getMessage(), null);

    for (long i = from.longValue(); i <= to.longValue(); ++i)
      resultList.add(
          blockchain.blockByNumber(i).map(tx -> blockResult.transactionComplete(tx)).orElse(null));

    return resultList;
  }

  @Override
  public String getType() {
    return GraphQLRpcDataFetcherType.BLOCKS.getType();
  }

  @Override
  public String getField() {
    return GraphQLRpcDataFetcherType.BLOCKS.getField();
  }
}
