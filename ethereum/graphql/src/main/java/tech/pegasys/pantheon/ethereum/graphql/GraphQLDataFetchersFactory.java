package tech.pegasys.pantheon.ethereum.graphql;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import graphql.schema.DataFetcher;
import tech.pegasys.pantheon.config.GenesisConfigOptions;
import tech.pegasys.pantheon.ethereum.blockcreation.MiningCoordinator;
import tech.pegasys.pantheon.ethereum.chain.Blockchain;
import tech.pegasys.pantheon.ethereum.core.PrivacyParameters;
import tech.pegasys.pantheon.ethereum.core.Synchronizer;
import tech.pegasys.pantheon.ethereum.eth.transactions.TransactionPool;
import tech.pegasys.pantheon.ethereum.graphql.GraphQLRpcConfiguration;
import tech.pegasys.pantheon.ethereum.graphql.RpcApi;
import tech.pegasys.pantheon.ethereum.graphql.internal.filter.FilterManager;
import tech.pegasys.pantheon.ethereum.graphql.internal.queries.BlockchainQueries;
import tech.pegasys.pantheon.ethereum.graphql.internal.parameters.GraphQLRpcParameter;
import tech.pegasys.pantheon.ethereum.graphql.internal.results.BlockResultFactory;
import tech.pegasys.pantheon.ethereum.graphql.internal.methods.GraphQLRpcMethod;
import tech.pegasys.pantheon.ethereum.graphql.GraphQLRpcConfiguration;
import tech.pegasys.pantheon.ethereum.graphql.RpcApis;
import tech.pegasys.pantheon.ethereum.graphql.internal.methods.EthGetBlockByHash;
import tech.pegasys.pantheon.ethereum.mainnet.ProtocolSchedule;
import tech.pegasys.pantheon.ethereum.p2p.api.P2PNetwork;
import tech.pegasys.pantheon.ethereum.p2p.wire.Capability;
import tech.pegasys.pantheon.ethereum.permissioning.AccountWhitelistController;
import tech.pegasys.pantheon.ethereum.permissioning.NodeLocalConfigPermissioningController;
import tech.pegasys.pantheon.ethereum.worldstate.WorldStateArchive;
import tech.pegasys.pantheon.metrics.MetricsSystem;
import tech.pegasys.pantheon.metrics.prometheus.MetricsConfiguration;

public class GraphQLDataFetchersFactory {
	
	private final BlockResultFactory blockResult = new BlockResultFactory();
  	private final GraphQLRpcParameter parameter = new GraphQLRpcParameter();

	public Map<String, DataFetcher> fetchers(
	  final String clientVersion,
      final int networkId,
      final GenesisConfigOptions genesisConfigOptions,
      final P2PNetwork peerNetworkingService,
      final Blockchain blockchain,
      final WorldStateArchive worldStateArchive,
      final Synchronizer synchronizer,
      final TransactionPool transactionPool,
      final ProtocolSchedule<?> protocolSchedule,
      final MiningCoordinator miningCoordinator,
      final MetricsSystem metricsSystem,
      final Set<Capability> supportedCapabilities,
      final Collection<RpcApi> rpcApis,
      final FilterManager filterManager,
      final Optional<AccountWhitelistController> accountsWhitelistController,
      final Optional<NodeLocalConfigPermissioningController> nodeWhitelistController,
      final PrivacyParameters privacyParameters,
      final GraphQLRpcConfiguration graphQLRpcConfiguration,
      final MetricsConfiguration metricsConfiguration) {
		
		final BlockchainQueries blockchainQueries =
		        new BlockchainQueries(blockchain, worldStateArchive);
		
		    return fetchers(
		        clientVersion,
		        networkId,
		        genesisConfigOptions,
		        peerNetworkingService,
		        blockchainQueries,
		        synchronizer,
		        protocolSchedule,
		        filterManager,
		        transactionPool,
		        miningCoordinator,
		        metricsSystem,
		        supportedCapabilities,
		        accountsWhitelistController,
		        nodeWhitelistController,
		        rpcApis,
		        privacyParameters,
		        graphQLRpcConfiguration,
		        metricsConfiguration);
	}
	
	public Map<String, DataFetcher> fetchers(
      final String clientVersion,
      final int networkId,
      final GenesisConfigOptions genesisConfigOptions,
      final P2PNetwork p2pNetwork,
      final BlockchainQueries blockchainQueries,
      final Synchronizer synchronizer,
      final ProtocolSchedule<?> protocolSchedule,
      final FilterManager filterManager,
      final TransactionPool transactionPool,
      final MiningCoordinator miningCoordinator,
      final MetricsSystem metricsSystem,
      final Set<Capability> supportedCapabilities,
      final Optional<AccountWhitelistController> accountsWhitelistController,
      final Optional<NodeLocalConfigPermissioningController> nodeWhitelistController,
      final Collection<RpcApi> rpcApis,
      final PrivacyParameters privacyParameters,
      final GraphQLRpcConfiguration graphQLRpcConfiguration,
      final MetricsConfiguration metricsConfiguration) {
	    final Map<String, DataFetcher>  enabledFetchers = new HashMap<>();
	      if (rpcApis.contains(RpcApis.ETH)) {
	        addFetchers(
	            enabledFetchers,
	            new EthGetBlockByHash(blockchainQueries, blockResult, parameter));
	      }
      return  enabledFetchers;   
	}
	
  private void addFetchers(
	      final Map<String, DataFetcher> fetchers, final GraphQLRpcMethod... rpcMethods) {
	    for (final GraphQLRpcMethod rpcMethod : rpcMethods) {
	      fetchers.put(rpcMethod.getName(), rpcMethod);
	    }
	  }
}
