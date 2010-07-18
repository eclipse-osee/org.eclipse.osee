/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
 * @author Roberto E. Escobar
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
		OseeLog.log(Activator.class, Level.FINER, String.format("Default URI for message Service [%s]", defaultURI.toASCIIString()));
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
         OseeLog.log(Activator.class, Level.FINEST, String.format("going to create a new Connection Node for [%s]", nodeInfo.toString()));
         node = factory.create(nodeInfo);
         connectionNodes.put(nodeInfo, (ConnectionNode)node);
         OseeLog.log(Activator.class, Level.FINE, String.format("Created a new Connection Node for [%s]", nodeInfo.toString()));
      }
      return node;
   }

	void stop() {
		for(ConnectionNode node:connectionNodes.values()){
			node.stop();
		}
	}
}
