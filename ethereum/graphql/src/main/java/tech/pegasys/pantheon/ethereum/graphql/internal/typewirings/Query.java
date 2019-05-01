/*
 * Copyright 2019 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package tech.pegasys.pantheon.ethereum.graphql.internal.typewirings;

import java.util.Map;

import graphql.schema.DataFetcher;
import graphql.schema.idl.TypeRuntimeWiring;

public class Query implements GraphQLTypeWiring {
  @SuppressWarnings("rawtypes")
  private final Map<String, DataFetcher> fetchers;
  private final String typeName;

  @SuppressWarnings("rawtypes")
  public Query(Map<String, DataFetcher> fetchers, String typeName) {
	this.typeName = typeName;
    this.fetchers = fetchers;
  }

  public TypeRuntimeWiring getWiring() {
    return TypeRuntimeWiring.newTypeWiring(this.typeName)
        .dataFetchers(this.fetchers)
        .build();
  }
}
