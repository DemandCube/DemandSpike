


public class StartCluster {
	static DemandSpikeClusterBuilder clusterBuilder;
	public static void main(String[] args) throws Exception {
		clusterBuilder = new DemandSpikeClusterBuilder();
		clusterBuilder.start();
		clusterBuilder.install();
	}
}
