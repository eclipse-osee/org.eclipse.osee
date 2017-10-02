/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.messaging.internal;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.ConnectionNodeFactory;
import org.eclipse.osee.framework.messaging.MessageService;
import org.eclipse.osee.framework.messaging.NodeInfo;
import org.eclipse.osee.framework.messaging.internal.activemq.ConnectionNodeFactoryImpl;

/**
 * @author Roberto E. Escobar
 */
public class MessageServiceProxy implements MessageService {

   private MessageServiceImpl messageService;
   private ExecutorService executor;

   public void start() {
      executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

      String sourceId = Integer.toString(hashCode());
      ConnectionNodeFactory factory = new ConnectionNodeFactoryImpl("1.0", sourceId, executor);

      messageService = new MessageServiceImpl(factory);
   }

   public void stop() {
      executor.shutdown();
      executor = null;

      messageService.stop();
      messageService = null;
   }

   public MessageService getProxiedService() {
      return messageService;
   }

   @Override
   public ConnectionNode getDefault()  {
      return getProxiedService().getDefault();
   }

   @Override
   public ConnectionNode get(NodeInfo nodeInfo)  {
      return getProxiedService().get(nodeInfo);
   }

   @Override
   public Collection<NodeInfo> getAvailableConnections() {
      return getProxiedService().getAvailableConnections();
   }

   @Override
   public int size() {
      return getProxiedService().size();
   }

   @Override
   public boolean isEmpty() {
      return getProxiedService().isEmpty();
   }

}
