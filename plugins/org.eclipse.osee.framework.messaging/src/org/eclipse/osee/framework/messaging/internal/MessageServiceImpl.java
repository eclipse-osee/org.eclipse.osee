/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.messaging.internal;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.ConnectionNodeFactory;
import org.eclipse.osee.framework.messaging.MessageService;
import org.eclipse.osee.framework.messaging.NodeInfo;

/**
 * @author Roberto E. Escobar
 */
public class MessageServiceImpl implements MessageService {
   private final NodeInfo defaultNode;
   private final Map<NodeInfo, ConnectionNode> connectionNodes;
   private final ConnectionNodeFactory factory;

   public MessageServiceImpl(ConnectionNodeFactory factory) {
      this.connectionNodes = new ConcurrentHashMap<>();
      this.factory = factory;
      defaultNode = createDefaultNode();
   }

   /**
    * Builds the default connection node from the {@code osee.default.broker.uri} system property.
    * Returns {@code null} when no broker URI is configured (or it is malformed): OSEE connects to
    * an EXTERNAL ActiveMQ broker and never hosts one. Without a URI there is no broker to reach, so
    * {@link #getDefault()} yields {@code null} and messaging degrades gracefully (real-time cross
    * client/server events are disabled; local operation is unaffected). Historically this fell back
    * to an in-VM {@code vm://} broker, which silently embedded a broker in the client/server JVM --
    * that behavior has been removed. Demo/dev embed a broker by explicitly setting a {@code vm:} URI.
    */
   private NodeInfo createDefaultNode() {
      String uri = OseeProperties.getOseeDefaultBrokerUri();
      if (uri == null || uri.trim().isEmpty()) {
         OseeLog.log(MessageServiceImpl.class, Level.INFO,
            "No osee.default.broker.uri configured -- ActiveMQ messaging disabled (no remote events).");
         return null;
      }
      try {
         return new NodeInfo("osee-jms", new URI(uri));
      } catch (URISyntaxException ex) {
         OseeLog.logf(MessageServiceImpl.class, Level.SEVERE,
            "Invalid osee.default.broker.uri [%s] -- ActiveMQ messaging disabled: %s", uri, ex.getMessage());
         return null;
      }
   }

   @Override
   public ConnectionNode getDefault() {
      if (defaultNode == null) {
         return null;
      }
      return get(defaultNode);
   }

   @Override
   public Collection<NodeInfo> getAvailableConnections() {
      return new ArrayList<>(connectionNodes.keySet());
   }

   @Override
   public int size() {
      return connectionNodes.size();
   }

   @Override
   public boolean isEmpty() {
      return connectionNodes.isEmpty();
   }

   @Override
   public ConnectionNode get(NodeInfo nodeInfo) {
      ConnectionNode node = connectionNodes.get(nodeInfo);
      if (node == null) {
         OseeLog.logf(Activator.class, Level.FINEST, "going to create a new Connection Node for [%s]",
            nodeInfo.toString());
         node = factory.create(nodeInfo);
         connectionNodes.put(nodeInfo, node);
         OseeLog.logf(Activator.class, Level.FINE, "Created a new Connection Node for [%s]", nodeInfo.toString());
      }
      return node;
   }

   void stop() {
      for (ConnectionNode node : connectionNodes.values()) {
         node.stop();
      }
   }
}
