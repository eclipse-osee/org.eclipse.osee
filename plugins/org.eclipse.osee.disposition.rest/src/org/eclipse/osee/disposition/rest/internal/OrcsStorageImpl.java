/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.internal;

import static org.eclipse.osee.disposition.rest.DispoConstants.DISPO_ARTIFACT;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.UriGeneralStringData;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.io.InputStream;
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
import org.eclipse.osee.disposition.model.DispoStorageMetadata;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.model.Note;
import org.eclipse.osee.disposition.model.OperationReport;
import org.eclipse.osee.disposition.rest.DispoConstants;
import org.eclipse.osee.disposition.rest.internal.importer.coverage.CoverageUtil;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.logger.Log;
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
   private final Log logger;
   private final OrcsApi orcsApi;
   public static final IOseeBranch dispoParent = IOseeBranch.create(5781701693103907161L, "Dispo Parent");

   public OrcsStorageImpl(Log logger, OrcsApi orcsApi) {
      this.logger = logger;
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

   private void reloadTypes() {
      orcsApi.getOrcsTypes().invalidateAll();
   }

   @Override
   public boolean typesExist() {
      try {
         return getQuery().fromBranch(COMMON).andId(DISPO_ARTIFACT).getResults().getAtMostOneOrDefault(
            ArtifactReadable.SENTINEL).isValid();
      } catch (OseeCoreException ex) {
         logger.warn(ex, "Error checking for Dispo Types");
         return false;
      }
   }

   @Override
   public void storeTypes(IResource resource) {
      TransactionBuilder tx = getTxFactory().createTransaction(COMMON, SystemUser.OseeSystem, "Initialize Dispo Types");
      ArtifactId artifactId = tx.createArtifact(DISPO_ARTIFACT);
      InputStream stream = null;
      try {
         stream = resource.getContent();
         tx.setSoleAttributeFromStream(artifactId, UriGeneralStringData, stream);
      } finally {
         Lib.close(stream);
      }
      tx.commit();
      reloadTypes();
   }

   @Override
   public ArtifactReadable findUser() {
      return getQuery().fromBranch(COMMON).andId(SystemUser.OseeSystem).getResults().getExactlyOne();
   }

   @Override
   public ArtifactReadable findUser(String userId) {
      return getQuery().fromBranch(COMMON).andGuid(userId).getResults().getExactlyOne();
   }

   @Override
   public boolean isUniqueProgramName(String name) {
      return getQuery().branchQuery().andNameEquals(name).getResults().isEmpty();
   }

   @Override
   public boolean isUniqueSetName(BranchId branch, String name) {
      ResultSet<ArtifactReadable> results = getQuery()//
         .fromBranch(branch)//
         .andTypeEquals(DispoConstants.DispoSet)//
         .andNameEquals(name)//
         .getResults();

      return results.isEmpty();
   }

   @Override
   public boolean isUniqueItemName(BranchId branch, String setId, String name) {
      ArtifactReadable setArt = findDispoArtifact(branch, setId);
      ResultSet<ArtifactReadable> results = getQuery()//
         .fromBranch(branch)//
         .andRelatedTo(CoreRelationTypes.Default_Hierarchical__Parent, setArt)//
         .andTypeEquals(DispoConstants.DispoItem)//
         .andNameEquals(name)//
         .getResults();

      return results.isEmpty();
   }

   @Override
   public IOseeBranch findDispoProgramIdByName(String branchName) {
      List<IOseeBranch> dispoPrograms = getDispoBranches();
      IOseeBranch branchId = null;
      int count = 0;
      for (IOseeBranch branch : dispoPrograms) {
         if (branch.getName().equals(branchName)) {
            count++;
            branchId = branch;
         }
      }

      if (count > 1) {
         throw new OseeCoreException("Multiple items found - total [%s]", count);
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
         .andTypeEquals(DispoConstants.DispoSet)//
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
   public List<DispoItem> findDipoItems(BranchId branch, String setId, boolean isDetailed) {
      ArtifactReadable setArt = findDispoArtifact(branch, setId);
      ResultSet<ArtifactReadable> results = setArt.getChildren();

      List<DispoItem> toReturn = new ArrayList<>();
      for (ArtifactReadable art : results) {
         DispoItemArtifact dispoItemArtifact = new DispoItemArtifact(art);
         dispoItemArtifact.setIsIncludeDetails(isDetailed);
         toReturn.add(dispoItemArtifact);
      }
      return toReturn;
   }

   @Override
   public Long createDispoProgram(ArtifactReadable author, String name) {
      String normalizedName = "(DISPO)" + name;
      IOseeBranch branch = IOseeBranch.create(normalizedName);

      getBranchFactory().createWorkingBranch(branch, author, dispoParent, ArtifactId.SENTINEL);

      return branch.getId();
   }

   @Override
   public Long createDispoSet(ArtifactReadable author, BranchId branch, DispoSet descriptor) {
      TransactionBuilder tx = getTxFactory().createTransaction(branch, author, "Create Dispo Set");
      ArtifactId creatdArtId = tx.createArtifact(DispoConstants.DispoSet, descriptor.getName());
      tx.setSoleAttributeValue(creatdArtId, DispoConstants.ImportPath, descriptor.getImportPath());
      tx.setSoleAttributeValue(creatdArtId, DispoConstants.ImportState, descriptor.getImportState());
      tx.setSoleAttributeValue(creatdArtId, DispoConstants.DispoType, descriptor.getDispoType());
      tx.setSoleAttributeValue(creatdArtId, DispoConstants.DispoNotesJson, JsonUtil.toJson(descriptor.getNotesList()));
      if (descriptor.getCiSet() == null) {
         tx.setSoleAttributeValue(creatdArtId, DispoConstants.DispoCiSet, "NOCI");
         tx.setSoleAttributeValue(creatdArtId, DispoConstants.DispoRerunList, "NOCI");
         tx.setSoleAttributeValue(creatdArtId, DispoConstants.DispoTime, new Date());
      } else {
         tx.setSoleAttributeValue(creatdArtId, DispoConstants.DispoCiSet, descriptor.getCiSet());
         tx.setSoleAttributeValue(creatdArtId, DispoConstants.DispoRerunList, descriptor.getRerunList());
         tx.setSoleAttributeValue(creatdArtId, DispoConstants.DispoTime, descriptor.getTime());
      }

      tx.commit();
      return creatdArtId.getUuid();
   }

   @Override
   public boolean deleteDispoItem(ArtifactReadable author, BranchId branch, String itemId) {
      return deleteDispoEntityArtifact(author, branch, itemId, DispoConstants.DispoItem);
   }

   @Override
   public boolean deleteDispoSet(ArtifactReadable author, BranchId branch, String setId) {
      return deleteDispoEntityArtifact(author, branch, setId, DispoConstants.DispoSet);
   }

   private boolean deleteDispoEntityArtifact(ArtifactReadable author, BranchId branch, String entityId, IArtifactType type) {
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
   public void updateDispoSet(ArtifactReadable author, BranchId branch, String setId, DispoSet newData) {
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
         tx.setSoleAttributeFromString(dispoSet, DispoConstants.ImportPath, importPath);
      }
      if (notesList != null && !notesList.equals(origSetAs.getNotesList())) {
         tx.setSoleAttributeFromString(dispoSet, DispoConstants.DispoNotesJson,
            JsonUtil.toJson(origSetAs.getNotesList()));
      }
      if (ciSet != null && !ciSet.equals(origSetAs.getCiSet())) {
         tx.setSoleAttributeFromString(dispoSet, DispoConstants.DispoCiSet, ciSet);
      }
      if (rerunList != null && !rerunList.equals(origSetAs.getRerunList())) {
         tx.setSoleAttributeFromString(dispoSet, DispoConstants.DispoRerunList, rerunList);
      }
      if (time != null && !time.equals(origSetAs.getTime())) {
         tx.setSoleAttributeValue(dispoSet, DispoConstants.DispoTime, time);
      }
      tx.commit();
   }

   @Override
   public void createDispoItems(ArtifactReadable author, BranchId branch, DispoSet parentSet, List<DispoItem> data) {
      ArtifactReadable parentSetArt = findDispoArtifact(branch, parentSet.getGuid());
      TransactionBuilder tx = getTxFactory().createTransaction(branch, author, "Create Dispoable Item");

      for (DispoItem item : data) {
         ArtifactId createdItem = tx.createArtifact(DispoConstants.DispoItem, item.getName());

         if (item.getCreationDate() == null) {
            tx.setSoleAttributeValue(createdItem, DispoConstants.DispoDateCreated, new Date());
         } else {
            tx.setSoleAttributeValue(createdItem, DispoConstants.DispoDateCreated, item.getCreationDate());
         }
         tx.setSoleAttributeValue(createdItem, DispoConstants.DispoLastUpdated, item.getLastUpdate());
         tx.setSoleAttributeValue(createdItem, DispoConstants.DispoItemStatus, item.getStatus());
         tx.setSoleAttributeValue(createdItem, DispoConstants.DispoItemTotalPoints, item.getTotalPoints());

         tx.setSoleAttributeValue(createdItem, DispoConstants.DispoItemNeedsRerun, item.getNeedsRerun());
         tx.setSoleAttributeValue(createdItem, DispoConstants.DispoItemAborted, item.getAborted());
         tx.setSoleAttributeValue(createdItem, DispoConstants.DispoDiscrepanciesJson,
            JsonUtil.toJson(item.getDiscrepanciesList()));
         tx.setSoleAttributeValue(createdItem, DispoConstants.DispoAnnotationsJson,
            JsonUtil.toJson(item.getAnnotationsList()));
         tx.setSoleAttributeValue(createdItem, DispoConstants.DispoItemVersion, item.getVersion());
         tx.setSoleAttributeValue(createdItem, DispoConstants.DispoItemAssignee, item.getAssignee());
         tx.setSoleAttributeValue(createdItem, DispoConstants.DispoItemMachine, item.getMachine());
         tx.setSoleAttributeValue(createdItem, DispoConstants.DispoItemCategory, item.getCategory());
         tx.setSoleAttributeValue(createdItem, DispoConstants.DispoItemElapsedTime, item.getElapsedTime());

         if (Strings.isValid(item.getFileNumber())) {
            tx.setSoleAttributeValue(createdItem, DispoConstants.DispoItemFileNumber, item.getFileNumber());
         }
         if (Strings.isValid(item.getMethodNumber())) {
            tx.setSoleAttributeValue(createdItem, DispoConstants.DispoItemMethodNumber, item.getMethodNumber());
         }
         tx.relate(parentSetArt, CoreRelationTypes.Default_Hierarchical__Child, createdItem);
      }
      tx.commit();
   }

   private void updateSingleItem(ArtifactReadable author, BranchId branch, ArtifactReadable currentItemArt, DispoItem newItemData, TransactionBuilder tx, boolean resetRerunFlag, DispoStorageMetadata metadata) {
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
         tx.setSoleAttributeFromString(currentItemArt, DispoConstants.DispoDiscrepanciesJson,
            JsonUtil.toJson(newDiscrepancies));
      }
      if (newAnnotations != null && !newAnnotations.equals(origItem.getAnnotationsList())) {
         tx.setSoleAttributeFromString(currentItemArt, DispoConstants.DispoAnnotationsJson,
            JsonUtil.toJson(newAnnotations));
      }
      if (assignee != null && !assignee.equals(origItem.getAssignee())) {
         tx.setSoleAttributeFromString(currentItemArt, DispoConstants.DispoItemAssignee, assignee);
      }
      if (status != null && !status.equals(origItem.getStatus())) {
         metadata.addIdOfUpdatedItem(newItemData.getGuid());
         tx.setSoleAttributeFromString(currentItemArt, DispoConstants.DispoItemStatus, status);
      }
      if (lastUpdate != null && !lastUpdate.equals(origItem.getLastUpdate())) {
         tx.setSoleAttributeValue(currentItemArt, DispoConstants.DispoLastUpdated, lastUpdate);
      }
      if (needsRerun != null && !needsRerun.equals(origItem.getNeedsRerun())) {
         tx.setSoleAttributeValue(currentItemArt, DispoConstants.DispoItemNeedsRerun, needsRerun.booleanValue());
      }
      if (totalPoints != null && !totalPoints.equals(origItem.getTotalPoints())) {
         tx.setSoleAttributeFromString(currentItemArt, DispoConstants.DispoItemTotalPoints, totalPoints);
      }
      if (machine != null && !machine.equals(origItem.getMachine())) {
         tx.setSoleAttributeFromString(currentItemArt, DispoConstants.DispoItemMachine, machine);
      }
      if (category != null && !category.equals(origItem.getCategory())) {
         tx.setSoleAttributeFromString(currentItemArt, DispoConstants.DispoItemCategory, category);
      }
      if (elapsedTime != null && !elapsedTime.equals(origItem.getElapsedTime())) {
         tx.setSoleAttributeFromString(currentItemArt, DispoConstants.DispoItemElapsedTime, elapsedTime);
      }
      if (aborted != null && !aborted.equals(origItem.getAborted())) {
         tx.setSoleAttributeValue(currentItemArt, DispoConstants.DispoItemAborted, aborted);
      }
      if (itemNotes != null && !itemNotes.equals(origItem.getItemNotes())) {
         tx.setSoleAttributeFromString(currentItemArt, DispoConstants.DispoItemItemNotes, itemNotes);
      }
      if (fileNumber != null && !fileNumber.equals(origItem.getFileNumber())) {
         tx.setSoleAttributeFromString(currentItemArt, DispoConstants.DispoItemFileNumber, fileNumber);
      }
      if (methodNumber != null && !methodNumber.equals(origItem.getMethodNumber())) {
         tx.setSoleAttributeFromString(currentItemArt, DispoConstants.DispoItemMethodNumber, methodNumber);
      }
      if (team != null && !team.equals(origItem.getTeam())) {
         tx.setSoleAttributeFromString(currentItemArt, DispoConstants.DispoItemTeam, team);
      }
   }

   @Override
   public void updateDispoItem(ArtifactReadable author, BranchId branch, String dispoItemId, DispoItem data, DispoStorageMetadata metadata) {
      TransactionBuilder tx = getTxFactory().createTransaction(branch, author, "Update Dispo Item");
      ArtifactReadable dispoItemArt = findDispoArtifact(branch, dispoItemId);
      updateSingleItem(author, branch, dispoItemArt, data, tx, false, metadata);
      tx.commit();
   }

   @Override
   public void updateDispoItems(ArtifactReadable author, BranchId branch, Collection<DispoItem> data, boolean resetRerunFlag, String operation, DispoStorageMetadata metadata) {
      TransactionBuilder tx = getTxFactory().createTransaction(branch, author, operation);
      boolean isCommitNeeded = false;

      for (DispoItem newItem : data) {
         String itemId = newItem.getGuid();
         if (Strings.isValid(itemId)) {
            isCommitNeeded = true;
            ArtifactReadable dispoItemArt = findDispoArtifact(branch, newItem.getGuid());
            updateSingleItem(author, branch, dispoItemArt, newItem, tx, resetRerunFlag, metadata);
         }
      }

      if (isCommitNeeded) {
         tx.commit();
      }
   }

   @Override
   public List<IOseeBranch> getDispoBranches() {
      List<IOseeBranch> dispoBranchesNormalized = new ArrayList<>();

      ResultSet<IOseeBranch> dispoBranches =
         getQuery().branchQuery().andIsOfType(BranchType.WORKING).andIsChildOf(dispoParent).getResultsAsId();

      for (IOseeBranch branch : dispoBranches) {
         IOseeBranch newName = IOseeBranch.create(branch, branch.getName().replaceFirst("\\(DISPO\\)", ""));

         dispoBranchesNormalized.add(newName);
      }

      return dispoBranchesNormalized;
   }

   @Override
   public Collection<DispoItem> findDispoItemByAnnoationText(BranchId branch, String setId, String keyword, boolean isDetailed) {
      ArtifactReadable dispoSetArt = findDispoArtifact(branch, setId);

      Set<DispoItem> toReturn = new HashSet<>();
      ResultSet<ArtifactReadable> dispoArtifacts = getQuery()//
         .fromBranch(branch)//
         .andTypeEquals(DispoConstants.DispoItem)//
         .andRelatedTo(CoreRelationTypes.Default_Hierarchical__Parent, dispoSetArt).and(
            DispoConstants.DispoAnnotationsJson, keyword, //
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
   public String createDispoReport(BranchId branch, ArtifactReadable author, String contents, String operationTitle) {
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
         if (!descendant.getAttributeValues(CoverageUtil.Item).isEmpty()) {
            ArtifactReadable parent = descendant.getParent();
            String fullName = String.format("%s.%s", parent.getName(), descendant.getName());
            toReturn.put(fullName, descendant);
         }
      }
      return toReturn;
   }

   @Override
   public void updateOperationSummary(ArtifactReadable author, BranchId branch, String setId, OperationReport summary) {
      OperationReport newReport = DispoUtil.cleanOperationReport(summary);
      ArtifactReadable dispoSet = findDispoArtifact(branch, setId);
      TransactionBuilder tx = getTxFactory().createTransaction(branch, author, "Update Dispo Operation Report");

      tx.setSoleAttributeFromString(dispoSet, DispoConstants.ImportState, newReport.getStatus().getName());
      tx.setSoleAttributeFromString(dispoSet, DispoConstants.OperationSummary,
         DispoUtil.operationReportToString(newReport));
      tx.commit();
   }

   @Override
   public Long getDispoItemParentSet(BranchId branch, String itemId) {
      ArtifactReadable artifact = findDispoArtifact(branch, itemId);
      return artifact.getParent().getUuid();
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
            String ciSet = dispoSet.getSoleAttributeValue(DispoConstants.DispoCiSet, "");
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
         getQuery().branchQuery().andNameEquals("Dispo Parent").getResults().getOneOrDefault(Branch.getSentinel());
      return getQuery().branchQuery().andIsChildOf(
         dispoParent).excludeArchived().excludeDeleted().getResults().getList();
   }

   private List<ArtifactReadable> findAllCiSets(BranchId branch) {
      return getQuery().fromBranch(branch).andIsOfType(DispoConstants.DispoSet).andExists(
         DispoConstants.DispoCiSet).getResults().getList();
   }

   @Override
   public ArtifactReadable findUserByName(String name) {
      ArtifactReadable user =
         getQuery().fromBranch(COMMON).andTypeEquals(CoreArtifactTypes.User).and(CoreAttributeTypes.Name,
            name).getResults().getOneOrDefault(ArtifactReadable.SENTINEL);
      if (user.isInvalid()) {
         user = findUser();
      }
      return user;
   }
}
