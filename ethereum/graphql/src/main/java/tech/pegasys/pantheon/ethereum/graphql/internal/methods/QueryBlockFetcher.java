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

import tech.pegasys.pantheon.ethereum.core.Hash;
import tech.pegasys.pantheon.ethereum.graphql.internal.queries.BlockchainQueries;
import tech.pegasys.pantheon.ethereum.graphql.internal.results.Block;
import tech.pegasys.pantheon.ethereum.graphql.internal.results.BlockResultFactory;
import tech.pegasys.pantheon.util.bytes.Bytes32;

import com.google.common.primitives.UnsignedLong;
import graphql.schema.DataFetchingEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class QueryBlockFetcher implements GraphQLRpcFetcher<Block> {
  private static final Logger LOG = LogManager.getLogger();
  private final BlockchainQueries blockchain;
  private final BlockResultFactory blockResult = new BlockResultFactory();

  public QueryBlockFetcher(final BlockchainQueries blockchain) {
    this.blockchain = blockchain;
  }

  @SuppressWarnings("unused")
  @Override
  public Block get(final DataFetchingEnvironment environment) throws Exception {
    LOG.info("QueryBlockFetcher");
    Block result;
    UnsignedLong number = environment.getArgument("number");
    Bytes32 hash = environment.getArgument("hash");
    if (hash != null) {
      result =
          blockchain
              .blockByHash(Hash.wrap(hash))
              .map(tx -> blockResult.transactionComplete(tx))
              .orElse(null);
    } else if (number != null) {
      result =
          blockchain
              .blockByNumber(number.longValue())
              .map(tx -> blockResult.transactionComplete(tx))
              .orElse(null);
    } else {
      result = blockchain.latestBlock().map(tx -> blockResult.transactionComplete(tx)).orElse(null);
    }

    return result;
  }

  @Override
  public String getType() {
    return GraphQLRpcDataFetcherType.BLOCK.getType();
  }

  @Override
  public String getField() {
    return GraphQLRpcDataFetcherType.BLOCK.getField();
  }
}
