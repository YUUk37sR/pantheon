/*
 * Copyright 2018 ConsenSys AG.
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
package tech.pegasys.pantheon.ethereum.graphql.internal.response;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.Objects;

@JsonPropertyOrder({"jsonrpc", "id", "result"})
public class GraphQLRpcSuccessResponse implements GraphQLRpcResponse {

  private final Object id;
  private final Object result;

  public GraphQLRpcSuccessResponse(final Object id, final Object result) {
    this.id = id;
    this.result = result;
  }

  public GraphQLRpcSuccessResponse(final Object id) {
    this.id = id;
    this.result = "Success";
  }

  @JsonGetter("id")
  public Object getId() {
    return id;
  }

  @JsonGetter("result")
  public Object getResult() {
    return result;
  }

  @Override
  @JsonIgnore
  public GraphQLRpcResponseType getType() {
    return GraphQLRpcResponseType.SUCCESS;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final GraphQLRpcSuccessResponse that = (GraphQLRpcSuccessResponse) o;
    return Objects.equal(id, that.id) && Objects.equal(result, that.result);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, result);
  }
}