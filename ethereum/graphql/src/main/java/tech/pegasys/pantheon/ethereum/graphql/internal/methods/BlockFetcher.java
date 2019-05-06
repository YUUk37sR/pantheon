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
import tech.pegasys.pantheon.ethereum.graphql.internal.results.BlockResult;
import tech.pegasys.pantheon.ethereum.graphql.internal.results.BlockResultFactory;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

public class BlockFetcher implements DataFetcher<BlockResult>, GraphQLRpcFetcher {
  private final BlockchainQueries blockchain;
  private final BlockResultFactory blockResult = new BlockResultFactory();

  public BlockFetcher(final BlockchainQueries blockchain) {
    this.blockchain = blockchain;
  }

  @Override
  public BlockResult get(final DataFetchingEnvironment environment) throws Exception {
    BlockResult result;

    Long number = environment.getArgument("number");
    Hash hash = environment.getArgument("hash");
    if (hash != null) {
      result =
          blockchain.blockByHash(hash).map(tx -> blockResult.transactionComplete(tx)).orElse(null);
    } else if (number != null) {
      result =
          blockchain
              .blockByNumber(number)
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
