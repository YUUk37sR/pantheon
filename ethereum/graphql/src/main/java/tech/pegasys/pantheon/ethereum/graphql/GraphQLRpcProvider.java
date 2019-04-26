package tech.pegasys.pantheon.ethereum.graphql;

import java.io.File;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

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
  
  private RuntimeWiring buildRuntimeWiring() {
	  return RuntimeWiring.newRuntimeWiring()
			 .scalar(Scalars) 
			 .build()
  }

}
