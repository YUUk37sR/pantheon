package tech.pegasys.pantheon.ethereum.graphql.internal.types;

import java.util.HashMap;
import java.util.Map;

import graphql.schema.DataFetcher;
import graphql.schema.idl.TypeRuntimeWiring;

public class Query {
	private final Map<String, DataFetcher> fetchers;
	
	public Query(Map<String, DataFetcher> fetchers) {
		this.fetchers = fetchers;
	}

	public TypeRuntimeWiring getWiring() {
		return TypeRuntimeWiring
				.newTypeWiring(this.getClass().getSimpleName())
				.dataFetchers(this.fetchers)
				.build();
				
	}
}
