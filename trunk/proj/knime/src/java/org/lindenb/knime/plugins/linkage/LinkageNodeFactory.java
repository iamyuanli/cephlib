package org.lindenb.knime.plugins.linkage;

import org.knime.core.node.NodeDialogPane;
import org.lindenb.knime.plugins.MyNodeFactory;

/**
 * <code>NodeFactory</code> for the "JSAppend" Node.
 * Append a Value from Javascript
 *

 */
public class LinkageNodeFactory 
        extends MyNodeFactory<LinkageNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public LinkageNodeModel createNodeModel() {
        return new LinkageNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
        return new LinkageNodeDialog();
    }

}

