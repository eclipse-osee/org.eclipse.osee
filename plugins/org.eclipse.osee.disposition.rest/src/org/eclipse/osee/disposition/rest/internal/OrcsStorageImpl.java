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

   @SuppressWarnings("unchecked")
   @Override
   public ArtifactReadable findUnassignedUser() {
      //      return getQuery().fromBranch(getAdminBranch()).andNameEquals("Unassigned").getResults().getExactlyOne(); COMMENTED OUT TEMP
      return getQuery().fromBranch(getAdminBranch()).andIds(SystemUser.OseeSystem).getResults().getExactlyOne();

   }

   @Override
   public boolean isUniqueSetName(DispoProgram program, String name) {
      IOseeBranch branch = getProgramBranch(program);
      ResultSet<ArtifactReadable> results = getQuery()//
      .fromBranch(branch)//
      .andTypeEquals(DispoConstants.DispoSet)//
      .andNameEquals(name)//
      .getResults();

      return results.isEmpty();
   }

   private IOseeBranch getProgramBranch(DispoProgram program) {
      return TokenFactory.createBranch(program.getUuid(), program.getName());
   }

   @Override
   public boolean isUniqueItemName(DispoProgram program, String setId, String name) {
      IOseeBranch branch = getProgramBranch(program);
      ArtifactReadable setArt = findDispoArtifact(branch, setId, DispoConstants.DispoSet);
      ResultSet<ArtifactReadable> results = getQuery()//
      .fromBranch(branch)//
      .andRelatedTo(CoreRelationTypes.Default_Hierarchical__Parent, setArt)//
      .andTypeEquals(DispoConstants.DispoItem)//
      .andNameEquals(name)//
      .getResults();

      return results.isEmpty();
   }

   @Override
   public ResultSet<DispoSet> findDispoSets(DispoProgram program) {
      IOseeBranch branch = getProgramBranch(program);
      ResultSet<ArtifactReadable> results = getQuery()//
      .fromBranch(branch)//
      .andTypeEquals(DispoConstants.DispoSet)//
      .getResults();

      List<DispoSet> toReturn = new ArrayList<DispoSet>();
      for (ArtifactReadable art : results) {
         toReturn.add(new DispoSetArtifact(art));
      }
      return ResultSets.newResultSet(toReturn);
   }

   @Override
   public DispoSet findDispoSetsById(DispoProgram program, String setId) {
      IOseeBranch branch = getProgramBranch(program);
      ArtifactReadable result = findDispoArtifact(branch, setId, DispoConstants.DispoSet);
      return new DispoSetArtifact(result);
   }

   private ArtifactReadable findDispoArtifact(IOseeBranch branch, String setId, IArtifactType type) {
      return getQuery()//
      .fromBranch(branch)//
      .andTypeEquals(type)//
      .andGuid(setId)//
      .getResults().getOneOrNull();
   }

   @Override
   public ResultSet<DispoItem> findDipoItems(DispoProgram program, String setId) {
      IOseeBranch branch = getProgramBranch(program);
      ArtifactReadable setArt = findDispoArtifact(branch, setId, DispoConstants.DispoSet);
      ResultSet<ArtifactReadable> results = setArt.getRelated(CoreRelationTypes.Default_Hierarchical__Child);

      List<DispoItem> toReturn = new ArrayList<DispoItem>();
      for (ArtifactReadable art : results) {
         toReturn.add(new DispoItemArtifact(art));
      }
      return ResultSets.newResultSet(toReturn);
   }

   @Override
   public DispoItem findDispoItemById(DispoProgram program, String itemId) {
      DispoItem toReturn = null;
      IOseeBranch branch = getProgramBranch(program);
      ArtifactReadable dispoArtifact = findDispoArtifact(branch, itemId, DispoConstants.DispoItem);
      if (dispoArtifact != null) {
         toReturn = new DispoItemArtifact(dispoArtifact);
      }
      return toReturn;
   }

   @Override
   public Identifiable<String> createDispoSet(ArtifactReadable author, DispoProgram program, DispoSet descriptor) {
      IOseeBranch branch = getProgramBranch(program);
      TransactionBuilder tx = getTxFactory().createTransaction(branch, author, "Create Dispo Set");
      ArtifactId creatdArtId = tx.createArtifact(DispoConstants.DispoSet, descriptor.getName());
      tx.setSoleAttributeFromString(creatdArtId, DispoConstants.ImportPath, descriptor.getImportPath());
      tx.setSoleAttributeFromString(creatdArtId, DispoConstants.ImportState, descriptor.getImportState());
      tx.setSoleAttributeFromString(creatdArtId, DispoConstants.StatusCount, descriptor.getStatusCount());
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
      IOseeBranch branch = getProgramBranch(program);
      ArtifactReadable dispoArtifact = findDispoArtifact(branch, entityId, type);
      if (dispoArtifact != null) {
         TransactionBuilder tx = getTxFactory().createTransaction(branch, author, "Delete Dispo Artifact");
         tx.deleteArtifact(dispoArtifact);
         tx.commit();
         toReturn = true;
      }

      return toReturn;
   }

   @Override
   public void updateDispoSet(ArtifactReadable author, DispoProgram program, String setId, DispoSet newData) {
      IOseeBranch branch = getProgramBranch(program);
      ArtifactReadable dispoSet = findDispoArtifact(branch, setId, DispoConstants.DispoSet);

      String name = newData.getName();
      String importPath = newData.getImportPath();
      String importState = newData.getImportState();
      //      DispoOperationsEnum operationRequest = dispositionSet.getOperation(); //  Operation classes still not created
      JSONArray notesList = newData.getNotesList();

      TransactionBuilder tx = getTxFactory().createTransaction(branch, author, "Delete Dispo Set");
      if (name != null) {
         tx.setName(dispoSet, name);
      }
      if (importPath != null) {
         tx.setSoleAttributeFromString(dispoSet, DispoConstants.ImportPath, importPath);
      }
      if (importState != null) {
         tx.setSoleAttributeFromString(dispoSet, DispoConstants.ImportState, importState);
      }
      if (notesList != null) {
         tx.setSoleAttributeFromString(dispoSet, DispoConstants.DispoNotesJson, notesList.toString());
      }
      tx.commit();
   }

   @Override
   public Identifiable<String> createDispoItem(ArtifactReadable author, DispoProgram program, DispoSet parentSet, DispoItem data, ArtifactReadable assignee) {
      IOseeBranch branch = getProgramBranch(program);
      ArtifactReadable parentSetArt = findDispoArtifact(branch, parentSet.getGuid(), DispoConstants.DispoSet);
      TransactionBuilder tx = getTxFactory().createTransaction(branch, author, "Create Dispoable Item");
      ArtifactId createdItem = tx.createArtifact(DispoConstants.DispoItem, data.getName());

      tx.setSoleAttributeValue(createdItem, DispoConstants.DispoDateCreated, data.getCreationDate());
      tx.setSoleAttributeValue(createdItem, DispoConstants.DispoLastUpdated, data.getLastUpdate());

      tx.setSoleAttributeValue(createdItem, DispoConstants.DispoItemStatus, data.getStatus());
      tx.setSoleAttributeFromString(createdItem, DispoConstants.DispoDiscrepanciesJson,
         data.getDiscrepanciesList().toString());
      tx.setSoleAttributeFromString(createdItem, DispoConstants.DispoAnnotationsJson,
         data.getAnnotationsList().toString());

      tx.relate(parentSetArt, CoreRelationTypes.Default_Hierarchical__Child, createdItem);
      //      tx.relate(createdItem, DispoConstants.DispoAssigned_Assignee, assignee); // UNCOMMENT ONCE on production DB
      tx.commit();
      return createdItem;
   }

   @Override
   public void createAnnotation(ArtifactReadable author, DispoProgram program, ArtifactId disposition, String annotationsJson) {
      IOseeBranch branch = getProgramBranch(program);
      TransactionBuilder tx = getTxFactory().createTransaction(branch, author, "Create Dispo Annotation");

      tx.setSoleAttributeFromString(disposition, DispoConstants.DispoAnnotationsJson, annotationsJson);
      tx.commit();
   }

   @Override
   public void updateDispoItem(ArtifactReadable author, DispoProgram program, String dispoItemId, DispoItem data) {
      boolean wasEdited = false;
      IOseeBranch branch = getProgramBranch(program);
      ArtifactId dispoItemArt = findDispoArtifact(branch, dispoItemId, DispoConstants.DispoItem);
      String assigneeId = data.getAssignee();

      TransactionBuilder tx = getTxFactory().createTransaction(branch, author, "Edit Dispoable Item");
      String name = data.getName();
      JSONArray discrepanciesList = data.getDiscrepanciesList();
      JSONObject annotationsList = data.getAnnotationsList();
      String status = data.getStatus();
      if (name != null) {
         tx.setName(dispoItemArt, name);
         wasEdited = true;
      }
      if (discrepanciesList != null) {
         tx.setSoleAttributeFromString(dispoItemArt, DispoConstants.DispoDiscrepanciesJson,
            discrepanciesList.toString());
         wasEdited = true;
      }
      if (annotationsList != null) {
         tx.setSoleAttributeFromString(dispoItemArt, DispoConstants.DispoAnnotationsJson, annotationsList.toString());
         wasEdited = true;
      }
      if (assigneeId != null) {
         ArtifactReadable userAsArt = findUser(assigneeId);
         tx.relate(dispoItemArt, DispoConstants.DispoAssigned_Assignee, userAsArt);
         wasEdited = true;
      }
      if (status != null) {
         tx.setSoleAttributeFromString(dispoItemArt, DispoConstants.DispoItemStatus, status);
         wasEdited = true;
      }
      if (wasEdited) {
         tx.setSoleAttributeValue(dispoItemArt, DispoConstants.DispoLastUpdated, new Date());
      }
      tx.commit();
   }

   @Override
   public IOseeBranch findProgramId(DispoProgram program) {
      IOseeBranch toReturn = null;
      BranchReadable branchRead = getQuery().branchQuery().andIds(CoreBranches.COMMON).getResults().getExactlyOne();

      ArtifactReadable configArt =
         getQuery().fromBranch(branchRead).andNameEquals(Dispo_Config_Art).getResults().getExactlyOne();

      String configContents = configArt.getSoleAttributeAsString(CoreAttributeTypes.GeneralStringData);

      Pattern regex = Pattern.compile(program.getUuid() + "\\s*:\\s*.*");
      Matcher matcher = regex.matcher(configContents);
      String guid = null;
      if (matcher.find()) {
         String match = matcher.group();
         String[] split = match.split(":");
         guid = split[1];
      }

      regex = Pattern.compile(program.getUuid() + "\\s*:\\s*.*");
      matcher = regex.matcher(configContents);
      Long uuid = null;
      if (matcher.find()) {
         String match = matcher.group();
         String[] split = match.split(":");
         uuid = Long.valueOf(split[1]);
      }
      toReturn = TokenFactory.createBranch(uuid, "");
      return toReturn;
   }

   @Override
   public ResultSet<? extends IOseeBranch> findBaselineBranches() {
      return getQuery().branchQuery().andIsOfType(BranchType.BASELINE).getResults();
   }
}
