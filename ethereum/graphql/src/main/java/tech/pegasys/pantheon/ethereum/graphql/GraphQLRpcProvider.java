package tech.pegasys.pantheon.ethereum.graphql;

import java.io.File;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;
import tech.pegasys.pantheon.ethereum.graphql.internal.scalars.AddressScalar;
import tech.pegasys.pantheon.ethereum.graphql.internal.scalars.BigIntScalar;
import tech.pegasys.pantheon.ethereum.graphql.internal.scalars.Bytes32Scalar;
import tech.pegasys.pantheon.ethereum.graphql.internal.scalars.BytesScalar;
import tech.pegasys.pantheon.ethereum.graphql.internal.scalars.LongScalar;

public class GraphQLRpcProvider {
  
  public static GraphQL GraphQLRpcProvider() {
	  SchemaParser schemaParser = new SchemaParser();
      SchemaGenerator schemaGenerator = new SchemaGenerator();

      File schemaFile = new File(GraphQLRpcProvider.class.getResource("schema.graphqls").getFile());	
      TypeDefinitionRegistry typeRegistry = schemaParser.parse(schemaFile);
      RuntimeWiring wiring = buildRuntimeWiring();
      GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, wiring);
      return GraphQL.newGraphQL(graphQLSchema).build();
  }
  
  private static RuntimeWiring buildRuntimeWiring() {
	  return RuntimeWiring.newRuntimeWiring()
			 .scalar(new AddressScalar())
			 .scalar(new BytesScalar())
			 .scalar(new Bytes32Scalar())
			 .scalar(new BigIntScalar())
			 .scalar(new LongScalar())
			 .type(TypeRuntimeWiring.newTypeWiring("Query")
				    .dataFetcher("block", GraphQLDataFetchers.getBlockDataFetcher()))
			 .build();
  }

}
