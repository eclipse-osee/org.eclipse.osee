/*********************************************************************
 * Copyright (c) 2017 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.rest.internal.agile.operations;

import com.google.common.base.Strings;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileProgram;
import org.eclipse.osee.ats.api.agile.atw.AtwNode;
import org.eclipse.osee.ats.api.agile.program.UiGridProgItem;
import org.eclipse.osee.ats.api.agile.program.UiGridProgram;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.insertion.IAtsInsertion;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionActivity;
import org.eclipse.osee.ats.api.insertion.JaxInsertion;
import org.eclipse.osee.ats.api.insertion.JaxInsertionActivity;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Donald G. Dunne
 */
public class ProgramOperations {

   private final AtsApi atsApi;
   private static String IMG_BASE_PATH = "/ats/agileui/images/";

   public ProgramOperations(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public AtwNode getAtwTree(IAgileProgram program) {
      if (program == null) {
         throw new OseeCoreException(
            "In ProgramOperations.getAtwTree, the parameter \"program\" is null which is dereferenced");
      }
      ArtifactToken progArt = atsApi.getQueryService().getArtifact(program.getId());
      AtwNode progNode = handleArtifactAndChildren(progArt, null);
      progNode.setExpanded(true);
      return progNode;
   }

   private AtwNode handleArtifactAndChildren(ArtifactToken artifact, AtwNode parentNode) {
      AtwNode node = getNodeFromArt(artifact);
      if (parentNode != null) {
         parentNode.getChildren().add(node);
      }

      for (ArtifactToken child : atsApi.getRelationResolver().getChildren(artifact)) {
         handleArtifactAndChildren(child, node);
      }
      return node;
   }

   private AtwNode getNodeFromArt(ArtifactToken artifact) {
      AtwNode node = new AtwNode();
      node.setName(artifact.getName());
      node.setId(artifact.getIdString());
      node.setImage(getImage(artifact));
      return node;
   }

   public UiGridProgram getUiGridTree(IAgileProgram program) {
      Objects.requireNonNull(program, "Agile Program can not be");
      ArtifactToken progArt = atsApi.getQueryService().getArtifact(program.getId());
      UiGridProgram progItem = new UiGridProgram();
      progItem.setId(program.getId());
      progItem.setName(program.getName());
      progItem.setItems(handleProgArtifactAndChildren(progArt, null, -1, new LinkedList<UiGridProgItem>()));
      return progItem;
   }

   private List<UiGridProgItem> handleProgArtifactAndChildren(ArtifactToken artifact, UiGridProgItem parentNode,
      int level, LinkedList<UiGridProgItem> items) {
      UiGridProgItem item = getProgItemFromArt(artifact, level);
      if (level >= 0) {
         items.add(item);
      }
      if (artifact.isOfType(AtsArtifactTypes.AgileStory)) {
         if (artifact.getName().contains("up")) {
            item.setAgilePoints("Large");
         } else if (artifact.getName().contains("forward")) {
            item.setAgilePoints("Small");
         } else if (artifact.getName().contains("left")) {
            item.setAgilePoints("X-Large");
         }
         addTeamAndSprintAndTasksToStory(artifact, items, level + 1);
      }

      for (ArtifactToken child : atsApi.getRelationResolver().getChildren(artifact)) {
         handleProgArtifactAndChildren(child, item, level + 1, items);
      }
      return items;
   }

   private void addTeamAndSprintAndTasksToStory(ArtifactToken storyArt, LinkedList<UiGridProgItem> items, int level) {
      ArtifactToken teamArt =
         atsApi.getRelationResolver().getRelatedOrSentinel(storyArt, AtsRelationTypes.AgileStoryToAgileTeam_AgileTeam);
      if (teamArt.isValid()) {
         UiGridProgItem item = getProgItemFromArt(teamArt, level);
         items.add(item);
      }
      ArtifactToken sprintArt =
         atsApi.getRelationResolver().getRelatedOrSentinel(storyArt, AtsRelationTypes.AgileStoryToSprint_AgileSprint);
      if (sprintArt.isValid()) {
         UiGridProgItem item = getProgItemFromArt(sprintArt, level);
         items.add(item);
         for (ArtifactToken taskArt : atsApi.getRelationResolver().getRelatedArtifacts(sprintArt,
            AtsRelationTypes.AgileSprintToItem_AtsItem)) {
            UiGridProgItem taskItem = getProgItemFromArt(taskArt, level + 1);
            taskItem.setTLevel(level + 1);
            setTaskFields(taskArt, taskItem);
            items.add(taskItem);
         }

      }
      for (ArtifactToken taskArt : atsApi.getRelationResolver().getRelatedArtifacts(storyArt,
         AtsRelationTypes.AgileStoryToItem_TeamWorkflow)) {
         UiGridProgItem taskItem = getProgItemFromArt(taskArt, level);
         taskItem.setTLevel(level);
         setTaskFields(taskArt, taskItem);
         items.add(taskItem);
      }
   }

   private void setTaskFields(ArtifactToken taskArt, UiGridProgItem taskItem) {
      IAtsWorkItem workItem = atsApi.getQueryService().getTeamWf(taskArt);
      String points = atsApi.getAgileService().getAgileTeamPointsStr(workItem);
      if (org.eclipse.osee.framework.jdk.core.util.Strings.isValid(points)) {
         taskItem.setAgilePoints(points);
      }
      String assignees = workItem.getStateMgr().getAssigneesStr();
      if (org.eclipse.osee.framework.jdk.core.util.Strings.isValid(assignees)) {
         taskItem.setAssigneesOrImplementers(assignees);
      }
   }

   private UiGridProgItem getProgItemFromArt(ArtifactToken artifact, int level) {
      UiGridProgItem item = new UiGridProgItem();
      String paddedName = level > 0 ? Strings.repeat("  - ", level) + artifact.getName() : artifact.getName();
      item.setName(paddedName);
      item.setId(artifact.getId());
      item.setImage(getImage(artifact));
      item.setType(atsApi.getStoreService().getArtifactType(artifact).getName());
      item.setTLevel(level);
      return item;
   }

   private String getImage(ArtifactToken artifact) {
      if (artifact.isOfType(AtsArtifactTypes.AgileProgramBacklog)) {
         return IMG_BASE_PATH + "agileProgramBacklog.gif";
      } else if (artifact.isOfType(AtsArtifactTypes.AgileProgram)) {
         return IMG_BASE_PATH + "agileProgram.gif";
      } else if (artifact.isOfType(AtsArtifactTypes.AgileProgramBacklogItem)) {
         return IMG_BASE_PATH + "agileProgramBacklogItem.gif";
      } else if (artifact.isOfType(AtsArtifactTypes.AgileProgramFeature)) {
         return IMG_BASE_PATH + "agileProgramFeature.gif";
      } else if (artifact.isOfType(AtsArtifactTypes.AgileStory)) {
         return IMG_BASE_PATH + "agileStory.gif";
      } else if (artifact.isOfType(AtsArtifactTypes.AgileSprint)) {
         return IMG_BASE_PATH + "agileSprint.gif";
      } else if (artifact.isOfType(AtsArtifactTypes.AgileBacklog)) {
         return IMG_BASE_PATH + "agileBacklog.gif";
      } else if (artifact.isOfType(AtsArtifactTypes.AgileTeam)) {
         return IMG_BASE_PATH + "agileTeam.gif";
      } else if (artifact.isOfType(AtsArtifactTypes.AgileFeatureGroup)) {
         return IMG_BASE_PATH + "agileFeatureGroup.gif";
      } else if (artifact.isOfType(CoreArtifactTypes.GeneralDocument)) {
         return IMG_BASE_PATH + "generalDoc.gif";
      } else if (artifact.isOfType(CoreArtifactTypes.Folder)) {
         return IMG_BASE_PATH + "folder.gif";
      }
      return null;
   }

   public IAtsInsertion createInsertion(ArtifactId programArtifact, JaxInsertion newInsertion) {

      long id = newInsertion.getId();
      if (id <= 0) {
         id = Lib.generateArtifactIdAsInt();
      }
      IAtsChangeSet changes =
         atsApi.getStoreService().createAtsChangeSet("Create new Insertion", atsApi.getUserService().getCurrentUser());
      ArtifactReadable insertionArt =
         (ArtifactReadable) changes.createArtifact(AtsArtifactTypes.Insertion, newInsertion.getName(), id);

      changes.relate(programArtifact, AtsRelationTypes.ProgramToInsertion_Insertion, insertionArt);
      changes.execute();
      return atsApi.getProgramService().getInsertionById(insertionArt);
   }

   public IAtsInsertion updateInsertion(JaxInsertion updatedInsertion) {
      IAtsChangeSet changes =
         atsApi.getStoreService().createAtsChangeSet("Update Insertion", atsApi.getUserService().getCurrentUser());
      changes.setSoleAttributeValue(ArtifactId.valueOf(updatedInsertion.getId()), CoreAttributeTypes.Name,
         updatedInsertion.getName());
      changes.execute();
      return atsApi.getProgramService().getInsertionById(
         atsApi.getQueryService().getArtifact(updatedInsertion.getId()));
   }

   public void deleteInsertion(ArtifactId artifact) {
      deleteConfigObject(artifact, "Delete Insertion", AtsArtifactTypes.Insertion);
   }

   public IAtsInsertionActivity createInsertionActivity(ArtifactId insertion, JaxInsertionActivity newActivity) {
      long id = newActivity.getId();
      if (id <= 0) {
         id = Lib.generateArtifactIdAsInt();
      }
      IAtsChangeSet changes = atsApi.getStoreService().createAtsChangeSet("Create new Insertion Activity",
         atsApi.getUserService().getCurrentUser());
      ArtifactReadable insertionActivityArt =
         (ArtifactReadable) changes.createArtifact(AtsArtifactTypes.InsertionActivity, newActivity.getName(), id);

      changes.relate(insertion, AtsRelationTypes.InsertionToInsertionActivity_InsertionActivity, insertionActivityArt);
      changes.execute();
      return atsApi.getProgramService().getInsertionActivityById(insertionActivityArt);
   }

   public IAtsInsertionActivity updateInsertionActivity(JaxInsertionActivity updatedActivity) {
      IAtsChangeSet changes =
         atsApi.getStoreService().createAtsChangeSet("Update Insertion", atsApi.getUserService().getCurrentUser());
      ArtifactReadable insertionActivityArt =
         (ArtifactReadable) atsApi.getQueryService().getArtifact(updatedActivity.getId());

      changes.setSoleAttributeValue(insertionActivityArt, CoreAttributeTypes.Name, updatedActivity.getName());
      changes.setSoleAttributeValue(ArtifactId.valueOf(updatedActivity.getId()), CoreAttributeTypes.Name,
         updatedActivity.getName());
      changes.execute();
      return atsApi.getProgramService().getInsertionActivityById(
         atsApi.getQueryService().getArtifact(updatedActivity.getId()));
   }

   public void deleteInsertionActivity(ArtifactId artifact) {
      deleteConfigObject(artifact, "Delete Insertion Activity", AtsArtifactTypes.InsertionActivity);
   }

   private void deleteConfigObject(ArtifactId id, String comment, ArtifactTypeToken type) {
      ArtifactReadable toDelete = (ArtifactReadable) atsApi.getQueryService().getArtifact(id);
      if (toDelete == null) {
         throw new OseeCoreException("No object found for id %s", id);
      }

      if (!toDelete.isTypeEqual(type)) {
         throw new OseeCoreException("Artifact type does not match for %s", comment);
      }
      IAtsChangeSet changes = atsApi.createChangeSet(String.format("Delete config object %s-%s", id, type));
      changes.deleteArtifact(toDelete);
      changes.execute();
   }

}
