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
import tech.pegasys.pantheon.ethereum.eth.transactions.PendingTransactions;
import tech.pegasys.pantheon.ethereum.graphql.internal.exception.InvalidGraphQLRpcRequestException;
import tech.pegasys.pantheon.ethereum.graphql.internal.queries.BlockchainQueries;
import tech.pegasys.pantheon.ethereum.graphql.internal.response.GraphQLRpcError;
import tech.pegasys.pantheon.ethereum.graphql.internal.results.TransactionCompleteResult;
import tech.pegasys.pantheon.ethereum.graphql.internal.results.TransactionPendingResult;
import tech.pegasys.pantheon.ethereum.graphql.internal.results.TransactionResult;

import java.util.Optional;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

public class TransactionDataFetcher implements DataFetcher<TransactionResult> {
  private final BlockchainQueries blockchain;
  private final PendingTransactions pendingTransactions;

  public TransactionDataFetcher(
      final BlockchainQueries blockchain, final PendingTransactions pendingTransactions) {
    this.blockchain = blockchain;
    this.pendingTransactions = pendingTransactions;
  }

  @Override
  public TransactionResult get(final DataFetchingEnvironment environment) throws Exception {

    Hash hash = environment.getArgument("hash");
    if (hash == null)
      throw new InvalidGraphQLRpcRequestException(GraphQLRpcError.INVALID_PARAMS.getMessage());

    final Optional<TransactionResult> transactionCompleteResult =
        blockchain.transactionByHash(hash).map(TransactionCompleteResult::new);
    return transactionCompleteResult.orElseGet(
        () ->
            pendingTransactions
                .getTransactionByHash(hash)
                .map(TransactionPendingResult::new)
                .orElse(null));
  }
}
