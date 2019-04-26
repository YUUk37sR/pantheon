package tech.pegasys.pantheon.ethereum.graphql.internal;

import java.util.List;

import graphql.language.ScalarTypeDefinition;
import graphql.schema.Coercing;
import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLScalarType;

import tech.pegasys.pantheon.ethereum.graphql.internal.scalars.AddressScalar;
import tech.pegasys.pantheon.ethereum.graphql.internal.scalars.BigIntScalar;
import tech.pegasys.pantheon.ethereum.graphql.internal.scalars.Bytes32Scalar;
import tech.pegasys.pantheon.ethereum.graphql.internal.scalars.BytesScalar;
import tech.pegasys.pantheon.ethereum.graphql.internal.scalars.LongScalar;

public class Scalars {

	public static GraphQLScalarType Address = new AddressScalar();
	public static GraphQLScalarType Bytes = new BytesScalar();
	public static GraphQLScalarType Bytes32 = new Bytes32Scalar();
	public static GraphQLScalarType BigInt = new BigIntScalar();
	public static GraphQLScalarType Long = new LongScalar();

}
