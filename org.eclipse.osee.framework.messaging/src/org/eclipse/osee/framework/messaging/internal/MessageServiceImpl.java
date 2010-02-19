/*
 * Created on Jan 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.ConnectionNodeFactory;
import org.eclipse.osee.framework.messaging.MessageService;
import org.eclipse.osee.framework.messaging.NodeInfo;

/**
 * @author b1122182
 */
public class MessageServiceImpl implements MessageService {
   private static final String VM_URI = "vm://localhost?broker.persistent=false";
	
   private final NodeInfo defaultNode;
   private final Map<NodeInfo, ConnectionNode> connectionNodes;
   private final ConnectionNodeFactory factory;

   public MessageServiceImpl(ConnectionNodeFactory factory) {
      this.connectionNodes = new ConcurrentHashMap<NodeInfo, ConnectionNode>();
      this.factory = factory;
      defaultNode = new NodeInfo("osee-jms", getDefaultURI());
   }
   
	private URI getDefaultURI() {
		URI defaultURI = null;
		String uri = System.getProperty("osee.broker.primary.uri");
		if (uri == null) {
			uri = VM_URI;
		}
		try {
			defaultURI = new URI(uri);
		} catch (URISyntaxException ex) {
			try {
				defaultURI = new URI(VM_URI);
			} catch (URISyntaxException ex1) {
				OseeLog.log(MessageServiceImpl.class, Level.SEVERE, ex1);
			}
		}
		return defaultURI;
	}

	@Override
	public ConnectionNode getDefault() throws OseeCoreException {
		return get(defaultNode);
	}
   
   public Collection<NodeInfo> getAvailableConnections() {
      return new ArrayList<NodeInfo>(connectionNodes.keySet());
   }

   public int size() {
      return connectionNodes.size();
   }

   public boolean isEmpty() {
      return connectionNodes.isEmpty();
   }

   public ConnectionNode get(NodeInfo nodeInfo) throws OseeCoreException {
      ConnectionNode node = connectionNodes.get(nodeInfo);
      if (node == null) {
         node = factory.create(nodeInfo);
         connectionNodes.put(nodeInfo, (ConnectionNode)node);
      }
      return node;
   }

	void stop() {
		for(ConnectionNode node:connectionNodes.values()){
			node.stop();
		}
	}
}
