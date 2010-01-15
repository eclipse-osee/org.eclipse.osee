/*
 * Created on Jan 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.messaging.future.ConnectionNode;
import org.eclipse.osee.framework.messaging.future.ConnectionNodeFactory;
import org.eclipse.osee.framework.messaging.future.MessageService;
import org.eclipse.osee.framework.messaging.future.NodeInfo;

/**
 * @author b1122182
 */
public class MessageServiceImpl implements MessageService {

   private final Map<NodeInfo, ConnectionNode> connectionNodes;
   private final ConnectionNodeFactory factory;

   public MessageServiceImpl(ConnectionNodeFactory factory) {
      this.connectionNodes = new ConcurrentHashMap<NodeInfo, ConnectionNode>();
      this.factory = factory;
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
         connectionNodes.put(nodeInfo, node);
      }
      return node;
   }
}
