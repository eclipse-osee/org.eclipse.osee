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
import org.eclipse.osee.framework.skynet.core.event.EventUtil;
import org.eclipse.osee.framework.skynet.core.event.FrameworkEventUtil;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.IEventFilteredListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.internal.event.EventHandlerLocal;
import org.eclipse.osee.framework.skynet.core.internal.event.Transport;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactEventHandler implements EventHandlerLocal<IArtifactEventListener, ArtifactEvent> {

   @Override
   public void handle(IArtifactEventListener listener, Sender sender, ArtifactEvent event) {
      EventUtil.eventLog(String.format("IEM: processArtsAndRels [%s]", event));

      List<? extends IEventFilter> filters = ((IEventFilteredListener) listener).getEventFilters();
      if (filters != null) {
         for (IEventFilter eventFilter : filters) {
            // If this branch doesn't match, don't pass events through
            if (!eventFilter.isMatch(event.getBranch())) {
               return;
            }
            // Process artifacts and relations only if there were any in this ArtifactEvent
            if (!event.getArtifacts().isEmpty() || !event.getRelations().isEmpty()) {
               boolean matchFound = false;
               // If artifacts are in event and one or more match, pass events through
               if (!event.getArtifacts().isEmpty() && eventFilter.isMatchArtifacts(event.getArtifacts())) {
                  matchFound = true;
               }
               // If relations are in event and one or more artifacts of the relations match, pass events through
               if (!matchFound && !event.getRelations().isEmpty() && eventFilter.isMatchRelationArtifacts(
                  event.getRelations())) {
                  matchFound = true;
               }
               // If no match, don't pass events through
               if (!matchFound) {
                  return;
               }
            }
         }
      }
      listener.handleArtifactEvent(event, sender);
   }

   @Override
   public void send(Transport transport, Sender sender, ArtifactEvent event)  {
      if (transport.isDispatchToLocalAllowed(sender)) {
         transport.sendLocal(sender, event);
      }
      if (sender.isLocal() && !event.isReloadEvent()) {
         transport.sendRemote(FrameworkEventUtil.getRemotePersistEvent(event));
      }
   }

}
