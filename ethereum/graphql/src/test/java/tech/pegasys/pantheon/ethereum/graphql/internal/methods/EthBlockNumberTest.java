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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import tech.pegasys.pantheon.ethereum.graphql.internal.GraphQLRpcRequest;
import tech.pegasys.pantheon.ethereum.graphql.internal.queries.BlockchainQueries;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EthBlockNumberTest {

  private final String QUERY = "{block(number: 123456) { number } }";
  private final Map<String, Object> VARIABLES = new HashMap<>();
  private final String OPERATION_NAME = "";
  // private GraphQL graphQL;

  @Mock private BlockchainQueries blockchainQueries;

  @Before
  public void setUp() {
    // this.graphQL =
    //    new
    // GraphQLFactory().graphQL(GraphQLTypeWiringFactory.getRunTimeWiring(blockchainQueries));
  }

  @Test
  public void shouldReturnCorrectResult() {
    final long headBlockNumber = 123456L;

    when(blockchainQueries.headBlockNumber()).thenReturn(headBlockNumber);

    final GraphQLRpcRequest request =
        new GraphQLRpcRequest(this.QUERY, this.VARIABLES, this.OPERATION_NAME);

    final Object expected = "{\"data\": { \"block\": { \"number\": 123456 }}}";

    final Object actual = request.getQuery();

    assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
  }
}
