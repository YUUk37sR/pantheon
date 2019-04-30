package tech.pegasys.pantheon.ethereum.graphql.internal.methods;

import java.util.List;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import tech.pegasys.pantheon.ethereum.graphql.internal.queries.BlockchainQueries;
import tech.pegasys.pantheon.ethereum.graphql.internal.results.BlockResultFactory;

public class BlockListFetcher implements DataFetcher<List<Block>>{
	private final BlockchainQueries blockchain;
	
	public BlockListFetcher(final BlockchainQueries blockchain) {
	    this.blockchain = blockchain;
	}
	@Override
	public List<Block> get(DataFetchingEnvironment environment) throws Exception {
		return null;
	}

}
