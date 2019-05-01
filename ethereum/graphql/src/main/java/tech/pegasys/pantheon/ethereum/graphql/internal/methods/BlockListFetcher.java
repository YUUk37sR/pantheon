package tech.pegasys.pantheon.ethereum.graphql.internal.methods;

import java.util.ArrayList;
import java.util.List;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import tech.pegasys.pantheon.ethereum.graphql.internal.exception.InvalidGraphQLRpcRequestException;
import tech.pegasys.pantheon.ethereum.graphql.internal.queries.BlockchainQueries;
import tech.pegasys.pantheon.ethereum.graphql.internal.response.GraphQLRpcError;
import tech.pegasys.pantheon.ethereum.graphql.internal.results.BlockResult;
import tech.pegasys.pantheon.ethereum.graphql.internal.results.BlockResultFactory;

public class BlockListFetcher implements DataFetcher<List<BlockResult>>{
	private final BlockchainQueries blockchain;
	private final BlockResultFactory blockResult = new BlockResultFactory();
	
	public BlockListFetcher(final BlockchainQueries blockchain) {
	    this.blockchain = blockchain;
	}
	@Override
	public List<BlockResult> get(DataFetchingEnvironment environment) throws Exception {
		List<BlockResult> resultList = new ArrayList<>();
		
		Long to = environment.getArgument("to");
		if(to == null)
			to = blockchain.headBlockNumber();
		
		Long from = environment.getArgument("from");
		if(from == null || from > to)
			throw new InvalidGraphQLRpcRequestException(GraphQLRpcError.INVALID_PARAMS.getMessage());

		for (long i = from; i <= to; ++i)
			resultList.add(
					blockchain.blockByNumber(i).map(tx -> blockResult.transactionComplete(tx)).orElse(null));	
		
		return resultList;
	}

}
