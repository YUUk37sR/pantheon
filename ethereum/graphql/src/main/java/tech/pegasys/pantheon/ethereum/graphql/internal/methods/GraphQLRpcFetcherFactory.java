package tech.pegasys.pantheon.ethereum.graphql.internal.methods;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import tech.pegasys.pantheon.config.GenesisConfigOptions;
import tech.pegasys.pantheon.ethereum.blockcreation.MiningCoordinator;
import tech.pegasys.pantheon.ethereum.chain.Blockchain;
import tech.pegasys.pantheon.ethereum.core.PrivacyParameters;
import tech.pegasys.pantheon.ethereum.core.Synchronizer;
import tech.pegasys.pantheon.ethereum.eth.transactions.TransactionPool;
import tech.pegasys.pantheon.ethereum.graphql.GraphQLRpcConfiguration;
import tech.pegasys.pantheon.ethereum.graphql.internal.queries.BlockchainQueries;
import tech.pegasys.pantheon.ethereum.jsonrpc.RpcApi;
import tech.pegasys.pantheon.ethereum.mainnet.ProtocolSchedule;
import tech.pegasys.pantheon.ethereum.p2p.api.P2PNetwork;
import tech.pegasys.pantheon.ethereum.p2p.wire.Capability;
import tech.pegasys.pantheon.ethereum.permissioning.AccountWhitelistController;
import tech.pegasys.pantheon.ethereum.permissioning.NodeLocalConfigPermissioningController;
import tech.pegasys.pantheon.ethereum.worldstate.WorldStateArchive;
import tech.pegasys.pantheon.metrics.MetricsSystem;
import tech.pegasys.pantheon.metrics.prometheus.MetricsConfiguration;

public class GraphQLRpcFetcherFactory {
	
	public Map<String, GraphQLRpcFetcher> fetchers(
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
		            transactionPool,
		            protocolSchedule,
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
	
	public Map<String, GraphQLRpcFetcher> fetchers(
		      final String clientVersion,
		      final int networkId,
		      final GenesisConfigOptions genesisConfigOptions,
		      final P2PNetwork peerNetworkingService,
		      final BlockchainQueries blockchainQueries,
		      final Synchronizer synchronizer,
		      final TransactionPool transactionPool,
		      final ProtocolSchedule<?> protocolSchedule,
		      final MiningCoordinator miningCoordinator,
		      final MetricsSystem metricsSystem,
		      final Set<Capability> supportedCapabilities,
		      final Optional<AccountWhitelistController> accountsWhitelistController,
		      final Optional<NodeLocalConfigPermissioningController> nodeWhitelistController,
		      final Collection<RpcApi> rpcApis,
		      final PrivacyParameters privacyParameters,
		      final GraphQLRpcConfiguration graphQLRpcConfiguration,
		      final MetricsConfiguration metricsConfiguration) {
			final Map<String, GraphQLRpcFetcher> enabledFetchers = new HashMap<>();
			addFetchers(
					enabledFetchers,
					new BlockFetcher(blockchainQueries),
					new BlocksFetcher(blockchainQueries));
			return enabledFetchers;
		  }
	
	private void addFetchers(
		      final Map<String, GraphQLRpcFetcher> fetchers, final GraphQLRpcFetcher... rpcFetchers) {
		    for (final GraphQLRpcFetcher rpcFetcher : rpcFetchers) {
		      fetchers.put(rpcFetcher.getType(), rpcFetcher);
		    }
		  }

}
