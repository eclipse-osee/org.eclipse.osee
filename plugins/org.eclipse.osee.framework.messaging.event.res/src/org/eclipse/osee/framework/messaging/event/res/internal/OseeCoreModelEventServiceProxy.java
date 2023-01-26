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

package org.eclipse.osee.framework.messaging.event.res.internal;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.messaging.ConnectionListener;
import org.eclipse.osee.framework.messaging.MessageService;
import org.eclipse.osee.framework.messaging.event.res.IFrameworkEventListener;
import org.eclipse.osee.framework.messaging.event.res.IOseeCoreModelEventService;
import org.eclipse.osee.framework.messaging.event.res.RemoteEvent;

/**
 * @author Roberto E. Escobar
 */
public class OseeCoreModelEventServiceProxy implements IOseeCoreModelEventService {

   private MessageService messageService;
   private IOseeCoreModelEventService proxiedService;

   public void setMessageService(MessageService messageService) {
      this.messageService = messageService;
   }

   private IOseeCoreModelEventService getProxiedService() {
      return proxiedService;
   }

   public void start() {
      Map<ResMessages, Boolean> eventMessageConfig = new HashMap<>();

      eventMessageConfig.put(ResMessages.RemoteBranchEvent1, Boolean.TRUE);
      eventMessageConfig.put(ResMessages.RemoteBroadcastEvent1, Boolean.TRUE);
      eventMessageConfig.put(ResMessages.RemotePersistEvent1, Boolean.FALSE);
      eventMessageConfig.put(ResMessages.RemoteTopicEvent1, Boolean.FALSE);
      eventMessageConfig.put(ResMessages.RemoteTransactionEvent1, Boolean.TRUE);
      eventMessageConfig.put(ResMessages.RemoteTopicArtifactEvent, Boolean.TRUE);
      // the boolean value in the eventMessageConfig tells whether it is verbose - i.e. it logs when it is received

      proxiedService = new OseeCoreModelEventServiceImpl(messageService, eventMessageConfig);
   }

   public void stop() {
      proxiedService = null;
   }

   private void checkInitialized() {
      Conditions.checkNotNull(getProxiedService(),
         "IOseeCoreModelEventService was not initialized correctly. Make sure start() was called");
   }

   @Override
   public void addFrameworkListener(IFrameworkEventListener frameworkEventListener) {
      checkInitialized();
      getProxiedService().addFrameworkListener(frameworkEventListener);
   }

   @Override
   public void removeFrameworkListener(IFrameworkEventListener frameworkEventListener) {
      checkInitialized();
      getProxiedService().removeFrameworkListener(frameworkEventListener);
   }

   @Override
   public void addConnectionListener(ConnectionListener connectionListener) {
      checkInitialized();
      getProxiedService().addConnectionListener(connectionListener);
   }

   @Override
   public void removeConnectionListener(ConnectionListener connectionListener) {
      checkInitialized();
      getProxiedService().removeConnectionListener(connectionListener);
   }

   @Override
   public void sendRemoteEvent(RemoteEvent remoteEvent) {
      checkInitialized();
      getProxiedService().sendRemoteEvent(remoteEvent);
   }

}
