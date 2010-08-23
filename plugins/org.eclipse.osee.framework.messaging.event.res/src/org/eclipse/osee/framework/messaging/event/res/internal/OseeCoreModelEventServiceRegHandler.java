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
package org.eclipse.osee.framework.messaging.event.res.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.MessageService;
import org.eclipse.osee.framework.messaging.event.res.IOseeCoreModelEventService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Roberto E. Escobar
 */
public class OseeCoreModelEventServiceRegHandler extends AbstractTrackingHandler {

   private static final Class<?>[] DEPENDENCIES = new Class<?>[] {MessageService.class};

   private final Map<ResMessages, Boolean> eventMessageConfig = new HashMap<ResMessages, Boolean>();
   private ServiceRegistration registration;

   public OseeCoreModelEventServiceRegHandler() {
      eventMessageConfig.put(ResMessages.RemoteAccessControlEvent1, Boolean.TRUE);
      eventMessageConfig.put(ResMessages.RemoteBranchEvent1, Boolean.TRUE);
      eventMessageConfig.put(ResMessages.RemoteBroadcastEvent1, Boolean.TRUE);
      eventMessageConfig.put(ResMessages.RemotePersistEvent1, Boolean.FALSE);
      eventMessageConfig.put(ResMessages.RemoteTransactionEvent1, Boolean.TRUE);
   }

   @Override
   public Class<?>[] getDependencies() {
      return DEPENDENCIES;
   }

   @Override
   public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
      MessageService messageService = getService(MessageService.class, services);
      try {
         ConnectionNode connectionNode = messageService.getDefault();
         IOseeCoreModelEventService service = new OseeCoreModelEventServiceImpl(connectionNode, eventMessageConfig);
         registration = context.registerService(IOseeCoreModelEventService.class.getName(), service, null);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void onDeActivate() {
      if (registration != null) {
         registration.unregister();
      }
   }

}
