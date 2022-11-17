/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.ats.api.task.create;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeItemData;
import org.eclipse.osee.framework.core.model.change.ChangeItemUtil;
import org.eclipse.osee.framework.core.model.change.ChangeReportRollup;
import org.eclipse.osee.framework.core.model.change.ChangeType;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public interface IAtsChangeReportTaskNameProvider {

   public ChangeReportTaskNameProviderToken getId();

   default Map<ArtifactId, ArtifactToken> getTasksComputedAsNeeded(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, AtsApi atsApi) {
      return getTasksComputedAsNeeded(crtd, crttwd, atsApi, isAddTaskMatch());
   }

   /**
    * @return true if want default task names; false otherwise and this method will only fill up mod,del,rel lists
    */
   default boolean isAddTaskMatch() {
      return true;
   }

   default public Map<ArtifactId, ArtifactToken> getTasksComputedAsNeeded(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, AtsApi atsApi, boolean addTaskMatch) {
      Map<ArtifactId, ArtifactToken> idToArtifact = new HashMap<>();
      getModifiedArifactNames(crtd, crttwd, idToArtifact, atsApi, addTaskMatch);
      getRelArtifactNames(crtd, crttwd, idToArtifact, atsApi, addTaskMatch);
      getExtensionNames(crtd, crttwd, idToArtifact, atsApi, addTaskMatch);
      getApiAndTaskNames(crtd, crttwd, idToArtifact, atsApi, addTaskMatch);
      return idToArtifact;
   }

   /**
    * Add tasks defined in StaticTaskDefinition through java api. These will be added regardless of change report
    * contents.
    */
   default void getApiAndTaskNames(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, Map<ArtifactId, ArtifactToken> idToArtifact, AtsApi atsApi, boolean addTaskMatch) {
      for (StaticTaskDefinition taskDef : crtd.getSetDef().getStaticTaskDefs()) {
         ChangeReportTaskMatch match = new ChangeReportTaskMatch();
         match.setTaskName(taskDef.getName());
         match.setCreateTaskDef(taskDef);
         match.setType(ChangeReportTaskMatchType.StaticTskCompAsNeeded);
         match.setAutoTaskGenType(AutoTaskGenType.Static.name());
         crttwd.getTaskMatches().add(match);
      }
   }

   /**
    * Allow extensions to add other names
    */
   default void getExtensionNames(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, Map<ArtifactId, ArtifactToken> idToArtifact, AtsApi atsApi, boolean addTaskMatch) {
      // do nothing
   }

   /**
    * Compute all artifacts on an included rel that doesn't have 2 artifacts both excluded.
    */
   default void getRelArtifactNames(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, Map<ArtifactId, ArtifactToken> idToArtifact, AtsApi atsApi, boolean addTaskMatch) {
      for (ArtifactId chgRptArt : getRelArts(crtd, crttwd, atsApi)) {
         logAndAddTaskName(crtd, crttwd, atsApi, ArtifactToken.valueOf(chgRptArt, ""), idToArtifact,
            TaskChangeType.Relation.name(), addTaskMatch);
      }
   }

   /**
    * Compute all artifacts modified and deleted
    */
   default void getModifiedArifactNames(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, Map<ArtifactId, ArtifactToken> idToArtifact, AtsApi atsApi, boolean addTaskMatch) {
      processModifiedDeletedArts(crtd, crttwd, idToArtifact, atsApi, addTaskMatch);
   }

   /**
    * Override to provide additional attributes to be added to created task
    */
   default ChangeReportTaskMatch logAndAddTaskName(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, AtsApi atsApi, ArtifactToken chgRptArt, Map<ArtifactId, ArtifactToken> idToArtifact, String chgType, boolean addTaskMatch) {
      ArtifactToken art =
         atsApi.getQueryService().getArtifact(chgRptArt, crtd.getWorkOrParentBranchId(), DeletionFlag.INCLUDE_DELETED);
      idToArtifact.put(art, art);
      if (addTaskMatch) {
         String safeName = getTaskName(atsApi, chgType, art);
         if (Strings.isValid(safeName)) {
            ChangeReportTaskMatch match = new ChangeReportTaskMatch();
            match.setChgRptArt(chgRptArt);
            match.setChgRptArtName(art.getName());
            boolean deleted = isChangeItemDeleted(art, crtd.getChangeItems());
            match.setChgRptArtDeleted(deleted);
            match.setTaskName(safeName);
            match.setType(ChangeReportTaskMatchType.ChgRptTskCompAsNeeded);
            if (chgType.equals(TaskChangeType.Deleted.name())) {
               match.setAutoTaskGenType(AutoTaskGenType.ChgRptDelete.name());
            } else if (chgType.equals(TaskChangeType.AddMod.name())) {
               match.setAutoTaskGenType(AutoTaskGenType.ChgRptAddMod.name());
            }
            crttwd.getTaskMatches().add(match);
            return match;
         }
      }
      return null;
   }

   public static boolean isChangeItemDeleted(ArtifactToken reqArt, Collection<ChangeItem> changeItems) {
      boolean deleted = false;
      if (changeItems != null && !changeItems.isEmpty()) {
         for (ChangeItem change : changeItems) {
            if (change.getArtId().equals(reqArt) && change.getChangeType().isAttributeChange()) {
               if (change.getNetChange().getModType() == ModificationType.ARTIFACT_DELETED) {
                  deleted = true;
               }
            }
         }
      }
      return deleted;
   }

   /**
    * @return Name or null for no task
    */
   default String getTaskName(AtsApi atsApi, String chgType, ArtifactToken art) {
      String safeName = String.format("Handle %s change to [%s]", chgType, atsApi.getStoreService().getSafeName(art));
      if (TaskChangeType.Deleted.name().equals(chgType)) {
         safeName += " (Deleted)";
      }
      return safeName;
   }

   default ChangeReportModDelArts processModifiedDeletedArts(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, Map<ArtifactId, ArtifactToken> idToArtifact, //
      AtsApi atsApi, boolean addTaskMatch) {

      List<ChangeItem> changeItems = crtd.getChangeItems();

      Set<ArtifactId> modArts = new HashSet<>();
      Set<ArtifactId> delArts = new HashSet<>();

      if (crtd.getWorkOrParentBranchId().isValid()) {
         BranchToken branch = atsApi.getBranchService().getBranch(crtd.getWorkOrParentBranchId());

         ChangeItemData data = getChangeItemData(changeItems, branch, atsApi);
         Collection<ChangeReportRollup> rollups = data.getRollups().values();
         for (ChangeReportRollup rollup : rollups) {
            if (rollup.getArtToken().getName().contains("COM_SUBTASK")) {
               System.err.println("here");
            }
            ArtifactIncluded result = isIncluded(crtd, crttwd, rollup, rollup.getArtType(), atsApi);

            if (result.isIncluded()) {
               if (result.isDeleted()) {
                  delArts.add(rollup.getArtId());
                  logAndAddTaskName(crtd, crttwd, atsApi, rollup.getArtToken(), idToArtifact,
                     TaskChangeType.Deleted.name(), addTaskMatch);
               } else {
                  logAndAddTaskName(crtd, crttwd, atsApi, rollup.getArtToken(), idToArtifact,
                     TaskChangeType.AddMod.name(), addTaskMatch);
                  modArts.add(rollup.getArtId());
               }
            }
         }
      }
      ChangeReportModDelArts modDel = new ChangeReportModDelArts();
      modDel.setModified(modArts);
      modDel.setDeleted(delArts);
      return modDel;
   }

   /**
    * @return A, B where A is if Artifact/Attributes are included and B if deleted
    */
   default ArtifactIncluded isIncluded(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, ChangeReportRollup rollup, ArtifactTypeToken artType, AtsApi atsApi) {

      Collection<ArtifactTypeToken> incArtTypes = crtd.getSetDef().getChgRptOptions().getArtifactTypes();
      Collection<ArtifactTypeToken> exclArtTypes = crtd.getSetDef().getChgRptOptions().getNotArtifactTypes();
      Collection<AttributeTypeToken> incAttrTypes = crtd.getSetDef().getChgRptOptions().getAttributeTypes();
      Collection<AttributeTypeToken> exclAttrTypes = crtd.getSetDef().getChgRptOptions().getNotAttributeTypes();

      boolean included = false;
      boolean deleted = false;

      // Check for included/excluded artifact type
      if (artType.inheritsFromAny(exclArtTypes)) {
         // included == false
         return new ArtifactIncluded(included, deleted);
      }
      if (!incArtTypes.isEmpty() && !artType.inheritsFromAny(incArtTypes)) {
         // included == false
         return new ArtifactIncluded(included, deleted);
      }

      // Check for included/excluded attribute type
      for (ChangeItem item : rollup.getChangeItems()) {
         if (ChangeItemUtil.createdAndDeleted(item)) {
            continue;
         }
         if (item.getChangeType() != ChangeType.Relation && ChangeItemUtil.isArtifactDeleted(
            item.getCurrentVersion())) {
            deleted = true;
         }

         // Change Type or Applicability always true
         if (item.getChangeType().isArtifactChange() && !item.isSynthetic()) {
            deleted = calculatedIfDeleted(rollup);
            return new ArtifactIncluded(true, deleted);
         }

         // Look through attrs for anything that should be included
         if (item.getChangeType().isAttributeChange()) {

            Id typeId = item.getItemTypeId();
            AttributeTypeToken attrType = atsApi.tokenService().getAttributeType(typeId.getId());

            // Set to include if no include types specified
            boolean incAttrType = incAttrTypes.isEmpty();

            // If not included, add if included
            if (!incAttrType) {
               incAttrType = incAttrTypes.contains(attrType);
            }

            // If included, remove if excluded
            if (incAttrType) {
               incAttrType = !exclAttrTypes.contains(attrType);
            }

            if (incAttrType) {
               included = true;
               break;
            }
         }
      }
      return new ArtifactIncluded(included, deleted);
   }

   default public boolean calculatedIfDeleted(ChangeReportRollup rollup) {
      for (ChangeItem item : rollup.getChangeItems()) {
         if (item.getChangeType().isArtifactChange() && item.isDeleted()) {
            return true;
         }
      }
      return false;
   }

   /**
    * Create CID and set all art types in rollups
    */
   default ChangeItemData getChangeItemData(Collection<ChangeItem> changes, BranchToken branchId, AtsApi atsApi) {
      ChangeItemData data = new ChangeItemData(changes);
      for (ChangeReportRollup rollup : data.getRollups().values()) {
         ArtifactId artId = rollup.getArtId();
         ArtifactTypeToken artType = atsApi.getStoreService().getArtifactType(artId, branchId);
         rollup.setArtType(artType);
         ArtifactToken tok = atsApi.getQueryService().getArtifact(artId, branchId, DeletionFlag.INCLUDE_DELETED);
         rollup.setArtToken(ArtifactToken.valueOf(tok.getId(), tok.getName(), branchId, tok.getArtifactType()));
      }
      return data;
   }

   default Collection<ArtifactId> getRelArts(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, AtsApi atsApi) {

      Collection<RelationTypeToken> incRelTypes = crtd.getSetDef().getChgRptOptions().getRelationTypes();
      Collection<RelationTypeToken> exclRelTypes = crtd.getSetDef().getChgRptOptions().getNotRelationTypes();
      BranchToken branch = atsApi.getBranchService().getBranch(crtd.getWorkOrParentBranchId());
      Set<ArtifactId> arts = new HashSet<>();
      ChangeItemData data = getChangeItemData(crtd.getChangeItems(), branch, atsApi);

      for (ChangeReportRollup rollup : data.getRollups().values()) {

         List<ChangeItem> reqArtChangeItems = rollup.getChangeItems();
         for (ChangeItem item : reqArtChangeItems) {
            if (item.getChangeType().isNotRelationChange()) {
               continue;
            }
            if (ChangeItemUtil.createdAndDeleted(item)) {
               continue;
            }
            if (exclRelTypes.contains(item.getItemTypeId())) {
               continue;
            }
            if (!incRelTypes.isEmpty() && !incRelTypes.contains(item.getItemTypeId())) {
               continue;
            }

            ArtifactTypeToken artAType = atsApi.getStoreService().getArtifactType(item.getArtId(), branch);
            ArtifactTypeToken artBType = atsApi.getStoreService().getArtifactType(item.getArtIdB(), branch);

            ArtifactIncluded artAInc = isIncluded(crtd, crttwd, rollup, artAType, atsApi);
            ArtifactIncluded artBInc = isIncluded(crtd, crttwd, rollup, artBType, atsApi);

            if ((artAInc.isNotDeleted() && artAInc.isIncluded()) || //
               (artBInc.isNotDeleted() && artBInc.isIncluded())) {
               arts.add(item.getArtId());
               arts.add(item.getArtIdB());
            }
         }
      }
      return arts;
   }

}
