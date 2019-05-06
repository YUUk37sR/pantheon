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

import tech.pegasys.pantheon.ethereum.core.Address;
import tech.pegasys.pantheon.ethereum.core.Wei;
import tech.pegasys.pantheon.ethereum.graphql.internal.queries.BlockchainQueries;
import tech.pegasys.pantheon.ethereum.graphql.internal.results.AccountResult;
import tech.pegasys.pantheon.util.bytes.BytesValue;
import tech.pegasys.pantheon.util.uint.UInt256;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

public class MinerDataFetcher implements DataFetcher<AccountResult> {

  private final BlockchainQueries blockchain;

  public MinerDataFetcher(final BlockchainQueries blockchain) {
    this.blockchain = blockchain;
  }

  @Override
  public AccountResult get(final DataFetchingEnvironment environment) throws Exception {
    long blockNumber = environment.getArgument("block");
    Address address =
        blockchain
            .getBlockHeaderByNumber(blockNumber)
            .map(header -> header.getCoinbase())
            .orElse(null);
    return new AccountResult(
        address,
        blockchain.accountBalance(address, blockNumber).map(balance -> balance).orElse(Wei.ZERO),
        blockchain.accountNonce(address, blockNumber).map(count -> count.longValue()).orElse(0L),
        BytesValue.EMPTY,
        UInt256.ZERO);
  }
}
