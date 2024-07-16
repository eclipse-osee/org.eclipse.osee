/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.disposition.rest.internal;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.disposition.model.CiSetData;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoConfig;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.model.Note;
import org.eclipse.osee.disposition.model.OperationReport;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.CoverageOseeTypes;
import org.eclipse.osee.framework.core.enums.DispoOseeTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * @author Angel Avila
 */
public class OrcsStorageImpl implements Storage {
   private final OrcsApi orcsApi;
   public static final BranchToken dispoParent = BranchToken.create(5781701693103907161L, "Dispo Parent");

   public OrcsStorageImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   private QueryFactory getQuery() {
      return orcsApi.getQueryFactory();
   }

   private TransactionFactory getTxFactory() {
      return orcsApi.getTransactionFactory();
   }

   private OrcsBranch getBranchFactory() {
      return orcsApi.getBranchOps();
   }

   @Override
   public UserId findUser() {
      return orcsApi.userService().getUser();
   }

   @Override
   public UserId findUser(String userId) {
      return UserId.valueOf(userId);
   }

   @Override
   public boolean isUniqueProgramName(String name) {
      return getQuery().branchQuery().andNameEquals(name).getResults().isEmpty();
   }

   @Override
   public boolean isUniqueSetName(BranchId branch, String name) {
      ResultSet<ArtifactReadable> results = getQuery()//
         .fromBranch(branch)//
         .andTypeEquals(DispoOseeTypes.DispositionSet)//
         .andNameEquals(name)//
         .getResults();

      return results.isEmpty();
   }

   @Override
   public boolean isUniqueItemName(BranchId branch, String setId, String name) {
      ArtifactReadable setArt = findDispoArtifact(branch, setId);
      ResultSet<ArtifactReadable> results = getQuery()//
         .fromBranch(branch)//
         .andRelatedTo(CoreRelationTypes.DefaultHierarchical_Parent, setArt)//
         .andTypeEquals(DispoOseeTypes.DispositionableItem)//
         .andNameEquals(name)//
         .getResults();

      return results.isEmpty();
   }

   @Override
   public BranchToken findDispoProgramIdByName(String branchName) {
      List<BranchToken> dispoPrograms = getDispoBranches();
      BranchToken branchId = null;
      String allItems = "";
      int count = 0;
      for (BranchToken branch : dispoPrograms) {
         if (branch.getName().equals(branchName)) {
            allItems += branch.getIdString() + '\n';
            count++;
            branchId = branch;
         }
      }

      if (count > 1) {
         throw new OseeCoreException("Multiple items found - total [%s]\n%s", count, allItems);
      } else if (count < 1) {
         throw new OseeCoreException("No item found");
      }
      return branchId;
   }

   @Override
   public String findDispoSetIdByName(BranchId branchId, String setName) {
      List<DispoSet> dispoSets = findDispoSets(branchId, DispoStrings.CODE_COVERAGE);
      for (DispoSet set : dispoSets) {
         if (set.getName().equals(setName)) {
            return set.getGuid();
         }
      }
      return null;
   }

   @Override
   public List<DispoSet> findDispoSets(BranchId branch, String type) {
      ResultSet<ArtifactReadable> results = getQuery()//
         .fromBranch(branch)//
         .andTypeEquals(DispoOseeTypes.DispositionSet)//
         .getResults();

      List<DispoSet> toReturn = new ArrayList<>();
      for (ArtifactReadable art : results) {
         DispoSetArtifact dispoSetArt = new DispoSetArtifact(art);
         if (dispoSetArt.getDispoType().equals(type)) {
            toReturn.add(dispoSetArt);
         }
      }
      return toReturn;
   }

   @Override
   public DispoSet findDispoSetsById(BranchId branch, String setId) {
      ArtifactReadable result = findDispoArtifact(branch, setId);
      return new DispoSetArtifact(result);
   }

   private ArtifactReadable findDispoArtifact(BranchId branch, String artId) {
      return getQuery()//
         .fromBranch(branch)//
         .andUuid(Long.valueOf(artId))//
         .getResults().getExactlyOne();
   }

   @Override
   public List<DispoItem> findDispoItems(BranchId branch, String setId, boolean isDetailed) {
      ArtifactReadable setArt = findDispoArtifact(branch, setId);

      List<DispoItem> toReturn = new ArrayList<>();
      for (ArtifactReadable art : setArt.getChildren()) {
         DispoItemArtifact dispoItemArtifact = new DispoItemArtifact(art);
         dispoItemArtifact.setIsIncludeDetails(isDetailed);
         toReturn.add(dispoItemArtifact);
      }
      return toReturn;
   }

   @Override
   public Long createDispoProgram(UserId author, String name) {
      BranchToken branch = BranchToken.create(name);
      getBranchFactory().createWorkingBranch(branch, dispoParent, ArtifactId.SENTINEL);

      return branch.getId();
   }

   @Override
   public ArtifactId createDispoSet(UserId author, BranchId branch, DispoSet descriptor) {
      TransactionBuilder tx = getTxFactory().createTransaction(branch, author, "Create Dispo Set");
      ArtifactId creatdArtId = tx.createArtifact(DispoOseeTypes.DispositionSet, descriptor.getName());
      tx.setSoleAttributeValue(creatdArtId, DispoOseeTypes.DispoImportPath, descriptor.getImportPath());
      tx.setSoleAttributeValue(creatdArtId, DispoOseeTypes.DispoImportState, descriptor.getImportState());
      tx.setSoleAttributeValue(creatdArtId, DispoOseeTypes.DispoConfig, descriptor.getDispoType());
      tx.setSoleAttributeValue(creatdArtId, DispoOseeTypes.DispoNotesJson, JsonUtil.toJson(descriptor.getNotesList()));
      if (descriptor.getCiSet() == null) {
         tx.setSoleAttributeValue(creatdArtId, DispoOseeTypes.DispoCiSet, "NOCI");
         tx.setSoleAttributeValue(creatdArtId, DispoOseeTypes.DispoRerunList, "NOCI");
         tx.setSoleAttributeValue(creatdArtId, DispoOseeTypes.DispoTime, new Date());
      } else {
         tx.setSoleAttributeValue(creatdArtId, DispoOseeTypes.DispoCiSet, descriptor.getCiSet());
         tx.setSoleAttributeValue(creatdArtId, DispoOseeTypes.DispoRerunList, descriptor.getRerunList());
         tx.setSoleAttributeValue(creatdArtId, DispoOseeTypes.DispoTime, descriptor.getTime());
      }

      tx.commit();
      return creatdArtId;
   }

   @Override
   public boolean deleteDispoSet(UserId author, BranchId branch, String setId) {
      return deleteDispoEntityArtifact(author, branch, setId, DispoOseeTypes.DispositionSet);
   }

   @Override
   public boolean deleteDispoItem(UserId author, BranchId branch, String itemId) {
      return deleteDispoEntityArtifact(author, branch, itemId, DispoOseeTypes.DispositionableItem);
   }

   private boolean deleteDispoEntityArtifact(UserId author, BranchId branch, String entityId, ArtifactTypeToken type) {
      boolean toReturn = false;
      ArtifactReadable dispoArtifact = findDispoArtifact(branch, entityId);
      if (dispoArtifact != null) {
         TransactionBuilder tx = getTxFactory().createTransaction(branch, author, "Delete Dispo Artifact");
         tx.deleteArtifact(dispoArtifact);
         tx.commit();
         toReturn = true;
      }
      return toReturn;
   }

   @Override
   public void updateDispoSet(UserId author, BranchId branch, String setId, DispoSet newData) {
      ArtifactReadable dispoSet = findDispoArtifact(branch, setId);
      DispoSetArtifact origSetAs = new DispoSetArtifact(dispoSet);

      String name = newData.getName();
      String importPath = newData.getImportPath();
      String ciSet = newData.getCiSet();
      String rerunList = newData.getRerunList();
      Date time = newData.getTime();

      List<Note> notesList = new LinkedList<>();
      if (newData.getNotesList() != null) {
         notesList = newData.getNotesList();
      }

      TransactionBuilder tx = getTxFactory().createTransaction(branch, author, "Update Dispo Set");
      if (name != null && !name.equals(origSetAs.getName())) {
         tx.setName(dispoSet, name);
      }
      if (importPath != null && !importPath.equals(origSetAs.getImportPath())) {
         tx.setSoleAttributeFromString(dispoSet, DispoOseeTypes.DispoImportPath, importPath);
      }
      if (notesList != null && !notesList.equals(origSetAs.getNotesList())) {
         tx.setSoleAttributeFromString(dispoSet, DispoOseeTypes.DispoNotesJson,
            JsonUtil.toJson(origSetAs.getNotesList()));
      }
      if (ciSet != null && !ciSet.equals(origSetAs.getCiSet())) {
         tx.setSoleAttributeFromString(dispoSet, DispoOseeTypes.DispoCiSet, ciSet);
      }
      if (rerunList != null && !rerunList.equals(origSetAs.getRerunList())) {
         tx.setSoleAttributeFromString(dispoSet, DispoOseeTypes.DispoRerunList, rerunList);
      }
      if (time != null && !time.equals(origSetAs.getTime())) {
         tx.setSoleAttributeValue(dispoSet, DispoOseeTypes.DispoTime, time);
      }
      tx.commit();
   }

   @Override
   public void createDispoItem(UserId author, BranchId branch, DispoSet parentSet, DispoItem data) {
      ArtifactReadable parentSetArt = findDispoArtifact(branch, parentSet.getGuid());
      TransactionBuilder tx = getTxFactory().createTransaction(branch, author, "Create Dispoable Item");

      ArtifactId createdItem = tx.createArtifact(DispoOseeTypes.DispositionableItem, data.getName());

      tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoDateCreated, data.getCreationDate());
      tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoItemLastUpdated, data.getLastUpdate());
      tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoItemStatus, data.getStatus());
      tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoItemTotalPoints, data.getTotalPoints());

      tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoItemNeedsRerun, data.getNeedsRerun());
      tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoItemAborted, data.getAborted());
      tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoDiscrepanciesJson,
         JsonUtil.toJson(data.getDiscrepanciesList()));
      tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoAnnotationsJson,
         JsonUtil.toJson(data.getAnnotationsList()));
      tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoItemVersion, data.getVersion());
      tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoItemAssignee, data.getAssignee());
      tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoItemMachine, data.getMachine());
      tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoItemCategory, data.getCategory());
      tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoItemElapsedTime, data.getElapsedTime());

      if (Strings.isValid(data.getFileNumber())) {
         tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoItemFileNumber, data.getFileNumber());
      }
      if (Strings.isValid(data.getMethodNumber())) {
         tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoItemMethodNumber, data.getMethodNumber());
      }
      tx.relate(parentSetArt, CoreRelationTypes.DefaultHierarchical_Child, createdItem);

      tx.commit();
   }

   @Override
   public void createDispoItems(UserId author, BranchId branch, DispoSet parentSet, List<DispoItem> data) {
      ArtifactReadable parentSetArt = findDispoArtifact(branch, parentSet.getGuid());
      TransactionBuilder tx = getTxFactory().createTransaction(branch, author, "Create Dispoable Item");

      for (DispoItem item : data) {
         ArtifactId createdItem = tx.createArtifact(DispoOseeTypes.DispositionableItem, item.getName());

         if (item.getCreationDate() == null) {
            tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoDateCreated, new Date());
         } else {
            tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoDateCreated, item.getCreationDate());
         }
         tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoItemLastUpdated, item.getLastUpdate());
         tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoItemStatus, item.getStatus());
         tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoItemTotalPoints, item.getTotalPoints());

         tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoItemNeedsRerun, item.getNeedsRerun());
         tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoItemAborted, item.getAborted());
         tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoDiscrepanciesJson,
            JsonUtil.toJson(item.getDiscrepanciesList()));
         tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoAnnotationsJson,
            JsonUtil.toJson(item.getAnnotationsList()));
         tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoItemVersion, item.getVersion());
         tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoItemAssignee, item.getAssignee());
         tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoItemMachine, item.getMachine());
         tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoItemCategory, item.getCategory());
         tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoItemElapsedTime, item.getElapsedTime());

         if (Strings.isValid(item.getFileNumber())) {
            tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoItemFileNumber, item.getFileNumber());
         }
         if (Strings.isValid(item.getMethodNumber())) {
            tx.setSoleAttributeValue(createdItem, DispoOseeTypes.DispoItemMethodNumber, item.getMethodNumber());
         }
         tx.relate(parentSetArt, CoreRelationTypes.DefaultHierarchical_Child, createdItem);
      }
      tx.commit();
   }

   private void updateSingleItem(ArtifactReadable currentItemArt, DispoItem newItemData, TransactionBuilder tx, boolean resetRerunFlag) {
      Date lastUpdate = newItemData.getLastUpdate();
      String name = newItemData.getName();
      Map<String, Discrepancy> newDiscrepancies = newItemData.getDiscrepanciesList();
      List<DispoAnnotationData> newAnnotations = newItemData.getAnnotationsList();
      String status = newItemData.getStatus();
      String assignee = newItemData.getAssignee();
      String totalPoints = newItemData.getTotalPoints();
      String machine = newItemData.getMachine();
      String category = newItemData.getCategory();
      String elapsedTime = newItemData.getElapsedTime();
      Boolean aborted = newItemData.getAborted();
      String itemNotes = newItemData.getItemNotes();
      String fileNumber = newItemData.getFileNumber();
      String methodNumber = newItemData.getMethodNumber();
      String team = newItemData.getTeam();

      Boolean needsRerun;
      if (resetRerunFlag) {
         needsRerun = false;
      } else {
         needsRerun = newItemData.getNeedsRerun();
      }

      DispoItemArtifact origItem = new DispoItemArtifact(currentItemArt);

      if (name != null && !name.equals(origItem.getName())) {
         tx.setName(currentItemArt, name);
      }
      if (newDiscrepancies != null && !newDiscrepancies.equals(origItem.getDiscrepanciesList())) {
         tx.setSoleAttributeFromString(currentItemArt, DispoOseeTypes.DispoDiscrepanciesJson,
            JsonUtil.toJson(newDiscrepancies));
      }
      if (newAnnotations != null && !newAnnotations.equals(origItem.getAnnotationsList())) {
         tx.setSoleAttributeFromString(currentItemArt, DispoOseeTypes.DispoAnnotationsJson,
            JsonUtil.toJson(newAnnotations));
      }
      if (assignee != null && !assignee.equals("UnAssigned") && !assignee.equals(origItem.getAssignee())) {
         tx.setSoleAttributeFromString(currentItemArt, DispoOseeTypes.DispoItemAssignee, assignee);
      }
      if (status != null && !status.equals("Unspecified") && !status.equals(origItem.getStatus())) {
         tx.setSoleAttributeFromString(currentItemArt, DispoOseeTypes.DispoItemStatus, status);
      }
      if (lastUpdate != null && !lastUpdate.equals(origItem.getLastUpdate())) {
         tx.setSoleAttributeValue(currentItemArt, DispoOseeTypes.DispoItemLastUpdated, lastUpdate);
      }
      if (needsRerun != null && !needsRerun.equals(origItem.getNeedsRerun())) {
         tx.setSoleAttributeValue(currentItemArt, DispoOseeTypes.DispoItemNeedsRerun, needsRerun.booleanValue());
      }
      if (totalPoints != null && !totalPoints.equals("0.0") && !totalPoints.equals(origItem.getTotalPoints())) {
         tx.setSoleAttributeFromString(currentItemArt, DispoOseeTypes.DispoItemTotalPoints, totalPoints);
      }
      if (machine != null && !machine.equals("n/a") && !machine.equals(origItem.getMachine())) {
         tx.setSoleAttributeFromString(currentItemArt, DispoOseeTypes.DispoItemMachine, machine);
      }
      if (category != null && !category.isEmpty() && !category.equals(origItem.getCategory())) {
         tx.setSoleAttributeFromString(currentItemArt, DispoOseeTypes.DispoItemCategory, category);
      }
      if (elapsedTime != null && elapsedTime.equals("0.0") && !elapsedTime.equals(origItem.getElapsedTime())) {
         tx.setSoleAttributeFromString(currentItemArt, DispoOseeTypes.DispoItemElapsedTime, elapsedTime);
      }
      if (aborted != null && !aborted.equals(origItem.getAborted())) {
         tx.setSoleAttributeValue(currentItemArt, DispoOseeTypes.DispoItemAborted, aborted);
      }
      if (itemNotes != null && !itemNotes.equals(origItem.getItemNotes())) {
         tx.setSoleAttributeFromString(currentItemArt, DispoOseeTypes.DispoItemNotes, itemNotes);
      }
      if (fileNumber != null && !fileNumber.equals(origItem.getFileNumber())) {
         tx.setSoleAttributeFromString(currentItemArt, DispoOseeTypes.DispoItemFileNumber, fileNumber);
      }
      if (methodNumber != null && !methodNumber.equals(origItem.getMethodNumber())) {
         tx.setSoleAttributeFromString(currentItemArt, DispoOseeTypes.DispoItemMethodNumber, methodNumber);
      }
      if (team != null && !team.equals(origItem.getTeam())) {
         tx.setSoleAttributeFromString(currentItemArt, DispoOseeTypes.DispoItemTeam, team);
      }
   }

   @Override
   public void updateDispoItem(UserId author, BranchId branch, String dispoItemId, DispoItem data) {
      TransactionBuilder tx = getTxFactory().createTransaction(branch, author, "Update Dispo Item");
      ArtifactReadable dispoItemArt = findDispoArtifact(branch, dispoItemId);
      updateSingleItem(dispoItemArt, data, tx, false);
      tx.commit();
   }

   @Override
   public void updateDispoItems(UserId author, BranchId branch, Collection<DispoItem> data, boolean resetRerunFlag, String operation) {
      TransactionBuilder tx = getTxFactory().createTransaction(branch, author, operation);
      boolean isCommitNeeded = false;

      for (DispoItem newItem : data) {
         String itemId = newItem.getGuid();
         if (Strings.isValid(itemId)) {
            isCommitNeeded = true;
            ArtifactReadable dispoItemArt = findDispoArtifact(branch, newItem.getGuid());
            updateSingleItem(dispoItemArt, newItem, tx, resetRerunFlag);
         }
      }

      if (isCommitNeeded) {
         tx.commit();
      }
   }

   @Override
   public List<BranchToken> getDispoBranches() {
      List<BranchToken> dispoBranchesNormalized = new ArrayList<>();

      ResultSet<BranchToken> dispoBranches =
         getQuery().branchQuery().andIsOfType(BranchType.WORKING).andIsChildOf(dispoParent).getResultsAsId();

      for (BranchToken branch : dispoBranches) {
         BranchToken newName = BranchToken.create(branch, branch.getName().replaceFirst("\\(DISPO\\)", ""));

         dispoBranchesNormalized.add(newName);
      }

      return dispoBranchesNormalized;
   }

   @Override
   public List<String> getCheckedReruns(HashMap<String, DispoItem> items, String setId) {
      return Named.getNames(items.values(), DispoItem::getNeedsRerun);
   }

   @Override
   public Collection<DispoItem> findDispoItemByAnnoationText(BranchId branch, String setId, String keyword, boolean isDetailed) {
      ArtifactReadable dispoSetArt = findDispoArtifact(branch, setId);

      Set<DispoItem> toReturn = new HashSet<>();
      ResultSet<ArtifactReadable> dispoArtifacts = getQuery()//
         .fromBranch(branch)//
         .andTypeEquals(DispoOseeTypes.DispositionableItem)//
         .andRelatedTo(CoreRelationTypes.DefaultHierarchical_Parent, dispoSetArt).and(
            DispoOseeTypes.DispoAnnotationsJson, keyword, //
            QueryOption.CONTAINS_MATCH_OPTIONS)//
         .getResults();

      for (ArtifactReadable art : dispoArtifacts) {
         DispoItemArtifact dispoItem = new DispoItemArtifact(art);
         dispoItem.setIsIncludeDetails(isDetailed);
         toReturn.add(dispoItem);
      }

      return toReturn;
   }

   @Override
   public DispoItem findDispoItemById(BranchId branch, String itemId) {
      DispoItem toReturn = null;
      ArtifactReadable dispoArtifact = findDispoArtifact(branch, itemId);
      if (dispoArtifact.isValid()) {
         toReturn = new DispoItemArtifact(dispoArtifact);
      }

      return toReturn;
   }

   @Override
   public String findDispoItemIdByName(BranchId branchId, String setId, String itemName) {
      List<DispoItem> dispoItems = findDispoItems(branchId, setId, false);
      for (DispoItem item : dispoItems) {
         if (item.getName().equals(itemName)) {
            return item.getGuid();
         }
      }
      return null;
   }

   @Override
   public DispoConfig findDispoConfig(BranchId branch) {
      ArtifactReadable config =
         getQuery().fromBranch(branch).andNameEquals("Program Config").getResults().getOneOrDefault(
            ArtifactReadable.SENTINEL);

      if (config.isInvalid()) {
         return DispoUtil.getDefaultConfig();
      }
      return DispoUtil.configArtToConfigData(new DispoConfigArtifact(config));
   }

   @Override
   public String createDispoReport(BranchId branch, UserId author, String contents, String operationTitle) {
      String toReturn = "";

      TransactionBuilder tx = getTxFactory().createTransaction(branch, author, "Update Report: " + operationTitle);

      ArtifactReadable reportArt =
         getQuery().fromBranch(branch).andNameEquals("Dispo_Report").getResults().getOneOrDefault(
            ArtifactReadable.SENTINEL);

      if (reportArt.isInvalid()) {
         TransactionBuilder txToCreate = getTxFactory().createTransaction(branch, author, "Add Operation Report Art");
         txToCreate.createArtifact(CoreArtifactTypes.GeneralData, "Dispo_Report");
         txToCreate.commit();
         reportArt = getQuery().fromBranch(branch).andNameEquals("Dispo_Report").getResults().getExactlyOne();
      }

      tx.setSoleAttributeFromString(reportArt, CoreAttributeTypes.GeneralStringData, contents);
      TransactionToken commit = tx.commit();
      if (commit.isValid()) {

         ArtifactReadable newRerpotArt =
            getQuery().fromBranch(branch).fromTransaction(commit).andId(reportArt).getResults().getExactlyOne();

         AttributeReadable<Object> contentsAsAttribute =
            newRerpotArt.getAttributes(CoreAttributeTypes.GeneralStringData).getExactlyOne();

         toReturn = String.format("/orcs/branch/%s/artifact/%s/attribute/%s/version/%s", branch, newRerpotArt.getGuid(),
            contentsAsAttribute.getId(), commit.getId());
      }
      return toReturn;
   }

   @Override
   public Map<String, ArtifactReadable> getCoverageUnits(BranchId branchId, Long artifactUuid) {
      ArtifactReadable coveragePackage =
         getQuery().fromBranch(branchId).andUuid(artifactUuid).getResults().getExactlyOne();

      List<ArtifactReadable> descendants = coveragePackage.getDescendants();
      return getChildrenRecurse(descendants);
   }

   private Map<String, ArtifactReadable> getChildrenRecurse(List<ArtifactReadable> descendants) {
      Map<String, ArtifactReadable> toReturn = new HashMap<>();

      for (ArtifactReadable descendant : descendants) {
         if (!descendant.getAttributeValues(CoverageOseeTypes.CoverageItem).isEmpty()) {
            ArtifactReadable parent = descendant.getParent();
            String fullName = String.format("%s.%s", parent.getName(), descendant.getName());
            toReturn.put(fullName, descendant);
         }
      }
      return toReturn;
   }

   @Override
   public void updateOperationSummary(UserId author, BranchId branch, String setId, OperationReport summary) {
      OperationReport newReport = DispoUtil.cleanOperationReport(summary);
      ArtifactReadable dispoSet = findDispoArtifact(branch, setId);
      TransactionBuilder tx = getTxFactory().createTransaction(branch, author, "Update Dispo Operation Report");

      tx.setSoleAttributeFromString(dispoSet, DispoOseeTypes.DispoImportState, newReport.getStatus().getName());
      tx.setSoleAttributeFromString(dispoSet, DispoOseeTypes.DispoOperationSummary,
         DispoUtil.operationReportToString(newReport));
      tx.commit();
   }

   @Override
   public ArtifactId getDispoItemParentSet(BranchId branch, String itemId) {
      ArtifactReadable artifact = findDispoArtifact(branch, itemId);
      return artifact.getParent();
   }

   @Override
   public HashMap<ArtifactReadable, BranchId> getCiSet(CiSetData setData) {
      HashMap<ArtifactReadable, BranchId> set = new HashMap<>();
      BranchId branch = BranchId.valueOf(setData.getBranchId());
      List<ArtifactReadable> arts = findDispoSet(branch, ArtifactId.valueOf(setData.getDispoSetId()));
      for (ArtifactReadable art : arts) {
         set.put(art, branch);
      }
      return set;
   }

   @Override
   public String getDispoItemId(BranchId branch, String setId, String item) {
      ArtifactReadable DispoSet =
         getQuery().fromBranch(branch).andId(ArtifactId.valueOf(setId)).getResults().getOneOrDefault(
            ArtifactReadable.SENTINEL);
      if (DispoSet.isInvalid()) {
         return "";
      }
      for (ArtifactReadable child : DispoSet.getChildren()) {
         if (child.getName().equals(item)) {
            return child.getIdString();
         }
      }
      return "";
   }

   @Override
   public List<CiSetData> getAllCiSets() {
      List<CiSetData> setData = new ArrayList<>();
      List<Branch> dispoBranches = findDispoBranches();
      for (BranchId branch : dispoBranches) {
         for (ArtifactReadable dispoSet : findAllCiSets(branch)) {
            String ciSet = dispoSet.getSoleAttributeValue(DispoOseeTypes.DispoCiSet, "");
            if (!ciSet.isEmpty()) {
               CiSetData set = new CiSetData();
               set.setBranchId(branch.getIdString());
               set.setDispoSetId(dispoSet.getIdString());
               set.setCiSetName(ciSet);
               setData.add(set);
            }
         }
      }
      return setData;
   }

   private List<ArtifactReadable> findDispoSet(BranchId branch, ArtifactId setId) {
      return getQuery().fromBranch(branch).andId(setId).getResults().getList();
   }

   private List<Branch> findDispoBranches() {
      Branch dispoParent =
         getQuery().branchQuery().andNameEquals("Dispo Parent").getResults().getOneOrDefault(Branch.SENTINEL);
      return getQuery().branchQuery().andIsChildOf(
         dispoParent).excludeArchived().excludeDeleted().getResults().getList();
   }

   private List<ArtifactReadable> findAllCiSets(BranchId branch) {
      return getQuery().fromBranch(branch).andIsOfType(DispoOseeTypes.DispositionSet).andExists(
         DispoOseeTypes.DispoCiSet).getResults().getList();
   }

   @Override
   public UserId findUserByName(String name) {
      ArtifactId userArtId = getQuery().fromBranch(COMMON).andTypeEquals(CoreArtifactTypes.User).andNameEquals(
         name).asArtifactTokenOrSentinel();
      UserId user = UserId.valueOf(userArtId);
      if (user.isInvalid()) {
         user = findUser();
      }
      return user;
   }
}