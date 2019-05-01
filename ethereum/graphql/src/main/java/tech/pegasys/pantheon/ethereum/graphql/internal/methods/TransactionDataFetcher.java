package tech.pegasys.pantheon.ethereum.graphql.internal.methods;

import java.util.Optional;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import tech.pegasys.pantheon.ethereum.core.Hash;
import tech.pegasys.pantheon.ethereum.eth.transactions.PendingTransactions;
import tech.pegasys.pantheon.ethereum.graphql.internal.results.TransactionResult;
import tech.pegasys.pantheon.ethereum.graphql.internal.results.TransactionCompleteResult;
import tech.pegasys.pantheon.ethereum.graphql.internal.results.TransactionPendingResult;
import tech.pegasys.pantheon.ethereum.graphql.internal.exception.InvalidGraphQLRpcRequestException;
import tech.pegasys.pantheon.ethereum.graphql.internal.queries.BlockchainQueries;
import tech.pegasys.pantheon.ethereum.graphql.internal.response.GraphQLRpcError;

public class TransactionDataFetcher implements DataFetcher<TransactionResult> {
	 private final BlockchainQueries blockchain;
	 private final PendingTransactions pendingTransactions;
	 
	 public TransactionDataFetcher(BlockchainQueries blockchain, PendingTransactions pendingTransactions) {
		 this.blockchain = blockchain;
		 this.pendingTransactions = pendingTransactions;
	 }
	 
	@Override
	public TransactionResult get(DataFetchingEnvironment environment) throws Exception {
		
		Hash hash = environment.getArgument("hash");
		if(hash == null)
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
