package bayes_network.builders;

import java.util.ArrayList;

import bayes_network.BayesianNetwork;
import bayes_network.BNNode;
import bayes_network.cpd.CPDTree;
import bayes_network.cpd.CPDTreeBuilder;
import data.DataSet;
import data.attribute.Attribute;

/**
 * Constructs a {@code BayesianNetwork} object
 * 
 * @author Matthew Bernstein - matthewb@cs.wisc.eud
 *
 */
abstract class NetworkBuilder 
{
    /**
     * The Laplace count used when generating all parameters in the network
     */
    private Integer laplaceCount;

    /**
     * Builds a new Bayesian network given a dataset.
     * 
     * @param data the data set used to construct the network
     * @param laplaceCount the Laplace count used when generating all
     * parameters in the network
     * @return a constructed Bayesian network
     */
    public BayesianNetwork buildNetwork(DataSet data, Integer laplaceCount)
    {
        // Set laplace count
        this.laplaceCount = laplaceCount;

        BayesianNetwork net = new BayesianNetwork();

        /*
         *  Create a node corresponding to each nominal attribute in the 
         *  dataset. Continuous attributes are ignored.
         */
        for (Attribute attr : data.getAttributeList())
        {
            if (attr.getType() == Attribute.NOMINAL)
            {
                BNNode newNode = new BNNode(attr);
                net.addNode( newNode );
            }
        }

        return net;
    }

    /**
     * Builds the conditional probability table for every node in the network.
     * 
     * @param net a network whose structure has already been inferred and
     * constructed
     * @param data the data set used for building each CPD tree
     */
    public void buildCPD(BayesianNetwork net, DataSet data)
    {
        /*
         * For each node in the network.  Find its parents and build a CPD
         * tree object for this node.
         */
        for (BNNode node : net.getNodes())
        {
            ArrayList<Attribute> cpdAttributes = new ArrayList<Attribute>();

            // Get parent's associated attribute
            for (BNNode parent : node.getParents())
            {
                cpdAttributes.add(parent.getAttribute());
            }

            // Add the current node's attribute
            cpdAttributes.add(node.getAttribute());

            // Build the CPD at this node
            CPDTreeBuilder treeBuilder = new CPDTreeBuilder();
            CPDTree cpdTree = treeBuilder.buildCPDTree(data, 
                    cpdAttributes,
                    this.laplaceCount);
            // Set the CPD Tree
            node.setCPDTree( cpdTree );
        }
    }
}
