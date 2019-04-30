package tech.pegasys.pantheon.ethereum.graphql.internal;

import java.util.List;

import graphql.schema.DataFetcher;
import tech.pegasys.pantheon.ethereum.graphql.internal.methods.Block;
import tech.pegasys.pantheon.ethereum.graphql.internal.methods.BlockDataFetcher;
import tech.pegasys.pantheon.ethereum.graphql.internal.methods.BlockListFetcher;

public class GraphQLDataFetchers {
	
	GraphQLDataFetchers( ) {
		
	}
	
	public static DataFetcher<Block> getBlockDataFetcher() {
		return new BlockDataFetcher();
	}
	
	public static DataFetcher<List<Block>> getBlockListFetcher() {
		return new BlockListFetcher();
	}
	
}
