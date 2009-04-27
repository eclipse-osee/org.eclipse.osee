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
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition.WriteType;

/**
 * @author Donald G. Dunne
 */
public class WorkItemDefinitionFactory {

   private static Map<String, WorkItemDefinition> itemIdToDefinition;
   private static Map<String, Artifact> itemIdToWidArtifact;

   public static void deCache(WorkItemDefinition workItemDefinition) {
      deCache(workItemDefinition.getId());
   }

   public synchronized static void deCache(String workItemDefinitionId) {
      itemIdToDefinition.remove(workItemDefinitionId);
      itemIdToWidArtifact.remove(workItemDefinitionId);
   }

   public static void deCache(Artifact artifact) {
      String itemId = null;
      if (itemIdToWidArtifact.containsValue(artifact)) {
         for (Entry<String, Artifact> entry : itemIdToWidArtifact.entrySet()) {
            if (entry.getValue().equals(artifact)) {
               deCache(entry.getKey());
               return;
            }
         }
      }
   }

   public synchronized static void loadDefinitions() throws OseeCoreException {
      if (itemIdToDefinition == null) {
         OseeLog.log(SkynetGuiPlugin.class, Level.INFO, "Loading Work Item Definitions");
         itemIdToDefinition = new HashMap<String, WorkItemDefinition>();
         itemIdToWidArtifact = new HashMap<String, Artifact>();

         // Add all work item definitions provided through extension points
         for (IWorkDefinitionProvider provider : WorkDefinitionProvider.getWorkDefinitionProviders()) {
            for (WorkItemDefinition def : provider.getProgramaticWorkItemDefinitions()) {
               addItemDefinition(WriteType.New, def);
            }
         }

         // This load is faster than loading each by artifact type
         Collection<String> artifactTypeNames = new ArrayList<String>(4);
         artifactTypeNames.add(WorkRuleDefinition.ARTIFACT_NAME);
         artifactTypeNames.add(WorkPageDefinition.ARTIFACT_NAME);
         artifactTypeNames.add(WorkFlowDefinition.ARTIFACT_NAME);
         artifactTypeNames.add(WorkWidgetDefinition.ARTIFACT_NAME);
         for (Artifact art : ArtifactQuery.getArtifactsFromTypes(artifactTypeNames, BranchManager.getCommonBranch(),
               false)) {
            if (art.getArtifactTypeName().equals(WorkRuleDefinition.ARTIFACT_NAME)) {
               addItemDefinition(WriteType.New, new WorkRuleDefinition(art), art);
            } else if (art.getArtifactTypeName().equals(WorkWidgetDefinition.ARTIFACT_NAME)) {
               addItemDefinition(WriteType.New, new WorkWidgetDefinition(art), art);
            } else if (art.getArtifactTypeName().equals(WorkPageDefinition.ARTIFACT_NAME)) {
               addItemDefinition(WriteType.New, new WorkPageDefinition(art), art);
            } else if (art.getArtifactTypeName().equals(WorkFlowDefinition.ARTIFACT_NAME)) {
               addItemDefinition(WriteType.New, new WorkFlowDefinition(art), art);
            }
         }
      }
   }

   /**
    * This should only be called on database initialization or when new work item definitions are created during
    * run-time.
    * 
    * @param workItemDefinition
    * @param artifact
    */
   public static void cacheWorkItemDefinitionArtifact(WriteType writeType, WorkItemDefinition workItemDefinition, Artifact artifact) {
      addItemDefinition(writeType, workItemDefinition, artifact);
   }

   public static void relateWorkItemDefinitions(String parentWorkflowId, String childWorkflowId) throws OseeCoreException {
      List<Artifact> parentArts =
            ArtifactQuery.getArtifactsFromAttribute(WorkItemAttributes.WORK_ID.getAttributeTypeName(),
                  parentWorkflowId, BranchManager.getCommonBranch());
      if (parentArts == null || parentArts.size() == 0) {
         throw new IllegalArgumentException("Can't access parentWorkflowId " + parentWorkflowId);
      }
      Artifact parentArt = parentArts.iterator().next();
      List<Artifact> childArts =
            ArtifactQuery.getArtifactsFromAttribute(WorkItemAttributes.WORK_ID.getAttributeTypeName(), childWorkflowId,
                  BranchManager.getCommonBranch());
      if (childArts == null || childArts.size() == 0) {
         throw new IllegalArgumentException("Can't access childWorkflowId " + childWorkflowId);
      }
      Artifact childArt = childArts.iterator().next();
      if (!parentArt.getRelatedArtifacts(CoreRelationEnumeration.WorkItem__Child, Artifact.class).contains(childArt)) {
         parentArt.addRelation(CoreRelationEnumeration.WorkItem__Child, childArt);
         parentArt.persistRelations();
      }
   }

   private static void addItemDefinition(WriteType writeType, WorkItemDefinition workItemDefinition) {
      if (workItemDefinition.getId() == null) throw new IllegalArgumentException("Item Id can't be null");
      if (writeType == WriteType.New && itemIdToDefinition.containsKey(workItemDefinition.getId())) throw new IllegalArgumentException(
            "Item Id must be unique.  Already work item with id \"" + workItemDefinition.getId() + "\"");
      itemIdToDefinition.put(workItemDefinition.getId(), workItemDefinition);
   }

   private static void addItemDefinition(WriteType writeType, WorkItemDefinition workItemDefinition, Artifact artifact) {
      addItemDefinition(writeType, workItemDefinition);
      itemIdToWidArtifact.put(workItemDefinition.id, artifact);
   }

   public static void loadDefinitions(Collection<Artifact> arts) throws OseeCoreException {
      for (Artifact art : arts) {
         if (art.getArtifactTypeName().equals(WorkRuleDefinition.ARTIFACT_NAME)) {
            System.out.println("Updating WorkItemDefinition cache with " + art);
            addItemDefinition(WriteType.New, new WorkRuleDefinition(art), art);
         }
         if (art.getArtifactTypeName().equals(WorkWidgetDefinition.ARTIFACT_NAME)) {
            System.out.println("Updating WorkItemDefinition cache with " + art);
            addItemDefinition(WriteType.New, new WorkWidgetDefinition(art), art);
         }
         if (art.getArtifactTypeName().equals(WorkPageDefinition.ARTIFACT_NAME)) {
            System.out.println("Updating WorkItemDefinition cache with " + art);
            addItemDefinition(WriteType.New, new WorkPageDefinition(art), art);
         }
         if (art.getArtifactTypeName().equals(WorkFlowDefinition.ARTIFACT_NAME)) {
            System.out.println("Updating WorkItemDefinition cache with " + art);
            addItemDefinition(WriteType.New, new WorkFlowDefinition(art), art);
         }
      }
   }

   public static WorkItemDefinition getWorkItemDefinition(String id) throws OseeCoreException {
      if (id == null) throw new IllegalStateException("WorkItemDefinition id can't be null");
      loadDefinitions();
      WorkItemDefinition wid = itemIdToDefinition.get(id);
      if (wid == null) {
         // Attempt to get from DB
         loadDefinitions(ArtifactQuery.getArtifactsFromAttribute(WorkItemAttributes.WORK_ID.getAttributeTypeName(), id,
               BranchManager.getCommonBranch()));
      }
      return itemIdToDefinition.get(id);
   }

   public static Artifact getWorkItemDefinitionArtifact(String id) throws OseeCoreException {
      if (id == null) throw new IllegalStateException("WorkItemDefinition id can't be null");
      loadDefinitions();
      Artifact art = itemIdToWidArtifact.get(id);
      if (art == null) {
         // Attempt to get from DB
         loadDefinitions(ArtifactQuery.getArtifactsFromAttribute(WorkItemAttributes.WORK_ID.getAttributeTypeName(), id,
               BranchManager.getCommonBranch()));
      }
      return itemIdToWidArtifact.get(id);
   }

   public static List<WorkItemDefinition> getWorkItemDefinition(java.util.Collection<String> ids) throws OseeCoreException {
      loadDefinitions();
      List<WorkItemDefinition> defs = new ArrayList<WorkItemDefinition>();
      for (String id : ids) {
         WorkItemDefinition def = getWorkItemDefinition(id);
         if (def == null) throw new IllegalArgumentException("Work Item Id \"" + id + "\" is not a defined work item");
         defs.add(def);
      }
      return defs;
   }

   public static List<WorkItemDefinition> getWorkItemDefinitionsStartsWithId(String id) throws OseeCoreException {
      loadDefinitions();
      List<WorkItemDefinition> defs = new ArrayList<WorkItemDefinition>();
      for (Entry<String, WorkItemDefinition> entry : itemIdToDefinition.entrySet()) {
         if (entry.getKey().startsWith(id)) {
            defs.add(entry.getValue());
         }
      }
      return defs;
   }

   /**
    * Call to get dynamic definitions based on data specified. This is intended for extenders to be able to provide
    * widgets that are either conditionally added or are configured dynamically based on dynamic circumstances
    * 
    * @param data
    * @return list of WorkItemDefinitions
    */
   public static List<WorkItemDefinition> getDynamicWorkItemDefintions(WorkFlowDefinition workFlowDefinition, WorkPageDefinition workPageDefinition, Object data) throws OseeCoreException {
      List<WorkItemDefinition> dynamicDefinitions = new ArrayList<WorkItemDefinition>();
      for (IWorkDefinitionProvider provider : WorkDefinitionProvider.getWorkDefinitionProviders()) {
         dynamicDefinitions.addAll(provider.getDynamicWorkItemDefinitionsForPage(workFlowDefinition,
               workPageDefinition, data));
      }
      return dynamicDefinitions;
   }

   public static List<WorkItemDefinition> getWorkItemDefinitions(Collection<String> pageids) throws OseeCoreException {
      loadDefinitions();
      List<WorkItemDefinition> defs = new ArrayList<WorkItemDefinition>();
      for (String itemId : pageids) {
         WorkItemDefinition def = getWorkItemDefinition(itemId);
         if (def == null) throw new IllegalArgumentException("Item Id \"" + itemId + "\" is not a defined item");
         defs.add(def);
      }
      return defs;
   }

   public static Collection<WorkItemDefinition> getWorkItemDefinitions() throws OseeCoreException {
      loadDefinitions();
      return itemIdToDefinition.values();
   }

}
