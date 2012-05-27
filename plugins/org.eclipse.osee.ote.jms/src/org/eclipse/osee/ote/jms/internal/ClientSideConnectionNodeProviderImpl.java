/*
 * Created on Nov 4, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.jms.internal;

import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.ote.jms.OteServerJmsNodeProvider;


public final class ClientSideConnectionNodeProviderImpl implements OteServerJmsNodeProvider{

	private final ConnectionNode node;

	ClientSideConnectionNodeProviderImpl(ConnectionNode node) {
		this.node = node;
	}

	@Override
	public ConnectionNode getConnectionNode() {
		return node;
	}
}
