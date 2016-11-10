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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.disposition.model.DispoConfig;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoProgram;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.OperationReport;
import org.eclipse.osee.disposition.rest.DispoConstants;
import org.eclipse.osee.disposition.rest.internal.importer.coverage.CoverageUtil;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.enums.SystemUser;
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
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Angel Avila
 */
public class OrcsStorageImpl implements Storage {
   private final Log logger;
   private final OrcsApi orcsApi;

   public OrcsStorageImpl(Log logger, OrcsApi orcsApi) {
      this.logger = logger;
      this.orcsApi = orcsApi;
   }

   private BranchId getAdminBranch() {
      return CoreBranches.COMMON;
   }

   @SuppressWarnings("unchecked")
   private ArtifactReadable getDispoUser() throws OseeCoreException {
      return getQuery().fromBranch(getAdminBranch()).andIds(SystemUser.OseeSystem).getResults().getExactlyOne();
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

   private ResultSet<ArtifactReadable> getDispoTypesArtifact() throws OseeCoreException {
      return getQuery().fromBranch(getAdminBranch()).andUuid(DISPO_ARTIFACT.getUuid()).andTypeEquals(
         DISPO_ARTIFACT.getArtifactType()).getResults();
   }

   @Override
   public boolean typesExist() {
      boolean result = false;
      try {
         result = !getDispoTypesArtifact().isEmpty();
      } catch (OseeCoreException ex) {
         logger.warn(ex, "Error checking for Dispo Types");
      }
      return result;
   }

   @Override
   public void storeTypes(IResource resource) {
      TransactionBuilder tx =
         getTxFactory().createTransaction(getAdminBranch(), getDispoUser(), "Initialize Dispo Types");
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

   @SuppressWarnings("unchecked")
   @Override
   public ArtifactReadable findUser() {
      return getQuery().fromBranch(getAdminBranch()).andIds(SystemUser.OseeSystem).getResults().getExactlyOne();
   }

   @Override
   public ArtifactReadable findUser(String userId) {
      return getQuery().fromBranch(getAdminBranch()).andGuid(userId).getResults().getExactlyOne();
   }

   @Override
   public ArtifactReadable findUnassignedUser() {
      return getQuery().fromBranch(getAdminBranch()).andNameEquals("UnAssigned").andTypeEquals(
         CoreArtifactTypes.User).getResults().getExactlyOne();
   }

   @Override
   public boolean isUniqueProgramName(String name) {
      ResultSet<BranchReadable> results = getQuery().branchQuery().andNameEquals(name).getResults();

      return results.isEmpty();
   }

   @Override
   public boolean isUniqueSetName(DispoProgram program, String name) {
      ResultSet<ArtifactReadable> results = getQuery()//
         .fromBranch(program.getUuid())//
         .andTypeEquals(DispoConstants.DispoSet)//
         .andNameEquals(name)//
         .getResults();

      return results.isEmpty();
   }

   @Override
   public boolean isUniqueItemName(DispoProgram program, String setId, String name) {
      ArtifactReadable setArt = findDispoArtifact(program, setId, DispoConstants.DispoSet);
      ResultSet<ArtifactReadable> results = getQuery()//
         .fromBranch(program.getUuid())//
         .andRelatedTo(CoreRelationTypes.Default_Hierarchical__Parent, setArt)//
         .andTypeEquals(DispoConstants.DispoItem)//
         .andNameEquals(name)//
         .getResults();

      return results.isEmpty();
   }

   @Override
   public List<DispoSet> findDispoSets(DispoProgram program, String type) {
      ResultSet<ArtifactReadable> results = getQuery()//
         .fromBranch(program.getUuid())//
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
   public DispoSet findDispoSetsById(DispoProgram program, String setId) {
      ArtifactReadable result = findDispoArtifact(program, setId, DispoConstants.DispoSet);
      return new DispoSetArtifact(result);
   }

   private ArtifactReadable findDispoArtifact(DispoProgram program, String artId, IArtifactType type) {
      return getQuery()//
         .fromBranch(program.getUuid())//
         .andUuid(Long.valueOf(artId))//
         .getResults().getOneOrNull();
   }

   @Override
   public List<DispoItem> findDipoItems(DispoProgram program, String setId, boolean isDetailed) {
      ArtifactReadable setArt = findDispoArtifact(program, setId, DispoConstants.DispoSet);
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
      BranchReadable dispoBranch = getQuery().branchQuery().andNameEquals("Dispo Parent").getResults().getExactlyOne();
      IOseeBranch branch = TokenFactory.createBranch(normalizedName);

      try {
         getBranchFactory().createWorkingBranch(branch, author, dispoBranch, null).call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }

      return branch.getGuid();
   }

   @Override
   public Long createDispoSet(ArtifactReadable author, DispoProgram program, DispoSet descriptor) {
      TransactionBuilder tx = getTxFactory().createTransaction(program.getUuid(), author, "Create Dispo Set");
      ArtifactId creatdArtId = tx.createArtifact(DispoConstants.DispoSet, descriptor.getName());
      tx.setSoleAttributeFromString(creatdArtId, DispoConstants.ImportPath, descriptor.getImportPath());
      tx.setSoleAttributeFromString(creatdArtId, DispoConstants.ImportState, descriptor.getImportState());
      tx.setSoleAttributeFromString(creatdArtId, DispoConstants.DispoType, descriptor.getDispoType());
      JSONArray notesJarray = DispoUtil.noteListToJsonObj(descriptor.getNotesList());
      tx.setSoleAttributeFromString(creatdArtId, DispoConstants.DispoNotesJson, notesJarray.toString());
      tx.commit();
      return creatdArtId.getUuid();
   }

   @Override
   public boolean deleteDispoItem(ArtifactReadable author, DispoProgram program, String itemId) {
      return deleteDispoEntityArtifact(author, program, itemId, DispoConstants.DispoItem);
   }

   @Override
   public boolean deleteDispoSet(ArtifactReadable author, DispoProgram program, String setId) {
      return deleteDispoEntityArtifact(author, program, setId, DispoConstants.DispoSet);
   }

   private boolean deleteDispoEntityArtifact(ArtifactReadable author, DispoProgram program, String entityId, IArtifactType type) {
      boolean toReturn = false;
      ArtifactReadable dispoArtifact = findDispoArtifact(program, entityId, type);
      if (dispoArtifact != null) {
         TransactionBuilder tx = getTxFactory().createTransaction(program.getUuid(), author, "Delete Dispo Artifact");
         tx.deleteArtifact(dispoArtifact);
         tx.commit();
         toReturn = true;
      }

      return toReturn;
   }

   @Override
   public void updateDispoSet(ArtifactReadable author, DispoProgram program, String setId, DispoSet newData) {
      ArtifactReadable dispoSet = findDispoArtifact(program, setId, DispoConstants.DispoSet);
      DispoSetArtifact origSetAs = new DispoSetArtifact(dispoSet);

      String name = newData.getName();
      String importPath = newData.getImportPath();

      JSONArray notesList = null;
      if (newData.getNotesList() != null) {
         notesList = DispoUtil.noteListToJsonObj(newData.getNotesList());
      }

      TransactionBuilder tx = getTxFactory().createTransaction(program.getUuid(), author, "Update Dispo Set");
      if (name != null && !name.equals(origSetAs.getName())) {
         tx.setName(dispoSet, name);
      }
      if (importPath != null && !importPath.equals(origSetAs.getImportPath())) {
         tx.setSoleAttributeFromString(dispoSet, DispoConstants.ImportPath, importPath);
      }
      if (notesList != null && !notesList.toString().equals(origSetAs.getNotesList().toString())) {
         tx.setSoleAttributeFromString(dispoSet, DispoConstants.DispoNotesJson, notesList.toString());
      }
      tx.commit();
   }

   @Override
   public void createDispoItems(ArtifactReadable author, DispoProgram program, DispoSet parentSet, List<DispoItem> data, String assignee) {
      ArtifactReadable parentSetArt = findDispoArtifact(program, parentSet.getGuid(), DispoConstants.DispoSet);
      TransactionBuilder tx = getTxFactory().createTransaction(program.getUuid(), author, "Create Dispoable Item");

      for (DispoItem item : data) {
         ArtifactId createdItem = tx.createArtifact(DispoConstants.DispoItem, item.getName());

         tx.setSoleAttributeValue(createdItem, DispoConstants.DispoDateCreated, item.getCreationDate());
         tx.setSoleAttributeValue(createdItem, DispoConstants.DispoLastUpdated, item.getLastUpdate());
         tx.setSoleAttributeValue(createdItem, DispoConstants.DispoItemStatus, item.getStatus());
         tx.setSoleAttributeValue(createdItem, DispoConstants.DispoItemTotalPoints, item.getTotalPoints());
         tx.setSoleAttributeValue(createdItem, DispoConstants.DispoItemNeedsRerun, item.getNeedsRerun());

         // Need to convert to Json String
         String discrepanciesAsJsonString = DispoUtil.disrepanciesMapToJson(item.getDiscrepanciesList()).toString();
         tx.setSoleAttributeFromString(createdItem, DispoConstants.DispoDiscrepanciesJson, discrepanciesAsJsonString);
         String annotationsAsJsonString = DispoUtil.annotationsListToJson(item.getAnnotationsList()).toString();
         tx.setSoleAttributeFromString(createdItem, DispoConstants.DispoAnnotationsJson, annotationsAsJsonString);
         // End

         tx.setSoleAttributeFromString(createdItem, DispoConstants.DispoItemVersion, item.getVersion());
         tx.setSoleAttributeFromString(createdItem, DispoConstants.DispoItemAssignee, assignee);
         tx.setSoleAttributeFromString(createdItem, DispoConstants.DispoItemMachine, item.getMachine());
         tx.setSoleAttributeFromString(createdItem, DispoConstants.DispoItemCategory, item.getCategory());
         tx.setSoleAttributeFromString(createdItem, DispoConstants.DispoItemElapsedTime, item.getElapsedTime());
         tx.setSoleAttributeValue(createdItem, DispoConstants.DispoItemAborted, item.getAborted());

         if (Strings.isValid(item.getFileNumber())) {
            tx.setSoleAttributeFromString(createdItem, DispoConstants.DispoItemFileNumber, item.getFileNumber());
         }
         if (Strings.isValid(item.getMethodNumber())) {
            tx.setSoleAttributeFromString(createdItem, DispoConstants.DispoItemMethodNumber, item.getMethodNumber());
         }
         tx.relate(parentSetArt, CoreRelationTypes.Default_Hierarchical__Child, createdItem);
      }
      tx.commit();
   }

   private void updateSingleItem(ArtifactReadable author, DispoProgram program, ArtifactReadable currentItemArt, DispoItem newItemData, TransactionBuilder tx, boolean resetRerunFlag) {
      Date lastUpdate = newItemData.getLastUpdate();
      String name = newItemData.getName();

      // Need to convert to Json String
      JSONObject discrepanciesList = null;
      if (newItemData.getDiscrepanciesList() != null) {
         discrepanciesList = DispoUtil.disrepanciesMapToJson(newItemData.getDiscrepanciesList());
      }

      JSONArray annotationsList = null;
      if (newItemData.getAnnotationsList() != null) {
         annotationsList = DispoUtil.annotationsListToJson(newItemData.getAnnotationsList());
      }
      // End

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
      if (discrepanciesList != null && !discrepanciesList.equals(origItem.getDiscrepanciesList())) {
         tx.setSoleAttributeFromString(currentItemArt, DispoConstants.DispoDiscrepanciesJson,
            discrepanciesList.toString());
      }
      if (annotationsList != null && !annotationsList.equals(origItem.getAnnotationsList())) {
         tx.setSoleAttributeFromString(currentItemArt, DispoConstants.DispoAnnotationsJson, annotationsList.toString());
      }
      if (assignee != null && !assignee.equals(origItem.getAssignee())) {
         tx.setSoleAttributeFromString(currentItemArt, DispoConstants.DispoItemAssignee, assignee);
      }
      if (status != null && !status.equals(origItem.getStatus())) {
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

   }

   @Override
   public void updateDispoItem(ArtifactReadable author, DispoProgram program, String dispoItemId, DispoItem data) {
      BranchId branch = TokenFactory.createBranch(program.getUuid());
      TransactionBuilder tx = getTxFactory().createTransaction(branch, author, "Update Dispo Item");
      ArtifactReadable dispoItemArt = findDispoArtifact(program, dispoItemId, DispoConstants.DispoItem);
      updateSingleItem(author, program, dispoItemArt, data, tx, false);
      tx.commit();
   }

   @Override
   public void updateDispoItems(ArtifactReadable author, DispoProgram program, Collection<DispoItem> data, boolean resetRerunFlag, String operation) {
      TransactionBuilder tx = getTxFactory().createTransaction(program.getUuid(), author, operation);

      for (DispoItem newItem : data) {
         ArtifactReadable dispoItemArt = findDispoArtifact(program, newItem.getGuid(), DispoConstants.DispoItem);
         updateSingleItem(author, program, dispoItemArt, newItem, tx, resetRerunFlag);
      }

      tx.commit();
   }

   @Override
   public List<IOseeBranch> getDispoBranches() {
      List<IOseeBranch> dispoBranchesNormalized = new ArrayList<>();
      BranchReadable dispoBranch = getQuery().branchQuery().andNameEquals("Dispo Parent").getResults().getExactlyOne();

      ResultSet<BranchReadable> dispoBranches =
         getQuery().branchQuery().andIsOfType(BranchType.WORKING).andIsChildOf(dispoBranch).getResults();

      for (BranchReadable branch : dispoBranches) {
         IOseeBranch newName =
            TokenFactory.createBranch(branch.getUuid(), branch.getName().replaceFirst("\\(DISPO\\)", ""));

         dispoBranchesNormalized.add(newName);
      }

      return dispoBranchesNormalized;
   }

   @Override
   public Collection<DispoItem> findDispoItemByAnnoationText(DispoProgram program, String setId, String keyword, boolean isDetailed) {
      ArtifactReadable dispoSetArt = findDispoArtifact(program, setId, DispoConstants.DispoSet);

      Set<DispoItem> toReturn = new HashSet<>();
      ResultSet<ArtifactReadable> dispoArtifacts = getQuery()//
         .fromBranch(program.getUuid())//
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
   public DispoItem findDispoItemById(DispoProgram program, String itemId) {
      DispoItem toReturn = null;
      ArtifactReadable dispoArtifact = findDispoArtifact(program, itemId, DispoConstants.DispoItem);
      if (dispoArtifact != null) {
         toReturn = new DispoItemArtifact(dispoArtifact);
      }

      return toReturn;
   }

   @Override
   public DispoConfig findDispoConfig(DispoProgram program) {
      ArtifactReadable config =
         getQuery().fromBranch(program.getUuid()).andNameEquals("Program Config").getResults().getOneOrNull();

      if (config == null) {
         return DispoUtil.getDefaultConfig();
      }
      return DispoUtil.configArtToConfigData(new DispoConfigArtifact(config));
   }

   @Override
   public String createDispoReport(DispoProgram program, ArtifactReadable author, String contents, String operationTitle) {
      String toReturn = "";

      TransactionBuilder tx =
         getTxFactory().createTransaction(program.getUuid(), author, "Update Report: " + operationTitle);

      ArtifactReadable reportArt =
         getQuery().fromBranch(program.getUuid()).andNameEquals("Dispo_Report").getResults().getOneOrNull();

      if (reportArt == null) {
         TransactionBuilder txToCreate =
            getTxFactory().createTransaction(program.getUuid(), author, "Add Operation Report Art");
         txToCreate.createArtifact(CoreArtifactTypes.GeneralData, "Dispo_Report");
         txToCreate.commit();
         reportArt =
            getQuery().fromBranch(program.getUuid()).andNameEquals("Dispo_Report").getResults().getExactlyOne();
      }

      tx.setSoleAttributeFromString(reportArt, CoreAttributeTypes.GeneralStringData, contents);
      TransactionReadable commit = tx.commit();
      if (commit != null) {

         ArtifactReadable newRerpotArt = getQuery().fromBranch(program.getUuid()).fromTransaction(commit).andGuid(
            reportArt.getGuid()).getResults().getExactlyOne();

         AttributeReadable<Object> contentsAsAttribute =
            newRerpotArt.getAttributes(CoreAttributeTypes.GeneralStringData).getExactlyOne();

         toReturn = String.format("/orcs/branch/%s/artifact/%s/attribute/%s/version/%s", program.getUuid(),
            newRerpotArt.getGuid(), contentsAsAttribute.getLocalId(), commit.getId());
      }
      return toReturn;

   }

   @Override
   public Map<String, ArtifactReadable> getCoverageUnits(long branchUuid, Long artifactUuid) {
      ArtifactReadable coveragePackage =
         getQuery().fromBranch(branchUuid).andUuid(artifactUuid).getResults().getOneOrNull();

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
   public void updateOperationSummary(ArtifactReadable author, DispoProgram program, DispoSet set, OperationReport summary) {
      OperationReport newReport = DispoUtil.cleanOperationReport(summary);
      ArtifactReadable dispoSet = findDispoArtifact(program, set.getGuid(), DispoConstants.DispoSet);
      TransactionBuilder tx =
         getTxFactory().createTransaction(program.getUuid(), author, "Update Dispo Operation Report");

      tx.setSoleAttributeFromString(dispoSet, DispoConstants.ImportState, newReport.getStatus().getName());
      tx.setSoleAttributeFromString(dispoSet, DispoConstants.OperationSummary,
         DispoUtil.operationReportToString(newReport));
      tx.commit();
   }
}
