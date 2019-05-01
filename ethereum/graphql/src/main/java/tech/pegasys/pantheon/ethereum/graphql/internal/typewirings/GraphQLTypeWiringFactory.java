package tech.pegasys.pantheon.ethereum.graphql.internal.typewirings;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import graphql.schema.DataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeRuntimeWiring;
import tech.pegasys.pantheon.config.GenesisConfigOptions;
import tech.pegasys.pantheon.ethereum.blockcreation.MiningCoordinator;
import tech.pegasys.pantheon.ethereum.core.PrivacyParameters;
import tech.pegasys.pantheon.ethereum.core.Synchronizer;
import tech.pegasys.pantheon.ethereum.eth.transactions.TransactionPool;
import tech.pegasys.pantheon.ethereum.graphql.GraphQLRpcConfiguration;
import tech.pegasys.pantheon.ethereum.graphql.RpcApi;
import tech.pegasys.pantheon.ethereum.graphql.internal.Scalars;
import tech.pegasys.pantheon.ethereum.graphql.internal.filter.FilterManager;
import tech.pegasys.pantheon.ethereum.graphql.internal.methods.BlockDataFetcher;
import tech.pegasys.pantheon.ethereum.graphql.internal.methods.BlockListFetcher;
import tech.pegasys.pantheon.ethereum.graphql.internal.queries.BlockchainQueries;
import tech.pegasys.pantheon.ethereum.mainnet.ProtocolSchedule;
import tech.pegasys.pantheon.ethereum.p2p.api.P2PNetwork;
import tech.pegasys.pantheon.ethereum.p2p.wire.Capability;
import tech.pegasys.pantheon.ethereum.permissioning.AccountWhitelistController;
import tech.pegasys.pantheon.ethereum.permissioning.NodeLocalConfigPermissioningController;
import tech.pegasys.pantheon.metrics.MetricsSystem;
import tech.pegasys.pantheon.metrics.prometheus.MetricsConfiguration;

public class GraphQLTypeWiringFactory {
	
	public static RuntimeWiring getRunTimeWiring(
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
	    
	    return RuntimeWiring.newRuntimeWiring()
	            .scalar(Scalars.Address)
	            .scalar(Scalars.Bytes)
	            .scalar(Scalars.Bytes32)
	            .scalar(Scalars.BigInt)
	            .scalar(Scalars.Long)
	            .type(TypeRuntimeWiring.newTypeWiring("Query")
	            	  .dataFetcher("block", new BlockDataFetcher(blockchainQueries))
	            	  .dataFetcher("blocks", new BlockListFetcher(blockchainQueries))
	            	  .build())
	            .type(TypeRuntimeWiring.newTypeWiring("Block")
	            	  .dataFetcher("miner", new AccountDataFetcher(blockchainQueries))
	            	  .build())
	            .build();
	}

}
