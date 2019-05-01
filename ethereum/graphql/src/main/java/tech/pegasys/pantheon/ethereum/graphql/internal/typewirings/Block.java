package tech.pegasys.pantheon.ethereum.graphql.internal.typewirings;

import java.util.Map;

import graphql.schema.DataFetcher;
import graphql.schema.idl.TypeRuntimeWiring;

public class Block implements GraphQLTypeWiring {
	@SuppressWarnings("rawtypes")
	private final Map<String, DataFetcher> fetchers;
	
	@SuppressWarnings("rawtypes")
	public Block(Map<String, DataFetcher> fetchers) {
	  this.fetchers = fetchers;
	}

	public TypeRuntimeWiring getWiring() {
	  return TypeRuntimeWiring.newTypeWiring(this.getClass().getSimpleName())
	      .dataFetchers(this.fetchers)
	      .build();
	}
}
