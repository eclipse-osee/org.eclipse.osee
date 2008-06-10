/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;

/**
 * @author Donald G. Dunne
 */
public class WorkItemDefinitionFactory {

   private static Map<String, WorkItemDefinition> itemIdToDefinition;
   private static Map<String, Artifact> itemIdToWidArtifact;

   public static void clearCache() {
      itemIdToDefinition = null;
      itemIdToWidArtifact = null;
   }

   private synchronized static void loadDefinitions() throws Exception {
      if (itemIdToDefinition == null) {
         itemIdToDefinition = new HashMap<String, WorkItemDefinition>();
         itemIdToWidArtifact = new HashMap<String, Artifact>();

         // Add all work item definitions provided through extension points
         for (IWorkDefinitionProvider provider : WorkDefinitionProvider.getWorkDefinitionProviders()) {
            for (WorkItemDefinition def : provider.getProgramaticWorkItemDefinitions()) {
               addItemDefinition(def);
            }
         }

         for (Artifact art : ArtifactQuery.getArtifactsFromType(WorkRuleDefinition.ARTIFACT_NAME,
               BranchPersistenceManager.getCommonBranch())) {
            addItemDefinition(new WorkRuleDefinition(art), art);
         }
         for (Artifact art : ArtifactQuery.getArtifactsFromType(WorkWidgetDefinition.ARTIFACT_NAME,
               BranchPersistenceManager.getCommonBranch())) {
            addItemDefinition(new WorkWidgetDefinition(art), art);
         }
         for (Artifact art : ArtifactQuery.getArtifactsFromType(WorkPageDefinition.ARTIFACT_NAME,
               BranchPersistenceManager.getCommonBranch())) {
            addItemDefinition(new WorkPageDefinition(art), art);
         }
         for (Artifact art : ArtifactQuery.getArtifactsFromType(WorkFlowDefinition.ARTIFACT_NAME,
               BranchPersistenceManager.getCommonBranch())) {
            addItemDefinition(new WorkFlowDefinition(art), art);
         }
      }
   }

   public static void relateWorkItemDefinitions(String parentWorkflowId, String childWorkflowId) throws SQLException {
      Artifact parentArt =
            ArtifactQuery.getArtifactsFromAttribute(WorkItemAttributes.WORK_ID.getAttributeTypeName(),
                  parentWorkflowId, BranchPersistenceManager.getCommonBranch()).iterator().next();
      if (parentArt == null) throw new IllegalArgumentException("Can't access parentWorkflowId " + parentWorkflowId);
      Artifact childArt =
            ArtifactQuery.getArtifactsFromAttribute(WorkItemAttributes.WORK_ID.getAttributeTypeName(), childWorkflowId,
                  BranchPersistenceManager.getCommonBranch()).iterator().next();
      if (childArt == null) throw new IllegalArgumentException("Can't access childWorkflowId " + childWorkflowId);
      if (!parentArt.getArtifacts(CoreRelationEnumeration.WorkItem__Child, Artifact.class).contains(childArt)) {
         parentArt.addRelation(CoreRelationEnumeration.WorkItem__Child, childArt);
         parentArt.persistRelations();
      }
   }

   private static void addItemDefinition(WorkItemDefinition workItemDefinition) {
      if (workItemDefinition.getId() == null) throw new IllegalArgumentException("Item Id can't be null");
      if (itemIdToDefinition.containsKey(workItemDefinition.getId())) throw new IllegalArgumentException(
            "Item Id must be unique.  Already work item with id \"" + workItemDefinition.getId() + "\"");
      itemIdToDefinition.put(workItemDefinition.getId(), workItemDefinition);
   }

   private static void addItemDefinition(WorkItemDefinition workItemDefinition, Artifact artifact) {
      addItemDefinition(workItemDefinition);
      itemIdToWidArtifact.put(workItemDefinition.id, artifact);
   }

   public static void loadDefinitions(Collection<Artifact> arts) throws Exception {
      for (Artifact art : arts) {
         if (art.getArtifactTypeName().equals(WorkRuleDefinition.ARTIFACT_NAME)) {
            System.out.println("Updating WorkItemDefinition cache with " + art);
            addItemDefinition(new WorkRuleDefinition(art), art);
         }
         if (art.getArtifactTypeName().equals(WorkWidgetDefinition.ARTIFACT_NAME)) {
            System.out.println("Updating WorkItemDefinition cache with " + art);
            addItemDefinition(new WorkWidgetDefinition(art), art);
         }
         if (art.getArtifactTypeName().equals(WorkPageDefinition.ARTIFACT_NAME)) {
            System.out.println("Updating WorkItemDefinition cache with " + art);
            addItemDefinition(new WorkPageDefinition(art), art);
         }
         if (art.getArtifactTypeName().equals(WorkFlowDefinition.ARTIFACT_NAME)) {
            System.out.println("Updating WorkItemDefinition cache with " + art);
            addItemDefinition(new WorkFlowDefinition(art), art);
         }
      }
   }

   public static WorkItemDefinition getWorkItemDefinition(String id) throws Exception {
      if (id == null) throw new IllegalStateException("WorkItemDefinition id can't be null");
      loadDefinitions();
      WorkItemDefinition wid = itemIdToDefinition.get(id);
      if (wid == null) {
         // Attempt to get from DB
         loadDefinitions(ArtifactQuery.getArtifactsFromAttribute(WorkItemAttributes.WORK_ID.getAttributeTypeName(), id,
               BranchPersistenceManager.getAtsBranch()));
      }
      return itemIdToDefinition.get(id);
   }

   public static Artifact getWorkItemDefinitionArtifact(String id) throws Exception {
      if (id == null) throw new IllegalStateException("WorkItemDefinition id can't be null");
      loadDefinitions();
      Artifact art = itemIdToWidArtifact.get(id);
      if (art == null) {
         // Attempt to get from DB
         loadDefinitions(ArtifactQuery.getArtifactsFromAttribute(WorkItemAttributes.WORK_ID.getAttributeTypeName(), id,
               BranchPersistenceManager.getAtsBranch()));
      }
      return itemIdToWidArtifact.get(id);
   }

   public static List<WorkItemDefinition> getWorkItemDefinition(java.util.Collection<String> ids) throws Exception {
      loadDefinitions();
      List<WorkItemDefinition> defs = new ArrayList<WorkItemDefinition>();
      for (String id : ids) {
         WorkItemDefinition def = getWorkItemDefinition(id);
         if (def == null) throw new IllegalArgumentException("Work Item Id \"" + id + "\" is not a defined work item");
         defs.add(def);
      }
      return defs;
   }

   /**
    * Call to get dynamic definitions based on data specified. This is intended for extenders to be able to provide
    * widgets that are either conditionally added or are configured dynamically based on dynamic circumstances
    * 
    * @param data
    * @return
    */
   public static List<WorkItemDefinition> getDynamicWorkItemDefintions(WorkFlowDefinition workFlowDefinition, WorkPageDefinition workPageDefinition, Object data) throws Exception {
      List<WorkItemDefinition> dynamicDefinitions = new ArrayList<WorkItemDefinition>();
      for (IWorkDefinitionProvider provider : WorkDefinitionProvider.getWorkDefinitionProviders()) {
         dynamicDefinitions.addAll(provider.getDynamicWorkItemDefinitionsForPage(workFlowDefinition,
               workPageDefinition, data));
      }
      return dynamicDefinitions;
   }

   public static List<WorkItemDefinition> getWorkItemDefinitions(Collection<String> pageids) throws Exception {
      loadDefinitions();
      List<WorkItemDefinition> defs = new ArrayList<WorkItemDefinition>();
      for (String itemId : pageids) {
         WorkItemDefinition def = getWorkItemDefinition(itemId);
         if (def == null) throw new IllegalArgumentException("Item Id \"" + itemId + "\" is not a defined item");
         defs.add(def);
      }
      return defs;
   }

   public static Collection<WorkItemDefinition> getWorkItemDefinitions() throws Exception {
      loadDefinitions();
      return itemIdToDefinition.values();
   }

}
