package tech.pegasys.pantheon.ethereum.graphql.internal.typewirings;

import graphql.schema.idl.TypeRuntimeWiring;

public interface GraphQLTypeWiring {
  public TypeRuntimeWiring getWiring();

}
