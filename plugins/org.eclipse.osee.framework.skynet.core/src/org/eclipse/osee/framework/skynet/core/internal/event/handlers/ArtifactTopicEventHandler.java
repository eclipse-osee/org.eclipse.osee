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

package org.eclipse.osee.framework.skynet.core.internal.event.handlers;

import java.util.List;
import org.eclipse.osee.framework.core.event.EventUtil;
import org.eclipse.osee.framework.skynet.core.event.FrameworkEventUtil;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactTopicEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.ITopicEventFilteredListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactTopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.internal.event.EventHandlerLocal;
import org.eclipse.osee.framework.skynet.core.internal.event.Transport;
import org.eclipse.osee.framework.skynet.core.topic.event.filter.ITopicEventFilter;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTopicEventHandler implements EventHandlerLocal<IArtifactTopicEventListener, ArtifactTopicEvent> {

   @Override
   public void handle(IArtifactTopicEventListener listener, Sender sender, ArtifactTopicEvent event) {
      EventUtil.eventLog(String.format("IEM: processArtsAndRels [%s]", event));

      List<? extends ITopicEventFilter> filters = ((ITopicEventFilteredListener) listener).getTopicEventFilters();
      if (filters != null) {
         for (ITopicEventFilter eventFilter : filters) {
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
      listener.handleArtifactTopicEvent(event, sender);
   }

   @Override
   public void send(Transport transport, Sender sender, ArtifactTopicEvent event) {
      if (transport.isDispatchToLocalAllowed(sender)) {
         transport.sendLocal(sender, event);
      }
      if (sender.isLocal() && !event.isReloadEvent()) {
         transport.sendRemote(FrameworkEventUtil.getRemotePersistTopicEvent(event));
      }
   }

}
