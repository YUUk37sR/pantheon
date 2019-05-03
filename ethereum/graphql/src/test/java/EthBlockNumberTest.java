package tech.pegasys.pantheon.ethereum.graphql.internal.methods;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import tech.pegasys.pantheon.ethereum.graphql.internal.GraphQLRpcRequest;
import tech.pegasys.pantheon.ethereum.graphql.internal.queries.BlockchainQueries;
import tech.pegasys.pantheon.ethereum.graphql.internal.response.GraphQLRpcResponse;
import tech.pegasys.pantheon.ethereum.graphql.internal.response.GraphQLRpcSuccessResponse;
import tech.pegasys.pantheon.ethereum.graphql.internal.results.Quantity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EthBlockNumberTest {

  private final String JSON_RPC_VERSION = "2.0";
  private final String ETH_METHOD = "eth_blockNumber";

  @Mock private BlockchainQueries blockchainQueries;
  private EthBlockNumber method;

  @Before
  public void setUp() {
    method = new EthBlockNumber(blockchainQueries);
  }

  @Test
  public void shouldReturnCorrectMethodName() {
    assertThat(method.getName()).isEqualTo(ETH_METHOD);
  }

  @Test
  public void shouldReturnCorrectResult() {
    final long headBlockNumber = 123456L;

    when(blockchainQueries.headBlockNumber()).thenReturn(headBlockNumber);

    final JsonRpcRequest request =
        new JsonRpcRequest(JSON_RPC_VERSION, ETH_METHOD, new Object[] {});

    final JsonRpcResponse expected =
        new JsonRpcSuccessResponse(request.getId(), Quantity.create(headBlockNumber));
    final JsonRpcResponse actual = method.response(request);

    assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
  }
}
