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
package tech.pegasys.pantheon.ethereum.graphql.internal.methods;

import tech.pegasys.pantheon.util.bytes.Bytes32;

import graphql.schema.DataFetchingEnvironment;

public class AccountStorageFetcher implements GraphQLRpcFetcher<Bytes32> {

  public AccountStorageFetcher() {}

  @Override
  public Bytes32 get(final DataFetchingEnvironment environment) throws Exception {
    Bytes32 slot = environment.getArgument("slot");
    return slot;
  }

  @Override
  public String getType() {
    return GraphQLRpcDataFetcherType.STORAGE.getType();
  }

  @Override
  public String getField() {
    return GraphQLRpcDataFetcherType.STORAGE.getType();
  }
}
