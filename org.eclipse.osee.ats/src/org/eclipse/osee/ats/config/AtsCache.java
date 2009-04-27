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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IArtifactsPurgedEventListener;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData.ChangeType;
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
public class AtsCache implements IArtifactsPurgedEventListener, IFrameworkTransactionEventListener {

   private final Set<Artifact> cache = new HashSet<Artifact>();
   private final Map<String, ActionableItemArtifact> guidToActionableItem =
         new HashMap<String, ActionableItemArtifact>();
   private final Map<String, TeamDefinitionArtifact> guidToTeamDefinition =
         new HashMap<String, TeamDefinitionArtifact>();
   private static final AtsCache instance = new AtsCache();
   private static List<String> cacheTypes =
         Arrays.asList(ActionableItemArtifact.ARTIFACT_NAME, TeamDefinitionArtifact.ARTIFACT_NAME,
               VersionArtifact.ARTIFACT_NAME);

   public AtsCache() {
      OseeEventManager.addListener(this);
   }

   public static void cache(Artifact artifact) throws OseeCoreException {
      if (cacheTypes.contains(artifact.getArtifactTypeName())) {
         instance.cache.add(artifact);
         if (artifact instanceof TeamDefinitionArtifact) {
            instance.guidToTeamDefinition.put(artifact.getGuid(), (TeamDefinitionArtifact) artifact);
         }
         if (artifact instanceof ActionableItemArtifact) {
            instance.guidToActionableItem.put(artifact.getGuid(), (ActionableItemArtifact) artifact);
         }
      }
   }

   public static void deCache(Artifact artifact) {
      instance.cache.remove(artifact);
   }

   public static ActionableItemArtifact getActionableItemByGuid(String guid) throws OseeCoreException {
      AtsBulkLoadCache.run(true);
      ActionableItemArtifact aia = instance.guidToActionableItem.get(guid);
      if (aia != null) return aia;
      Artifact art = ArtifactQuery.getArtifactFromId(guid, AtsPlugin.getAtsBranch(), false);
      if (art != null) {
         cache(art);
         return (ActionableItemArtifact) art;
      }
      return null;
   }

   public static TeamDefinitionArtifact getTeamDefinitionArtifact(String guid) throws OseeCoreException {
      AtsBulkLoadCache.run(true);
      TeamDefinitionArtifact teamDef = instance.guidToTeamDefinition.get(guid);
      if (teamDef != null) return teamDef;
      Artifact art = ArtifactQuery.getArtifactFromId(guid, AtsPlugin.getAtsBranch(), false);
      if (art != null) {
         cache(art);
         return (TeamDefinitionArtifact) art;
      }
      return null;
   }

   @SuppressWarnings("unchecked")
   public static <A> List<A> getArtifactsByActive(Active active, Class<A> clazz) {
      AtsBulkLoadCache.run(true);
      List<A> arts = new ArrayList<A>();
      for (Artifact art : instance.cache) {
         try {
            if (!art.isDeleted() && art.getClass().isAssignableFrom(clazz) && art.isAttributeTypeValid(ATSAttributes.ACTIVE_ATTRIBUTE.getStoreName()) && art.getSoleAttributeValue(
                  ATSAttributes.ACTIVE_ATTRIBUTE.getStoreName(), false)) {
               arts.add((A) art);
            }
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
      return arts;
   }

   @SuppressWarnings("unchecked")
   public static <A> List<A> getArtifactsByName(String name, Class<A> clazz) {
      AtsBulkLoadCache.run(true);
      List<A> arts = new ArrayList<A>();
      for (Artifact art : instance.cache) {
         if (!art.isDeleted() && art.getClass().isAssignableFrom(clazz) && art.getDescriptiveName().equals(name)) {
            arts.add((A) art);
         }
      }
      return arts;
   }

   public static <A> A getSoleArtifactByName(String name, Class<A> clazz) throws MultipleArtifactsExist, ArtifactDoesNotExist {
      AtsBulkLoadCache.run(true);
      List<A> arts = getArtifactsByName(name, clazz);
      if (arts.size() == 1) {
         return arts.iterator().next();
      }
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.event.IArtifactsPurgedEventListener#handleArtifactsPurgedEvent(org.eclipse.osee.framework.skynet.core.event.Sender, org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts)
    */
   @Override
   public void handleArtifactsPurgedEvent(Sender sender, LoadedArtifacts loadedArtifacts) throws OseeCoreException {
      try {
         for (Artifact artifact : loadedArtifacts.getLoadedArtifacts()) {
            deCache(artifact);
            if (artifact.getArtifactTypeName().equals(WorkRuleDefinition.ARTIFACT_NAME) || artifact.getArtifactTypeName().equals(
                  WorkPageDefinition.ARTIFACT_NAME) || artifact.getArtifactTypeName().equals(
                  WorkFlowDefinition.ARTIFACT_NAME) || artifact.getArtifactTypeName().equals(
                  WorkWidgetDefinition.ARTIFACT_NAME)) {
               WorkItemDefinitionFactory.deCache(artifact);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener#handleFrameworkTransactionEvent(org.eclipse.osee.framework.skynet.core.event.Sender, org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData)
    */
   @Override
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData) throws OseeCoreException {
      if (transData.branchId != AtsPlugin.getAtsBranch().getBranchId()) return;
      for (Artifact artifact : transData.cacheDeletedArtifacts) {
         deCache(artifact);
         if (artifact.getArtifactTypeName().equals(WorkRuleDefinition.ARTIFACT_NAME) || artifact.getArtifactTypeName().equals(
               WorkPageDefinition.ARTIFACT_NAME) || artifact.getArtifactTypeName().equals(
               WorkFlowDefinition.ARTIFACT_NAME) || artifact.getArtifactTypeName().equals(
               WorkWidgetDefinition.ARTIFACT_NAME)) {
            WorkItemDefinitionFactory.deCache(artifact);
         }
      }
      for (Artifact artifact : transData.cacheAddedArtifacts) {
         cache(artifact);
         if (artifact.getArtifactTypeName().equals(WorkRuleDefinition.ARTIFACT_NAME)) {
            WorkItemDefinitionFactory.cacheWorkItemDefinitionArtifact(WriteType.Update,
                  new WorkRuleDefinition(artifact), artifact);
         } else if (artifact.getArtifactTypeName().equals(WorkPageDefinition.ARTIFACT_NAME)) {
            WorkItemDefinitionFactory.cacheWorkItemDefinitionArtifact(WriteType.Update,
                  new WorkPageDefinition(artifact), artifact);
         } else if (artifact.getArtifactTypeName().equals(WorkWidgetDefinition.ARTIFACT_NAME)) {
            WorkItemDefinitionFactory.cacheWorkItemDefinitionArtifact(WriteType.Update, new WorkWidgetDefinition(
                  artifact), artifact);
         } else if (artifact.getArtifactTypeName().equals(WorkFlowDefinition.ARTIFACT_NAME)) {
            WorkItemDefinitionFactory.cacheWorkItemDefinitionArtifact(WriteType.Update,
                  new WorkFlowDefinition(artifact), artifact);
         }
      }
      for (Artifact artifact : transData.getArtifactsInRelations(ChangeType.All,
            AtsRelation.WorkItem__Child.getRelationType())) {
         if (artifact.getArtifactTypeName().equals(WorkRuleDefinition.ARTIFACT_NAME)) {
            WorkItemDefinitionFactory.cacheWorkItemDefinitionArtifact(WriteType.Update,
                  new WorkRuleDefinition(artifact), artifact);
         } else if (artifact.getArtifactTypeName().equals(WorkPageDefinition.ARTIFACT_NAME)) {
            WorkItemDefinitionFactory.cacheWorkItemDefinitionArtifact(WriteType.Update,
                  new WorkPageDefinition(artifact), artifact);
         } else if (artifact.getArtifactTypeName().equals(WorkWidgetDefinition.ARTIFACT_NAME)) {
            WorkItemDefinitionFactory.cacheWorkItemDefinitionArtifact(WriteType.Update, new WorkWidgetDefinition(
                  artifact), artifact);
         } else if (artifact.getArtifactTypeName().equals(WorkFlowDefinition.ARTIFACT_NAME)) {
            WorkItemDefinitionFactory.cacheWorkItemDefinitionArtifact(WriteType.Update,
                  new WorkFlowDefinition(artifact), artifact);
         }
      }
   }
}
