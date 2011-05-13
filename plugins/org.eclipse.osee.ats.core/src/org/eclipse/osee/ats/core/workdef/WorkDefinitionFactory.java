/*
 * Created on Dec 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workdef;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.core.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.team.TeamWorkflowProviders;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.util.WorkflowManagerCore;
import org.eclipse.osee.ats.core.workdef.provider.AtsWorkDefinitionProviderCore;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.workflow.ITeamWorkflowProvider;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

public class WorkDefinitionFactory {

   // Cache the WorkDefinition used for each AbstractWorkflowId so don't have to recompute each time
   private static final Map<String, WorkDefinitionMatch> awaHridToWorkDefinitions =
      new HashMap<String, WorkDefinitionMatch>();
   // Cache the WorkDefinition object for each WorkDefinition id so don't have to reload
   // This grows as WorkDefinitions are requested/loaded
   private static final Map<String, WorkDefinitionMatch> workDefIdToWorkDefintion =
      new HashMap<String, WorkDefinitionMatch>();
   public static final String TaskWorkflowDefinitionId = "osee.ats.taskWorkflow";
   public static final String GoalWorkflowDefinitionId = "osee.ats.goalWorkflow";
   public static final String PeerToPeerWorkflowDefinitionId = "osee.ats.peerToPeerReview";
   public static final String DecisionWorkflowDefinitionId = "osee.ats.decisionReview";

   public static void clearCaches() {
      awaHridToWorkDefinitions.clear();
      workDefIdToWorkDefintion.clear();
   }

   public static WorkDefinitionMatch getWorkDefinition(Artifact artifact) throws OseeCoreException {
      if (!awaHridToWorkDefinitions.containsKey(artifact.getHumanReadableId())) {
         WorkDefinitionMatch match = getWorkDefinitionNew(artifact);
         if (!match.isMatched()) {
            match = WorkDefinitionFactoryLegacyMgr.getWorkFlowDefinitionFromArtifact(artifact);
         }
         awaHridToWorkDefinitions.put(artifact.getHumanReadableId(), match);
      }
      return awaHridToWorkDefinitions.get(artifact.getHumanReadableId());
   }

   public static WorkDefinitionMatch getWorkDefinition(String id) throws OseeCoreException {
      if (!workDefIdToWorkDefintion.containsKey(id)) {
         WorkDefinitionMatch match = new WorkDefinitionMatch();
         String translatedId = WorkDefinitionFactory.getOverrideWorkDefId(id);
         // Try to get from new DSL provider if configured to use it
         if (!match.isMatched()) {
            WorkDefinition workDef = AtsWorkDefinitionProviderCore.get().getWorkFlowDefinition(translatedId);
            if (workDef != null) {
               match.setWorkDefinition(workDef);
               match.addTrace((String.format("from DSL provider loaded id [%s] and override translated Id [%s]", id,
                  translatedId)));
            }
         }
         // Otherwise, just translate legacy WorkFlowDefinition from artifact
         if (!match.isMatched()) {
            match = WorkDefinitionFactoryLegacyMgr.getWorkFlowDefinitionFromId(id);
         }
         // If still no match, configuration may have new workdef id but not set to use them
         // Attempt to get back to original id and load through WorkFlowDefinition translate
         if (!match.isMatched()) {
            match = WorkDefinitionFactoryLegacyMgr.getWorkFlowDefinitionFromReverseId(id);
         }
         if (match.isMatched()) {
            if (!OseeProperties.isInTest()) {
               OseeLog.log(Activator.class, Level.INFO, null, "Loaded Work Definition [%s]", match);
               System.out.println();
            }
            workDefIdToWorkDefintion.put(id, match);
         }
      }
      WorkDefinitionMatch match = workDefIdToWorkDefintion.get(id);
      if (match == null) {
         match = new WorkDefinitionMatch();
      }
      return match;
   }

   private static WorkDefinitionMatch getWorkDefinitionFromArtifactsAttributeValue(Artifact artifact) throws OseeCoreException {
      // If this artifact specifies it's own workflow definition, use it
      String workFlowDefId = artifact.getSoleAttributeValue(AtsAttributeTypes.WorkflowDefinition, null);
      if (Strings.isValid(workFlowDefId)) {
         String translatedId = WorkDefinitionFactory.getOverrideWorkDefId(workFlowDefId);
         WorkDefinitionMatch match = getWorkDefinition(translatedId);
         if (match.isMatched()) {
            match.addTrace(String.format("from artifact [%s] for id [%s] and override translated Id [%s]", artifact,
               workFlowDefId, translatedId));
            return match;
         }
      }
      return new WorkDefinitionMatch();
   }

   private static WorkDefinitionMatch getTaskWorkDefinitionFromArtifactsAttributeValue(Artifact artifact) throws OseeCoreException {
      // If this artifact specifies it's own workflow definition, use it
      String workFlowDefId = artifact.getSoleAttributeValue(AtsAttributeTypes.RelatedTaskWorkDefinition, null);
      if (Strings.isValid(workFlowDefId)) {
         String translatedId = WorkDefinitionFactory.getOverrideWorkDefId(workFlowDefId);
         WorkDefinitionMatch match = getWorkDefinition(translatedId);
         if (match.isMatched()) {
            match.addTrace(String.format("from artifact [%s] for id [%s] and override translated Id [%s]", artifact,
               workFlowDefId, translatedId));
            return match;
         }
      }
      return new WorkDefinitionMatch();
   }

   /**
    * Look at team def's attribute for Work Definition setting, otherwise, walk up team tree for setting
    */
   private static WorkDefinitionMatch getWorkDefinitionFromTeamDefinitionAttributeInherited(TeamDefinitionArtifact teamDef) throws OseeCoreException {
      WorkDefinitionMatch match = getWorkDefinitionFromArtifactsAttributeValue(teamDef);
      if (match.isMatched()) {
         return match;
      }
      Artifact parentArt = teamDef.getParent();
      if (parentArt != null && parentArt instanceof TeamDefinitionArtifact) {
         return getWorkDefinitionFromTeamDefinitionAttributeInherited((TeamDefinitionArtifact) parentArt);
      }
      return new WorkDefinitionMatch();
   }

   private static WorkDefinitionMatch getWorkDefinitionForTask(Artifact taskArt) throws OseeCoreException {
      WorkDefinitionMatch match = new WorkDefinitionMatch();
      AbstractWorkflowArtifact parentAwa = WorkflowManagerCore.getParentAWA(taskArt);
      for (ITeamWorkflowProvider provider : TeamWorkflowProviders.getAtsTeamWorkflowExtensions()) {
         String workFlowDefId = provider.getRelatedTaskWorkflowDefinitionId(parentAwa);
         if (Strings.isValid(workFlowDefId)) {
            String translatedId = getOverrideWorkDefId(workFlowDefId);
            match = WorkDefinitionFactory.getWorkDefinition(translatedId);
            match.addTrace((String.format("from provider [%s] for id [%s] and override translated Id [%s]",
               provider.getClass().getSimpleName(), workFlowDefId, translatedId)));
            break;
         }
      }
      if (!match.isMatched()) {
         // If task specifies it's own workflow id, use it
         match = getWorkDefinitionFromArtifactsAttributeValue(taskArt);
      }
      if (!match.isMatched()) {
         // Else If parent SMA has a related task definition workflow id specified, use it
         WorkDefinitionMatch match2 = getTaskWorkDefinitionFromArtifactsAttributeValue(parentAwa);
         if (match2.isMatched()) {
            match2.addTrace(String.format("from task parent SMA [%s]", parentAwa));
            match = match2;
         }
      }
      if (!match.isMatched()) {
         // Else If parent TeamWorkflow's TeamDefinition has a related task definition workflow id, use it
         match = getTaskWorkDefinitionFromArtifactsAttributeValue(WorkflowManagerCore.getTeamDefinition(taskArt));
      }
      if (!match.isMatched()) {
         // Else, use default Task workflow
         String translatedId = getOverrideWorkDefId(TaskWorkflowDefinitionId);
         match = getWorkDefinition(translatedId);
         if (match.isMatched()) {
            match.addTrace(String.format("default TaskWorkflowDefinition ID [%s] and override translated Id [%s]",
               TaskWorkflowDefinitionId, translatedId));
         }
      }
      return match;
   }

   private static WorkDefinitionMatch getWorkDefinitionNew(Artifact artifact) throws OseeCoreException {
      WorkDefinitionMatch match = new WorkDefinitionMatch();
      if (artifact.isOfType(AtsArtifactTypes.Task)) {
         match = getWorkDefinitionForTask(artifact);
      }
      if (!match.isMatched() && artifact instanceof AbstractWorkflowArtifact) {
         AbstractWorkflowArtifact aba = (AbstractWorkflowArtifact) artifact;
         // Check extensions for definition handling
         for (ITeamWorkflowProvider provider : TeamWorkflowProviders.getAtsTeamWorkflowExtensions()) {
            String workFlowDefId = provider.getWorkflowDefinitionId(aba);
            if (Strings.isValid(workFlowDefId)) {
               match =
                  WorkDefinitionFactory.getWorkDefinition(WorkDefinitionFactory.getOverrideWorkDefId(workFlowDefId));
            }
         }
         if (!match.isMatched()) {
            // If this artifact specifies it's own workflow definition, use it
            match = getWorkDefinitionFromArtifactsAttributeValue(artifact);
            if (!match.isMatched()) {
               // Otherwise, use workflow defined by attribute of WorkflowDefinition
               // Note: This is new.  Old TeamDefs got workflow off relation
               if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
                  TeamDefinitionArtifact teamDef = ((TeamWorkFlowArtifact) artifact).getTeamDefinition();
                  match = getWorkDefinitionFromTeamDefinitionAttributeInherited(teamDef);
                  if (!match.isMatched()) {
                     match = ((TeamWorkFlowArtifact) artifact).getTeamDefinition().getWorkDefinition();
                  }
               } else if (artifact.isOfType(AtsArtifactTypes.Goal)) {
                  match = getWorkDefinition(getOverrideWorkDefId(GoalWorkflowDefinitionId));
                  match.addTrace(String.format("Override translated from id [%s]", GoalWorkflowDefinitionId));
               } else if (artifact.isOfType(AtsArtifactTypes.PeerToPeerReview)) {
                  match = getWorkDefinition(getOverrideWorkDefId(PeerToPeerWorkflowDefinitionId));
                  match.addTrace(String.format("Override translated from id [%s]", PeerToPeerWorkflowDefinitionId));
               } else if (artifact.isOfType(AtsArtifactTypes.DecisionReview)) {
                  match = getWorkDefinition(getOverrideWorkDefId(DecisionWorkflowDefinitionId));
                  match.addTrace(String.format("Override translated from id [%s]", DecisionWorkflowDefinitionId));
               }
            }
         }
      }
      return match;
   }

   public static Set<String> errorDisplayed = new HashSet<String>();

   public static String getOverrideWorkDefId(String id) {
      // Don't override if no providers available (dsl plugins not released)
      String overrideId = WorkDefinitionFactoryLegacyMgr.getOverrideId(id);
      if (Strings.isValid(overrideId)) {
         // Only display this override once in log
         if (!errorDisplayed.contains(overrideId) && !OseeProperties.isInTest()) {
            OseeLog.log(Activator.class, Level.INFO,
               String.format("Override WorkDefinition [%s] with [%s]", id, overrideId));
            errorDisplayed.add(overrideId);
         }
         return overrideId;
      }
      return id;
   }

   public static Set<WorkDefinition> loadAllDefinitions() throws OseeCoreException {
      Set<WorkDefinition> workDefs = new HashSet<WorkDefinition>();
      // This load is faster than loading each by artifact type
      for (Artifact art : ArtifactQuery.getArtifactListFromType(AtsArtifactTypes.WorkDefinition,
         AtsUtilCore.getAtsBranch(), DeletionFlag.EXCLUDE_DELETED)) {
         try {
            WorkDefinition workDef = AtsWorkDefinitionProviderCore.get().loadWorkDefinitionFromArtifact(art);
            workDefs.add(workDef);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE,
               "Error loading WorkDefinition from artifact " + art.toStringWithId(), ex);
         }
      }
      return workDefs;
   }
}
