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

import static org.eclipse.osee.disposition.model.DispoStrings.Dispo_Config_Art;
import static org.eclipse.osee.disposition.rest.DispoConstants.DispoTypesArtifact;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.OseeTypeDefinition;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.UriGeneralStringData;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoProgram;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.rest.DispoConstants;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactId;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.BranchReadable;
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

   private IOseeBranch getAdminBranch() {
      return CoreBranches.COMMON;
   }

   @SuppressWarnings("unchecked")
   private ArtifactReadable getDispoUser() throws OseeCoreException {
      return getQuery().fromBranch(getAdminBranch()).andIds(SystemUser.OseeSystem).getResults().getExactlyOne();
   }

   private ApplicationContext getContext() {
      return null;
   }

   private QueryFactory getQuery() {
      return orcsApi.getQueryFactory(getContext());
   }

   private TransactionFactory getTxFactory() {
      return orcsApi.getTransactionFactory(getContext());
   }

   private void reloadTypes() {
      orcsApi.getOrcsTypes(getContext()).invalidateAll();
   }

   @SuppressWarnings("unchecked")
   private ResultSet<ArtifactReadable> getDispoTypesArtifact() throws OseeCoreException {
      return getQuery().fromBranch(getAdminBranch()).andIds(DispoConstants.DispoTypesArtifact).getResults();
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
      ArtifactId artifactId = DispoTypesArtifact;
      if (!typesExist()) {
         tx.createArtifact(OseeTypeDefinition, artifactId.getName(), artifactId.getGuid());
      }
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
      return getQuery().fromBranch(getAdminBranch()).andNameEquals("UnAssigned").andTypeEquals(CoreArtifactTypes.User).getResults().getExactlyOne();
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
   public List<DispoSet> findDispoSets(DispoProgram program) {
      ResultSet<ArtifactReadable> results = getQuery()//
      .fromBranch(program.getUuid())//
      .andTypeEquals(DispoConstants.DispoSet)//
      .getResults();

      List<DispoSet> toReturn = new ArrayList<DispoSet>();
      for (ArtifactReadable art : results) {
         toReturn.add(new DispoSetArtifact(art));
      }
      return toReturn;
   }

   @Override
   public DispoSet findDispoSetsById(DispoProgram program, String setId) {
      ArtifactReadable result = findDispoArtifact(program, setId, DispoConstants.DispoSet);
      return new DispoSetArtifact(result);
   }

   private ArtifactReadable findDispoArtifact(DispoProgram program, String setId, IArtifactType type) {
      return getQuery()//
      .fromBranch(program.getUuid())//
      .andTypeEquals(type)//
      .andGuid(setId)//
      .getResults().getOneOrNull();
   }

   @Override
   public List<DispoItem> findDipoItems(DispoProgram program, String setId) {
      ArtifactReadable setArt = findDispoArtifact(program, setId, DispoConstants.DispoSet);
      ResultSet<ArtifactReadable> results = setArt.getChildren();

      List<DispoItem> toReturn = new ArrayList<DispoItem>();
      for (ArtifactReadable art : results) {
         DispoItemArtifact dispoItemArtifact = new DispoItemArtifact(art);
         dispoItemArtifact.getAborted();
         toReturn.add(dispoItemArtifact);
      }
      return toReturn;
   }

   @Override
   public Identifiable<String> createDispoSet(ArtifactReadable author, DispoProgram program, DispoSet descriptor) {
      TransactionBuilder tx = getTxFactory().createTransaction(program.getUuid(), author, "Create Dispo Set");
      ArtifactId creatdArtId = tx.createArtifact(DispoConstants.DispoSet, descriptor.getName());
      tx.setSoleAttributeFromString(creatdArtId, DispoConstants.ImportPath, descriptor.getImportPath());
      tx.setSoleAttributeFromString(creatdArtId, DispoConstants.ImportState, descriptor.getImportState());
      tx.setSoleAttributeFromString(creatdArtId, DispoConstants.DispoType, descriptor.getDispoType());
      tx.setSoleAttributeFromString(creatdArtId, DispoConstants.DispoNotesJson, descriptor.getNotesList().toString());
      tx.commit();
      return creatdArtId;
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
      JSONArray notesList = newData.getNotesList();

      TransactionBuilder tx = getTxFactory().createTransaction(program.getUuid(), author, "Delete Dispo Set");
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
         tx.setSoleAttributeFromString(createdItem, DispoConstants.DispoDiscrepanciesJson,
            item.getDiscrepanciesList().toString());
         tx.setSoleAttributeFromString(createdItem, DispoConstants.DispoAnnotationsJson,
            item.getAnnotationsList().toString());
         tx.setSoleAttributeFromString(createdItem, DispoConstants.DispoItemVersion, item.getVersion());
         tx.setSoleAttributeFromString(createdItem, DispoConstants.DispoItemAssignee, assignee);
         tx.setSoleAttributeFromString(createdItem, DispoConstants.DispoItemMachine, item.getMachine());
         tx.setSoleAttributeFromString(createdItem, DispoConstants.DispoItemCategory, item.getCategory());
         tx.setSoleAttributeFromString(createdItem, DispoConstants.DispoItemElapsedTime, item.getElapsedTime());
         tx.setSoleAttributeValue(createdItem, DispoConstants.DispoItemAborted, item.getAborted());

         tx.relate(parentSetArt, CoreRelationTypes.Default_Hierarchical__Child, createdItem);
      }
      tx.commit();
   }

   private void updateSingleItem(ArtifactReadable author, DispoProgram program, ArtifactReadable currentItemArt, DispoItem newItemData, TransactionBuilder tx, boolean resetRerunFlag) {
      Date lastUpdate = newItemData.getLastUpdate();
      String name = newItemData.getName();
      JSONObject discrepanciesList = newItemData.getDiscrepanciesList();
      JSONArray annotationsList = newItemData.getAnnotationsList();
      String status = newItemData.getStatus();
      String assignee = newItemData.getAssignee();
      String totalPoints = newItemData.getTotalPoints();
      String machine = newItemData.getMachine();
      String category = newItemData.getCategory();
      String elapsedTime = newItemData.getElapsedTime();
      Boolean aborted = newItemData.getAborted();
      String itemNotes = newItemData.getItemNotes();

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

   }

   @Override
   public void updateDispoItem(ArtifactReadable author, DispoProgram program, String dispoItemId, DispoItem data) {
      IOseeBranch branch = TokenFactory.createBranch(program.getUuid(), "");
      TransactionBuilder tx = getTxFactory().createTransaction(branch, author, "Edit Dispo Item");
      ArtifactReadable dispoItemArt = findDispoArtifact(program, dispoItemId, DispoConstants.DispoItem);
      updateSingleItem(author, program, dispoItemArt, data, tx, false);
      tx.commit();
   }

   @Override
   public void updateDispoItems(ArtifactReadable author, DispoProgram program, List<DispoItem> data, boolean resetRerunFlag) {
      TransactionBuilder tx = getTxFactory().createTransaction(program.getUuid(), author, "Edit Multiple Dispo Items");

      for (DispoItem newItem : data) {
         ArtifactReadable dispoItemArt = findDispoArtifact(program, newItem.getGuid(), DispoConstants.DispoItem);
         updateSingleItem(author, program, dispoItemArt, newItem, tx, resetRerunFlag);
      }

      tx.commit();
   }

   private String getDispoConfigContents() {
      BranchReadable branchRead = getQuery().branchQuery().andIds(CoreBranches.COMMON).getResults().getExactlyOne();

      ArtifactReadable configArt =
         getQuery().fromBranch(branchRead).andNameEquals(Dispo_Config_Art).getResults().getExactlyOne();

      return configArt.getSoleAttributeAsString(CoreAttributeTypes.GeneralStringData);
   }

   private IOseeBranch convertToDispoBranch(String configContents, IOseeBranch baselineBranch) {
      IOseeBranch toReturn = null;

      Pattern regex = Pattern.compile("(\\n|^+)" + baselineBranch.getUuid() + "\\s*:\\s*.*");
      Matcher matcher = regex.matcher(configContents);

      Long uuid = null;
      if (matcher.find()) {
         String match = matcher.group();
         String[] split = match.split(":");
         uuid = Long.valueOf(split[1]);
      }
      if (uuid != null) {
         toReturn = TokenFactory.createBranch(uuid, baselineBranch.getName());
      }
      return toReturn;
   }

   @Override
   public ResultSet<IOseeBranch> getDispoBranches() {
      ResultSet<BranchReadable> baselineBranches =
         getQuery().branchQuery().andIsOfType(BranchType.BASELINE).getResults();

      String configContents = getDispoConfigContents();

      List<IOseeBranch> results = new ArrayList<IOseeBranch>();
      for (BranchReadable baselineBranch : baselineBranches) {
         IOseeBranch dispoBranch = convertToDispoBranch(configContents, baselineBranch);
         if (dispoBranch != null) {
            results.add(dispoBranch);
         }
      }
      return ResultSets.newResultSet(results);
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
}
