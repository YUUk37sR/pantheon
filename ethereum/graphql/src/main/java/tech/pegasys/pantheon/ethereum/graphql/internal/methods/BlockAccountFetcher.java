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
import tech.pegasys.pantheon.ethereum.graphql.internal.results.Account;
import tech.pegasys.pantheon.util.bytes.BytesValue;

import graphql.schema.DataFetchingEnvironment;

public class BlockAccountFetcher implements GraphQLRpcFetcher<Account> {
  private final BlockchainQueries blockchain;
  private final long blockNumber;

  public BlockAccountFetcher(final BlockchainQueries blockchain, final long blockNumber) {
    this.blockchain = blockchain;
    this.blockNumber = blockNumber;
  }

  @Override
  public Account get(final DataFetchingEnvironment environment) throws Exception {
    Address address = environment.getArgument("address");
    Wei balance = blockchain.accountBalance(address, blockNumber).orElseGet(null);
    Long nonce = blockchain.accountNonce(address, blockNumber).orElseGet(null);
    BytesValue code = blockchain.getCode(address, blockNumber).orElseGet(null);

    return new Account(address, balance, nonce, code);
  }

  @Override
  public String getType() {
    return GraphQLRpcDataFetcherType.ACCOUNT.getType();
  }

  @Override
  public String getField() {
    return GraphQLRpcDataFetcherType.ACCOUNT.getField();
  }
}
