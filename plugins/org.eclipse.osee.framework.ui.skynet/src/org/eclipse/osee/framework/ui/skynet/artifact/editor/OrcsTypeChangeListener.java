/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.framework.ui.skynet.artifact.editor;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.skynet.core.event.filter.ArtifactTypeEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.BranchIdEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactTopicEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactTopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventTopicArtifactTransfer;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.topic.event.filter.ArtifactTopicTypeEventFilter;
import org.eclipse.osee.framework.skynet.core.topic.event.filter.BranchIdTopicEventFilter;
import org.eclipse.osee.framework.skynet.core.topic.event.filter.ITopicEventFilter;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class OrcsTypeChangeListener implements IArtifactEventListener, IArtifactTopicEventListener {

   List<IEventFilter> filters;
   List<ITopicEventFilter> topicFilters;

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      if (filters == null) {
         filters = new LinkedList<>();
         filters.add(new ArtifactTypeEventFilter(CoreArtifactTypes.OseeTypeDefinition));
         filters.add(new BranchIdEventFilter(CoreBranches.COMMON));
      }
      return filters;
   }

   @Override
   public List<? extends ITopicEventFilter> getTopicEventFilters() {
      if (topicFilters == null) {
         topicFilters = new LinkedList<>();
         topicFilters.add(new ArtifactTopicTypeEventFilter(CoreArtifactTypes.OseeTypeDefinition));
         topicFilters.add(new BranchIdTopicEventFilter(CoreBranches.COMMON));
      }
      return topicFilters;
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      if (sender.isRemote() || !artifactEvent.isOnBranch(CoreBranches.COMMON)) {
         return;
      }
      boolean found = false;
      for (EventBasicGuidArtifact art : artifactEvent.getArtifacts()) {
         if (art.isTypeEqual(CoreArtifactTypes.OseeTypeDefinition)) {
            found = true;
            break;
         }
      }
      if (!found) {
         return;
      }
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            boolean reload = true;
            if (!OseeProperties.isInTest()) {
               reload = MessageDialog.openConfirm(Displays.getActiveShell(), "Reload Server Types Cache",
                  "OSEE has detected a change to the ORCS Types.\n\nWould you like to notify the server to reload types cache?");
            }
         }
      });
   }

   @Override
   public void handleArtifactTopicEvent(ArtifactTopicEvent artifactTopicEvent, Sender sender) {
      if (sender.isRemote() || !artifactTopicEvent.isOnBranch(CoreBranches.COMMON)) {
         return;
      }
      boolean found = false;
      for (EventTopicArtifactTransfer art : artifactTopicEvent.getArtifacts()) {
         if (art.getArtifactTypeId().equals((CoreArtifactTypes.OseeTypeDefinition.getId()))) {
            found = true;
            break;
         }
      }
      if (!found) {
         return;
      }
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            boolean reload = true;
            if (!OseeProperties.isInTest()) {
               reload = MessageDialog.openConfirm(Displays.getActiveShell(), "Reload Server Types Cache",
                  "OSEE has detected a change to the ORCS Types.\n\nWould you like to notify the server to reload types cache?");
            }
         }
      });
   }
}