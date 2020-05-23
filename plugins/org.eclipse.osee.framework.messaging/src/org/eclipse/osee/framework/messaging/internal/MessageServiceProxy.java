/*********************************************************************
 * Copyright (c) 2012 Boeing
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

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
   public ConnectionNode getDefault() {
      return getProxiedService().getDefault();
   }

   @Override
   public ConnectionNode get(NodeInfo nodeInfo) {
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
