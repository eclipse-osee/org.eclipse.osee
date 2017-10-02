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
package org.eclipse.osee.framework.skynet.core.internal.event.handlers;

import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.event.EventUtil;
import org.eclipse.osee.framework.skynet.core.event.FrameworkEventUtil;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.IEventFilteredListener;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.internal.event.EventHandlerLocal;
import org.eclipse.osee.framework.skynet.core.internal.event.Transport;

/**
 * @author Roberto E. Escobar
 */
public class BranchEventHandler implements EventHandlerLocal<IBranchEventListener, BranchEvent> {

   @Override
   public void handle(IBranchEventListener listener, Sender sender, BranchEvent event) {
      List<? extends IEventFilter> filters = ((IEventFilteredListener) listener).getEventFilters();
      if (filters != null) {
         for (IEventFilter eventFilter : filters) {
            if (!eventFilter.isMatch(event.getSourceBranch()) && !eventFilter.isMatch(event.getDestinationBranch())) {
               return;
            }
         }
      }
      if (event.getEventType() == BranchEventType.Added) {
         try {
            BranchManager.checkAndReload(event.getSourceBranch());
         } catch (OseeCoreException ex) {
            EventUtil.eventLog("IEM: updateBranches", ex);
         }
      }
      // Call listener if we matched all of the filters
      listener.handleBranchEvent(sender, event);
   }

   @Override
   public void send(Transport transport, Sender sender, BranchEvent event)  {
      if (transport.isDispatchToLocalAllowed(sender)) {
         transport.sendLocal(sender, event);
      }

      BranchEventType branchEventType = event.getEventType();
      if (sender.isLocal() && branchEventType.isRemoteEventType()) {
         transport.sendRemote(FrameworkEventUtil.getRemoteBranchEvent(event));
      }
   }
}
