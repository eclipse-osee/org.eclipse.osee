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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IArtifactsPurgedEventListener;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData.ChangeType;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkWidgetDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition.WriteType;

/**
 * Common cache storage for ATS configuration artifacts:<br>
 * TeamDefinitionArtifact<br>
 * VersionArtifact<br>
 * ActionableItemArtifact<br>
 * All other artifact types will silently not cached
 * 
 * @author Donald G. Dunne
 */
public class AtsCacheManager implements IArtifactsPurgedEventListener, IFrameworkTransactionEventListener {

   public static List<String> CommonCachedArtifacts = new ArrayList<String>();

   public static void start() {
      new AtsCacheManager();
   }

   private AtsCacheManager() {
      OseeEventManager.addListener(this);
   }

   public static List<Artifact> getArtifactsByName(IArtifactType artifactType, String name) throws OseeCoreException {
      AtsBulkLoadCache.run(true);
      return ArtifactCache.getArtifactsByName(artifactType, name);
   }

   public static ActionableItemArtifact getActionableItemByGuid(String guid) throws OseeCoreException {
      AtsBulkLoadCache.run(true);
      return (ActionableItemArtifact) ArtifactCache.getActive(guid, AtsUtil.getAtsBranch().getId());
   }

   public static TeamDefinitionArtifact getTeamDefinitionArtifact(String guid) throws OseeCoreException {
      AtsBulkLoadCache.run(true);
      return (TeamDefinitionArtifact) ArtifactCache.getActive(guid, AtsUtil.getAtsBranch().getId());
   }

   public static List<Artifact> getArtifactsByActive(ArtifactType artifactType, Active active) throws OseeCoreException {
      AtsBulkLoadCache.run(true);
      return ArtifactCache.getArtifactsByType(artifactType, active);
   }

   public static Artifact getSoleArtifactByName(IArtifactType artifactType, String name) throws OseeCoreException {
      AtsBulkLoadCache.run(true);
      List<Artifact> arts = ArtifactCache.getArtifactsByName(artifactType, name);
      if (arts.size() == 1) {
         return arts.iterator().next();
      }
      return null;
   }

   @Override
   public void handleArtifactsPurgedEvent(Sender sender, LoadedArtifacts loadedArtifacts) throws OseeCoreException {
      if (DbUtil.isDbInit()) {
         OseeEventManager.removeListener(this);
         return;
      }
      try {
         for (Artifact artifact : loadedArtifacts.getLoadedArtifacts()) {
            if (artifact.isOfType(WorkRuleDefinition.ARTIFACT_NAME) || artifact.isOfType(WorkPageDefinition.ARTIFACT_NAME) || artifact.isOfType(CoreArtifactTypes.WorkFlowDefinition) || artifact.isOfType(WorkWidgetDefinition.ARTIFACT_NAME)) {
               WorkItemDefinitionFactory.deCache(artifact);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData) throws OseeCoreException {
      if (DbUtil.isDbInit()) {
         OseeEventManager.removeListener(this);
         return;
      }
      if (transData.branchId != AtsUtil.getAtsBranch().getId()) {
         return;
      }
      for (Artifact artifact : transData.cacheDeletedArtifacts) {
         if (artifact.isOfType(WorkRuleDefinition.ARTIFACT_NAME) || artifact.isOfType(WorkPageDefinition.ARTIFACT_NAME) || artifact.isOfType(CoreArtifactTypes.WorkFlowDefinition) || artifact.isOfType(WorkWidgetDefinition.ARTIFACT_NAME)) {
            WorkItemDefinitionFactory.deCache(artifact);
         }
      }
      for (Artifact artifact : transData.cacheAddedArtifacts) {
         if (artifact.isOfType(WorkRuleDefinition.ARTIFACT_NAME)) {
            WorkItemDefinitionFactory.cacheWorkItemDefinitionArtifact(WriteType.Update,
                  new WorkRuleDefinition(artifact), artifact);
         } else if (artifact.isOfType(WorkPageDefinition.ARTIFACT_NAME)) {
            WorkItemDefinitionFactory.cacheWorkItemDefinitionArtifact(WriteType.Update,
                  new WorkPageDefinition(artifact), artifact);
         } else if (artifact.isOfType(WorkWidgetDefinition.ARTIFACT_NAME)) {
            WorkItemDefinitionFactory.cacheWorkItemDefinitionArtifact(WriteType.Update, new WorkWidgetDefinition(
                  artifact), artifact);
         } else if (artifact.isOfType(CoreArtifactTypes.WorkFlowDefinition)) {
            WorkItemDefinitionFactory.cacheWorkItemDefinitionArtifact(WriteType.Update,
                  new WorkFlowDefinition(artifact), artifact);
         }
      }
      for (Artifact artifact : transData.getArtifactsInRelations(ChangeType.All, CoreRelationTypes.WorkItem__Child)) {
         if (artifact.isOfType(WorkRuleDefinition.ARTIFACT_NAME)) {
            WorkItemDefinitionFactory.cacheWorkItemDefinitionArtifact(WriteType.Update,
                  new WorkRuleDefinition(artifact), artifact);
         } else if (artifact.isOfType(WorkPageDefinition.ARTIFACT_NAME)) {
            WorkItemDefinitionFactory.cacheWorkItemDefinitionArtifact(WriteType.Update,
                  new WorkPageDefinition(artifact), artifact);
         } else if (artifact.isOfType(WorkWidgetDefinition.ARTIFACT_NAME)) {
            WorkItemDefinitionFactory.cacheWorkItemDefinitionArtifact(WriteType.Update, new WorkWidgetDefinition(
                  artifact), artifact);
         } else if (artifact.isOfType(CoreArtifactTypes.WorkFlowDefinition)) {
            WorkItemDefinitionFactory.cacheWorkItemDefinitionArtifact(WriteType.Update,
                  new WorkFlowDefinition(artifact), artifact);
         }
      }
   }
}
