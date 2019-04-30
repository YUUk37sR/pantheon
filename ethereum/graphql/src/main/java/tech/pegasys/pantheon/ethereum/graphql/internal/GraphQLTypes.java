package tech.pegasys.pantheon.ethereum.graphql.internal;

import graphql.schema.GraphQLScalarType;
import tech.pegasys.pantheon.ethereum.graphql.internal.types.Query;

public class GraphQLTypes {
	public static GraphQLObjectType Address = new Query();

}
