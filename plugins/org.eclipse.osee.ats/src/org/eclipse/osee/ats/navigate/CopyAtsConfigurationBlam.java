/*
 * Created on Mar 4, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.navigate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.widgets.XTeamDefinitionCombo;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class CopyAtsConfigurationBlam extends AbstractBlam {

   private org.eclipse.osee.ats.util.widgets.XTeamDefinitionCombo xTeamDefinitionCombo;

   @Override
   public String getName() {
      return "Copy ATS Configuration";
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets>");
      builder.append("<XWidget xwidgetType=\"XTeamDefinitionCombo\" displayName=\"Top Team Definition to Copy From (most like new config)\" />");
      builder.append("<XWidget xwidgetType=\"XText\" displayName=\"Name Search String\" />");
      builder.append("<XWidget xwidgetType=\"XText\" displayName=\"Name Replace String\" />");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"Retain Team Leads/Members\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"Persist Changes\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      builder.append("</xWidgets>");
      return builder.toString();
   }

   private TeamDefinitionArtifact getSelectedTeamDefinition() {
      return (TeamDefinitionArtifact) xTeamDefinitionCombo.getSelectedTeamDef();
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      try {
         if (ArtifactCache.getDirtyArtifacts().size() > 0) {
            AWorkbench.popup("Dirty artifacts found in cache, save all artifacts before running this operation (may need to restart)");
            return;
         }
         String searchStr = (String) variableMap.getValue("Name Search String");
         String replaceStr = (String) variableMap.getValue("Name Replace String");
         TeamDefinitionArtifact teamDef = getSelectedTeamDefinition();
         boolean persistChanges = variableMap.getBoolean("Persist Changes");
         boolean retainTeamLeads = variableMap.getBoolean("Retain Team Leads/Members");

         if (teamDef == null) {
            AWorkbench.popup("ERROR", "Must Select Team Definition");
            return;
         }
         if (!Strings.isValid(searchStr)) {
            AWorkbench.popup("ERROR", "Must Enter Search String");
            return;
         }
         if (!Strings.isValid(replaceStr)) {
            AWorkbench.popup("ERROR", "Must Enter Replace String");
            return;
         }
         XResultData resultData = new XResultData();
         if (persistChanges) {
            resultData.log("Persisting Changes");
         } else {
            resultData.log("Report-Only, Changes are not persisted");
         }

         Set<Artifact> newArtsToPersist = new HashSet<Artifact>(50);
         TeamDefinitionArtifact parentTeamDef = null;
         if (teamDef.getParent() instanceof TeamDefinitionArtifact) {
            parentTeamDef = (TeamDefinitionArtifact) teamDef.getParent();
         } else {
            parentTeamDef = TeamDefinitionArtifact.getTopTeamDefinition();
         }
         confirmOrCreateTeamDefAndAIs(newArtsToPersist, searchStr, replaceStr, resultData, retainTeamLeads, teamDef,
            parentTeamDef, null);
         if (persistChanges) {
            SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Copy ATS Configuration");
            for (Artifact art : newArtsToPersist) {
               art.persist(transaction);
            }
            transaction.execute();
         } else {
            for (Artifact artifact : newArtsToPersist) {
               if (!artifact.isInDb()) {
                  System.out.println("purging " + artifact.toStringWithId());
                  artifact.purgeFromBranch();
               } else {
                  System.out.println("reverting " + artifact.toStringWithId());
                  artifact.revert();
               }
            }
         }
         resultData.report(getName());
      } finally {
         monitor.subTask("Done");
      }
   }

   private void confirmOrCreateTeamDefAndAIs(Set<Artifact> newArts, String searchStr, String replaceStr, XResultData resultData, boolean retainTeamLeads, TeamDefinitionArtifact fromTeamDef, TeamDefinitionArtifact parentTeamDef, ActionableItemArtifact parentActionableItem) throws OseeCoreException {

      // Determine parent actionable item if possible, otherwise use top actionable item
      if (parentActionableItem == null) {
         List<Artifact> fromAias = fromTeamDef.getRelatedArtifacts(AtsRelationTypes.TeamActionableItem_ActionableItem);
         if (fromAias.size() == 1) {
            parentActionableItem = (ActionableItemArtifact) fromAias.iterator().next();
         } else {
            parentActionableItem = ActionableItemArtifact.getTopActionableItem();
         }
      }

      // Get or create new team definition
      TeamDefinitionArtifact newTeamDef =
         (TeamDefinitionArtifact) ArtifactQuery.getArtifactFromTypeAndNameNoException(AtsArtifactTypes.TeamDefinition,
            getConvertedName(fromTeamDef.getName(), searchStr, replaceStr), AtsUtil.getAtsBranch());
      if (newTeamDef == null) {
         newTeamDef =
            (TeamDefinitionArtifact) duplicateTeamDefinitionOrActionableItem(newArts, searchStr, replaceStr,
               resultData, fromTeamDef);
         parentTeamDef.addChild(newTeamDef);
         newArts.add(newTeamDef);
      }
      if (retainTeamLeads) {
         duplicateTeamLeadsAndMembers(newArts, resultData, fromTeamDef, newTeamDef);
      }
      duplicateWorkItems(newArts, resultData, fromTeamDef, newTeamDef);

      List<ActionableItemArtifact> newAias = new ArrayList<ActionableItemArtifact>();
      for (ActionableItemArtifact fromAi : fromTeamDef.getRelatedArtifacts(
         AtsRelationTypes.TeamActionableItem_ActionableItem, ActionableItemArtifact.class)) {
         ActionableItemArtifact newAia =
            (ActionableItemArtifact) ArtifactQuery.getArtifactFromTypeAndNameNoException(
               AtsArtifactTypes.ActionableItem, getConvertedName(fromAi.getName(), searchStr, replaceStr),
               AtsUtil.getAtsBranch());
         // Check for actionable item in relationship
         if (newAia == null) {
            newAia =
               (ActionableItemArtifact) duplicateTeamDefinitionOrActionableItem(newArts, searchStr, replaceStr,
                  resultData, fromAi);
            newTeamDef.addRelation(AtsRelationTypes.TeamActionableItem_ActionableItem, newAia);
            parentActionableItem.addChild(newAia);
            newArts.add(parentActionableItem);
         }
         newAias.add(newAia);
      }

      // Create all children team definitions and their related AIs
      for (Artifact childTeamDef : fromTeamDef.getChildren()) {
         if (childTeamDef instanceof TeamDefinitionArtifact) {
            ActionableItemArtifact parentAi = (newAias.size() == 1 ? newAias.iterator().next() : null);
            confirmOrCreateTeamDefAndAIs(newArts, searchStr, replaceStr, resultData, retainTeamLeads,
               (TeamDefinitionArtifact) childTeamDef, newTeamDef, parentAi);
         }
      }
   }

   private void duplicateTeamLeadsAndMembers(Set<Artifact> newArts, XResultData resultData, TeamDefinitionArtifact fromTeamDef, TeamDefinitionArtifact newTeamDef) throws OseeCoreException {
      Collection<Artifact> leads = newTeamDef.getRelatedArtifacts(AtsRelationTypes.TeamLead_Lead);
      for (Artifact user : fromTeamDef.getRelatedArtifacts(AtsRelationTypes.TeamLead_Lead)) {
         if (!leads.contains(user)) {
            newArts.add(user);
            newArts.add(newTeamDef);
            newTeamDef.addRelation(AtsRelationTypes.TeamLead_Lead, user);
            resultData.log("   - Relating team lead " + user);
         }
      }
      Collection<Artifact> members = newTeamDef.getRelatedArtifacts(AtsRelationTypes.TeamMember_Member);
      for (Artifact user : fromTeamDef.getRelatedArtifacts(AtsRelationTypes.TeamMember_Member)) {
         if (!members.contains(user)) {
            newArts.add(user);
            newArts.add(newTeamDef);
            newTeamDef.addRelation(AtsRelationTypes.TeamMember_Member, user);
            resultData.log("   - Relating team member " + user);
         }
      }
   }

   private void duplicateWorkItems(Set<Artifact> newArts, XResultData resultData, TeamDefinitionArtifact fromTeamDef, TeamDefinitionArtifact newTeamDef) throws OseeCoreException {
      Collection<Artifact> workItems = newTeamDef.getRelatedArtifacts(CoreRelationTypes.WorkItem__Child);
      for (Artifact workChild : fromTeamDef.getRelatedArtifacts(CoreRelationTypes.WorkItem__Child)) {
         if (!workItems.contains(workChild)) {
            newArts.add(workChild);
            newTeamDef.addRelation(CoreRelationTypes.WorkItem__Child, workChild);
            resultData.log("   - Adding work child " + workChild);
         }
      }
   }

   private Artifact duplicateTeamDefinitionOrActionableItem(Set<Artifact> newArts, String searchStr, String replaceStr, XResultData resultData, Artifact fromArtifact) throws OseeCoreException {
      String newName = getConvertedName(fromArtifact.getName(), searchStr, replaceStr);
      if (newName.equals(fromArtifact.getName())) {
         throw new OseeArgumentException("Could not get new name from name conversion.");
      }
      // duplicate all but baseline branch guid
      Artifact newTeamDef =
         fromArtifact.duplicate(AtsUtil.getAtsBranch(), Arrays.asList(AtsAttributeTypes.BaselineBranchGuid));
      newTeamDef.setName(newName);
      resultData.log("Creating new " + newTeamDef.getArtifactTypeName() + ": " + newTeamDef);
      String fullName = newTeamDef.getSoleAttributeValue(AtsAttributeTypes.FullName, null);
      if (fullName != null) {
         String newFullName = getConvertedName(fullName, searchStr, replaceStr);
         if (!newFullName.equals(fullName)) {
            newTeamDef.setSoleAttributeFromString(AtsAttributeTypes.FullName, newFullName);
            resultData.log("   - Converted \"ats.Full Name\" to " + newFullName);
         }
      }
      newArts.add(newTeamDef);
      return newTeamDef;
   }

   private String getConvertedName(String name, String searchStr, String replaceStr) {
      return name.replaceFirst(searchStr, replaceStr);
   }

   @Override
   public String getDescriptionUsage() {
      return "This BLAM will use existing configuration of a top Team Definition to create a new configuration..\n" +
      //
      "This includes making team defs, actionable items, setting all team leads/team members and changing name using search string and replace string.";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("ATS.ADMIN");
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (xWidget.getLabel().equals("Top Team Definition to Copy From (most like new config)")) {
         xTeamDefinitionCombo = (XTeamDefinitionCombo) xWidget;
      }
   }

}
