package tech.pegasys.pantheon.ethereum.graphql.internal.methods;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import tech.pegasys.pantheon.ethereum.core.Hash;
import tech.pegasys.pantheon.ethereum.graphql.internal.queries.BlockchainQueries;
import tech.pegasys.pantheon.ethereum.graphql.internal.results.BlockResult;
import tech.pegasys.pantheon.ethereum.graphql.internal.results.BlockResultFactory;

public class BlockDataFetcher implements DataFetcher<Block> {
	private final BlockchainQueries blockchain;
	private final BlockResultFactory blockResult = new BlockResultFactory();
	
	public BlockDataFetcher(final BlockchainQueries blockchain) {
	    this.blockchain = blockchain;
	}
	
	@Override
	public Block get(DataFetchingEnvironment environment) throws Exception {
		BlockResult result;
		Long number = environment.getArgument("number");
		Hash hash = environment.getArgument("hash");
		if(hash != null) {
			result = blockchain.blockByHash(hash).map(tx -> blockResult.transactionComplete(tx)).orElse(null);
		} else if(number != null) {
			result = blockchain.blockByNumber(number).map(tx -> blockResult.transactionComplete(tx)).orElse(null);;
		} else {
			result = blockchain.latestBlock().map(tx -> blockResult.transactionComplete(tx)).orElse(null);;
		}
		return null;
	}

}
