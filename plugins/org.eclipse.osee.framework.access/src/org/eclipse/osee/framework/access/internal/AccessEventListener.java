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
package org.eclipse.osee.framework.access.internal;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.access.AccessControlData;
import org.eclipse.osee.framework.access.AccessObject;
import org.eclipse.osee.framework.access.internal.data.ArtifactAccessObject;
import org.eclipse.osee.framework.access.internal.data.BranchAccessObject;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IAccessControlEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.AccessControlEvent;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;

/**
 * @author Donald G. Dunne
 */
public final class AccessEventListener implements IBranchEventListener, IAccessControlEventListener, IArtifactEventListener {

   private final AccessControlService service;

   public AccessEventListener(AccessControlService service) {
      this.service = service;
   }

   private void reload() throws OseeCoreException {
      service.reloadCache();
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      for (EventBasicGuidArtifact guidArt : artifactEvent.getArtifacts()) {
         if (guidArt.is(EventModType.Added) && guidArt.is(CoreArtifactTypes.User)) {
            try {
               reload();
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
         if (guidArt.is(EventModType.Purged)) {
            try {
               Artifact cacheArt = ArtifactCache.getActive(guidArt);
               if (cacheArt != null) {
                  ArtifactAccessObject artifactAccessObject = ArtifactAccessObject.getArtifactAccessObject(cacheArt);
                  updateAccessList(sender, artifactAccessObject);
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }

         }
      }
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return null;
   }

   @Override
   public void handleBranchEvent(Sender sender, final BranchEvent branchEvent) {
      try {
         if (branchEvent.getEventType() == BranchEventType.Deleted || sender.isLocal() && branchEvent.getEventType() == BranchEventType.Purged) {
            BranchAccessObject branchAccessObject =
               BranchAccessObject.getBranchAccessObject(branchEvent.getBranchGuid());
            updateAccessList(sender, branchAccessObject);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void updateAccessList(Sender sender, AccessObject accessObject) throws OseeCoreException {
      List<AccessControlData> acl = service.generateAccessControlList(accessObject);
      for (AccessControlData accessControlData : acl) {
         service.removeAccessControlDataIf(sender.isLocal(), accessControlData);
      }
   }

   @Override
   public void handleAccessControlArtifactsEvent(Sender sender, AccessControlEvent accessControlEvent) {
      try {
         // local is handled by operations against cache
         if (sender.isRemote()) {
            service.reloadCache();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }
}