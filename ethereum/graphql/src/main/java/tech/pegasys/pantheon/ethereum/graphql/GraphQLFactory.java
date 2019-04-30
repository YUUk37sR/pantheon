package tech.pegasys.pantheon.ethereum.graphql;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;
import tech.pegasys.pantheon.config.GenesisConfigOptions;
import tech.pegasys.pantheon.ethereum.blockcreation.MiningCoordinator;
import tech.pegasys.pantheon.ethereum.chain.Blockchain;
import tech.pegasys.pantheon.ethereum.core.PrivacyParameters;
import tech.pegasys.pantheon.ethereum.core.Synchronizer;
import tech.pegasys.pantheon.ethereum.eth.transactions.TransactionPool;
import tech.pegasys.pantheon.ethereum.graphql.internal.Scalars;
import tech.pegasys.pantheon.ethereum.graphql.internal.types.Query;
import tech.pegasys.pantheon.ethereum.graphql.GraphQLRpcConfiguration;
import tech.pegasys.pantheon.ethereum.graphql.RpcApi;
import tech.pegasys.pantheon.ethereum.graphql.internal.filter.FilterManager;
import tech.pegasys.pantheon.ethereum.graphql.internal.methods.BlockDataFetcher;
import tech.pegasys.pantheon.ethereum.graphql.internal.methods.BlockListFetcher;
import tech.pegasys.pantheon.ethereum.graphql.internal.queries.BlockchainQueries;
import tech.pegasys.pantheon.ethereum.mainnet.ProtocolSchedule;
import tech.pegasys.pantheon.ethereum.p2p.api.P2PNetwork;
import tech.pegasys.pantheon.ethereum.p2p.wire.Capability;
import tech.pegasys.pantheon.ethereum.permissioning.AccountWhitelistController;
import tech.pegasys.pantheon.ethereum.permissioning.NodeLocalConfigPermissioningController;
import tech.pegasys.pantheon.ethereum.worldstate.WorldStateArchive;
import tech.pegasys.pantheon.metrics.MetricsSystem;
import tech.pegasys.pantheon.metrics.prometheus.MetricsConfiguration;
import tech.pegasys.pantheon.ethereum.graphql.internal.results.BlockResultFactory;

public class GraphQLFactory {

  private final BlockResultFactory blockResult = new BlockResultFactory();

  public GraphQL graphQL(
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
		    return graphQL(
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
  
   public GraphQL graphQL(
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
	  
	  final Map<String, DataFetcher> fetchers = new HashMap<>();
	  final Map<String, TypeRuntimeWiring> typeWirings = new HashMap<>();
	  fetchers.put("block",new BlockDataFetcher(blockchainQueries));
	  fetchers.put("blocks",new BlockListFetcher(blockchainQueries));
	  typeWirings.put("Query", new Query(fetchers).getWiring());
	  SchemaParser schemaParser = new SchemaParser();
      SchemaGenerator schemaGenerator = new SchemaGenerator();

      File schemaFile = new File(GraphQLFactory.class.getResource("schema.graphqls").getFile());	
      TypeDefinitionRegistry typeRegistry = schemaParser.parse(schemaFile);
      RuntimeWiring wiring = buildRuntimeWiring(typeWirings);
      GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, wiring);
      return GraphQL.newGraphQL(graphQLSchema).build();
  }
  
  private static RuntimeWiring buildRuntimeWiring(Map<String, TypeRuntimeWiring> typeWirings) {
	  return RuntimeWiring.newRuntimeWiring()
			 .scalar(Scalars.Address)
			 .scalar(Scalars.Bytes)
			 .scalar(Scalars.Bytes32)
			 .scalar(Scalars.BigInt)
			 .scalar(Scalars.Long)
			 .type(typeWirings.get("Query"))
			 .build();
  }

}
