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
package org.eclipse.osee.ats.config;

import java.util.Collections;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.AtsFolderUtil;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.AtsFolderUtil.AtsFolder;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;

/**
 * @author Donald G. Dunne
 */
public class AtsBulkLoadCache extends org.eclipse.core.runtime.jobs.Job {

   private AtsBulkLoadCache() {
      super("Bulk Loading ATS Config Artifacts");
   }

   private static AtsBulkLoadCache bulkLoadAtsCacheJob;
   private static boolean atsTypeDataLoadedStarted = false;

   private synchronized static void ensureBulkLoading() {
      if (atsTypeDataLoadedStarted) return;
      atsTypeDataLoadedStarted = true;
      bulkLoadAtsCacheJob = new AtsBulkLoadCache();
      bulkLoadAtsCacheJob.setPriority(Job.SHORT);
      bulkLoadAtsCacheJob.setSystem(true);
      bulkLoadAtsCacheJob.schedule();
   }

   public static void run(boolean forcePend) {
      ensureBulkLoading();
      try {
         if (forcePend) {
            bulkLoadAtsCacheJob.join();
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }

   }

   @Override
   protected IStatus run(IProgressMonitor monitor) {
      OseeLog.log(AtsPlugin.class, Level.INFO, getName());
      try {
         Artifact headingArt = AtsFolderUtil.getFolder(AtsFolder.Ats_Heading);
         // Loading artifacts will cache them in ArtifactCache
         RelationManager.getRelatedArtifacts(Collections.singleton(headingArt), 8,
               CoreRelationTypes.Default_Hierarchical__Child, AtsRelationTypes.TeamDefinitionToVersion_Version);
         // Load Work Definitions
         WorkItemDefinitionFactory.loadDefinitions();
      } catch (Exception ex) {
         return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
      } finally {
         monitor.done();
      }

      return Status.OK_STATUS;
   }
}
