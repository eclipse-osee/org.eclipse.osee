package org.eclipse.osee.ote.jms;

import org.eclipse.osee.framework.messaging.ConnectionNode;

public interface OteServerJmsNodeProvider {
	ConnectionNode getConnectionNode();
}
