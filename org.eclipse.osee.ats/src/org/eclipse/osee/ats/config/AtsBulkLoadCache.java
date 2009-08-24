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
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.util.AtsFolderUtil;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.AtsFolderUtil.AtsFolder;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkWidgetDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsBulkLoadCache extends org.eclipse.core.runtime.jobs.Job {

   private AtsBulkLoadCache() {
      super("Bulk Loading ATS Config Artifacts");
      try {
         ArtifactCache.registerEternalArtifactType(ArtifactTypeManager.getType(VersionArtifact.ARTIFACT_NAME));
         ArtifactCache.registerEternalArtifactType(ArtifactTypeManager.getType(TeamDefinitionArtifact.ARTIFACT_NAME));
         ArtifactCache.registerEternalArtifactType(ArtifactTypeManager.getType(ActionableItemArtifact.ARTIFACT_NAME));
         ArtifactCache.registerEternalArtifactType(ArtifactTypeManager.getType(WorkRuleDefinition.ARTIFACT_NAME));
         ArtifactCache.registerEternalArtifactType(ArtifactTypeManager.getType(WorkFlowDefinition.ARTIFACT_NAME));
         ArtifactCache.registerEternalArtifactType(ArtifactTypeManager.getType(WorkWidgetDefinition.ARTIFACT_NAME));
         ArtifactCache.registerEternalArtifactType(ArtifactTypeManager.getType(WorkPageDefinition.ARTIFACT_NAME));
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }
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
               CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, AtsRelation.TeamDefinitionToVersion_Version);
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
