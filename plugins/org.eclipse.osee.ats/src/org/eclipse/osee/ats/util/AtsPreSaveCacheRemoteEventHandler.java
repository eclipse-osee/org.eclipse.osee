/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event2.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventModType;
import org.eclipse.osee.framework.skynet.core.event2.artifact.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event2.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;

/**
 * This class handles updating ATS state machine artifacts based on remote events that change the assignees. Without
 * this, the client will think it changed the assignees if the artifact is saved after the remote modified event.<br>
 * <REM2>
 * 
 * @author Donald G. Dunne
 */
public class AtsPreSaveCacheRemoteEventHandler implements IArtifactEventListener, IFrameworkTransactionEventListener {

   private static AtsPreSaveCacheRemoteEventHandler instance = new AtsPreSaveCacheRemoteEventHandler();

   public static AtsPreSaveCacheRemoteEventHandler start() {
      return instance;
   }

   private AtsPreSaveCacheRemoteEventHandler() {
      if (DbUtil.isDbInit()) return;
      OseeLog.log(AtsPlugin.class, Level.INFO, "Starting ATS Pre-Save Remote Event Handler");
      OseeEventManager.addListener(this);
   }

   public void dispose() {
      OseeEventManager.removeListener(this);
   }

   @Override
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData) throws OseeCoreException {
      if (DbUtil.isDbInit()) return;
      if (transData.branchId != AtsUtil.getAtsBranch().getId()) return;
      for (Artifact artifact : transData.cacheChangedArtifacts) {
         if (artifact instanceof StateMachineArtifact) {
            ((StateMachineArtifact) artifact).initalizePreSaveCache();
         }
      }
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return AtsUtil.getAtsObjectEventFilters();
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      if (DbUtil.isDbInit()) return;
      for (Artifact artifact : artifactEvent.getCacheArtifacts(EventModType.Modified, EventModType.Reloaded)) {
         if (artifact instanceof StateMachineArtifact) {
            ((StateMachineArtifact) artifact).initalizePreSaveCache();
         }
      }
   }

}
