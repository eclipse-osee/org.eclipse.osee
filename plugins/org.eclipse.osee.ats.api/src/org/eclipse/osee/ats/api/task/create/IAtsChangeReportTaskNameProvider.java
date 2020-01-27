/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeItemData;
import org.eclipse.osee.framework.core.model.change.ChangeItemUtil;
import org.eclipse.osee.framework.core.model.change.ChangeReportRollup;
import org.eclipse.osee.framework.core.model.change.ChangeType;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Donald G. Dunne
 */
public interface IAtsChangeReportTaskNameProvider {

   public ChangeReportTaskNameProviderToken getId();

   default public Map<ArtifactId, ArtifactToken> getTasksComputedAsNeeded(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, AtsApi atsApi) {
      Map<ArtifactId, ArtifactToken> idToArtifact = new HashMap<>();
      getModifiedArifactNames(crtd, crttwd, idToArtifact, atsApi);
      getRelArtifactNames(crtd, crttwd, idToArtifact, atsApi);
      getExtensionNames(crtd, crttwd, idToArtifact, atsApi);
      getApiAndTaskNames(crtd, crttwd, idToArtifact, atsApi);
      return idToArtifact;
   }

   /**
    * Add tasks defined in StaticTaskDefinition through java api. These will be added regardless of change report
    * contents.
    */
   default void getApiAndTaskNames(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, Map<ArtifactId, ArtifactToken> idToArtifact, AtsApi atsApi) {
      for (StaticTaskDefinition taskDef : crtd.getSetDef().getStaticTaskDefs()) {
         ChangeReportTaskMatch match = new ChangeReportTaskMatch();
         match.setTaskName(taskDef.getName());
         match.setCreateTaskDef(taskDef);
         match.setType(ChangeReportTaskMatchType.StaticTaskComputedAsNeeded);
         crttwd.getTaskMatches().add(match);
      }
   }

   /**
    * Allow extensions to add other names
    */
   default void getExtensionNames(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, Map<ArtifactId, ArtifactToken> idToArtifact, AtsApi atsApi) {
      // do nothing
   }

   /**
    * Compute all artifacts on an included rel that doesn't have 2 artifacts both excluded.
    */
   default void getRelArtifactNames(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, Map<ArtifactId, ArtifactToken> idToArtifact, AtsApi atsApi) {
      for (ArtifactId chgRptArt : getRelArts(crtd, crttwd, atsApi)) {
         logAndAddTaskName(crtd, crttwd, atsApi, chgRptArt, idToArtifact, "Relation");
      }
   }

   /**
    * Compute all artifacts modified and deleted
    */
   default void getModifiedArifactNames(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, Map<ArtifactId, ArtifactToken> idToArtifact, AtsApi atsApi) {
      processModifiedDeletedArts(crtd, crttwd, idToArtifact, atsApi);
   }

   default void logAndAddTaskName(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, AtsApi atsApi, ArtifactId chgRptArt, Map<ArtifactId, ArtifactToken> idToArtifact, String chgType) {
      ArtifactToken art =
         atsApi.getQueryService().getArtifact(chgRptArt, crtd.getWorkOrParentBranch(), DeletionFlag.INCLUDE_DELETED);
      idToArtifact.put(art, art);
      String safeName = String.format("Handle %s change to [%s]", chgType, atsApi.getStoreService().getSafeName(art));
      if ("Deleted".equals(chgType)) {
         safeName += " (Deleted)";
      }
      ChangeReportTaskMatch match = new ChangeReportTaskMatch();
      match.setChgRptArt(chgRptArt);
      match.setTaskName(safeName);
      match.setType(ChangeReportTaskMatchType.ChangedReportTaskComputedAsNeeded);
      crttwd.getTaskMatches().add(match);
   }

   default ChangeReportModDelArts processModifiedDeletedArts(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, Map<ArtifactId, ArtifactToken> idToArtifact, //
      AtsApi atsApi) {

      List<ChangeItem> changeItems = crtd.getChangeItems();

      Set<ArtifactId> modArts = new HashSet<>();
      Set<ArtifactId> delArts = new HashSet<>();

      ChangeItemData data = getChangeItemData(changeItems, crtd.getWorkOrParentBranch(), atsApi);
      for (ChangeReportRollup rollup : data.getRollups().values()) {

         ArtifactIncluded result = isIncluded(crtd, crttwd, rollup.getChangeItems(), rollup.getArtType(), atsApi);

         if (result.isIncluded()) {
            if (result.isDeleted()) {
               delArts.add(rollup.getArtId());
               logAndAddTaskName(crtd, crttwd, atsApi, rollup.getArtId(), idToArtifact, "Deleted");
            } else {
               logAndAddTaskName(crtd, crttwd, atsApi, rollup.getArtId(), idToArtifact, "Add/Mod");
               modArts.add(rollup.getArtId());
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
   default ArtifactIncluded isIncluded(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, Collection<ChangeItem> reqArtChangeItems, ArtifactTypeToken artType, AtsApi atsApi) {

      Collection<ArtifactTypeToken> incArtTypes = crtd.getSetDef().getChgRptOptions().getArtifactTypes();
      Collection<ArtifactTypeToken> exclArtTypes = crtd.getSetDef().getChgRptOptions().getNotArtifactTypes();
      Collection<AttributeTypeToken> incAttrTypes = crtd.getSetDef().getChgRptOptions().getAttributeTypes();
      Collection<AttributeTypeToken> exclAttrTypes = crtd.getSetDef().getChgRptOptions().getNotAttributeTypes();

      boolean included = false;
      boolean deleted = false;

      // Check for included/excluded artifact type
      if (artType.inheritsFromAny(exclArtTypes)) {
         return new ArtifactIncluded(included, deleted);
      }
      if (!incArtTypes.isEmpty() && !artType.inheritsFromAny(incArtTypes)) {
         return new ArtifactIncluded(included, deleted);
      }

      // Check for included/excluded attribute type
      for (ChangeItem item : reqArtChangeItems) {
         if (item.getChangeType() != ChangeType.ATTRIBUTE_CHANGE) {
            continue;
         }
         if (ChangeItemUtil.createdAndDeleted(item)) {
            continue;
         }
         if (ChangeItemUtil.isDeleted(item.getCurrentVersion())) {
            deleted = true;
         }

         Id typeId = item.getItemTypeId();
         AttributeTypeToken attrType = atsApi.getStoreService().getAttributeType(typeId.getId());

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
      return new ArtifactIncluded(included, deleted);
   }

   /**
    * Create CID and set all art types in rollups
    */
   default ChangeItemData getChangeItemData(Collection<ChangeItem> changes, BranchId branchId, AtsApi atsApi) {
      ChangeItemData data = new ChangeItemData(changes);
      for (ChangeReportRollup rollup : data.getRollups().values()) {
         ArtifactId artId = rollup.getArtId();
         ArtifactTypeToken artType = atsApi.getStoreService().getArtifactType(artId, branchId);
         rollup.setArtType(artType);
      }
      return data;
   }

   default Collection<ArtifactId> getRelArts(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, AtsApi atsApi) {

      Collection<RelationTypeToken> incRelTypes = crtd.getSetDef().getChgRptOptions().getRelationTypes();
      Collection<RelationTypeToken> exclRelTypes = crtd.getSetDef().getChgRptOptions().getNotRelationTypes();
      BranchId workingOrParentBranch = crtd.getWorkOrParentBranch();
      Set<ArtifactId> arts = new HashSet<>();
      ChangeItemData data = getChangeItemData(crtd.getChangeItems(), crtd.getWorkOrParentBranch(), atsApi);

      for (ChangeReportRollup rollup : data.getRollups().values()) {

         List<ChangeItem> reqArtChangeItems = rollup.getChangeItems();
         for (ChangeItem item : reqArtChangeItems) {
            if (item.getChangeType() != ChangeType.RELATION_CHANGE) {
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

            ArtifactTypeToken artAType =
               atsApi.getStoreService().getArtifactType(item.getArtId(), workingOrParentBranch);
            ArtifactTypeToken artBType =
               atsApi.getStoreService().getArtifactType(item.getArtIdB(), workingOrParentBranch);

            ArtifactIncluded artAInc = isIncluded(crtd, crttwd, reqArtChangeItems, artAType, atsApi);
            ArtifactIncluded artBInc = isIncluded(crtd, crttwd, reqArtChangeItems, artBType, atsApi);

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