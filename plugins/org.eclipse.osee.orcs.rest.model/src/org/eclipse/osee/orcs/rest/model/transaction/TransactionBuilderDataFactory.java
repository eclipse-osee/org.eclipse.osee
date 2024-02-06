/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.orcs.rest.model.transaction;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeType;
import org.eclipse.osee.framework.core.model.change.ChangeVersion;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.Zip;
import org.eclipse.osee.framework.resource.management.DataResource;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

public class TransactionBuilderDataFactory {
   private final OrcsApi orcsApi;
   private final OrcsTokenService tokenService;
   private final IResourceManager resourceManager;
   private BranchId currentBranch;
   private TransactionId currentTransaction;
   private final HashMap<ArtifactId, ArtifactSortContainer> workingArtsById = new HashMap<>();
   private final ArrayList<ChangeItem> attributeChanges = new ArrayList<>();
   private final ArrayList<ChangeItem> relationChanges = new ArrayList<>();
   private final ArrayList<ChangeItem> tupleChanges = new ArrayList<>();
   private final XResultData results = new XResultData();

   public TransactionBuilderDataFactory(OrcsApi orcsApi, IResourceManager resourceManager) {
      this.orcsApi = orcsApi;
      this.tokenService = orcsApi.tokenService();
      this.resourceManager = resourceManager;
   }

   public TransactionBuilderDataFactory(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      this.tokenService = orcsApi.tokenService();
      this.resourceManager = null;
   }

   public TransactionBuilderData loadFromChanges(TransactionId txId1, TransactionId txId2) {

      Objects.requireNonNull(txId1, "The given start transaction cannot be null");
      Objects.requireNonNull(txId2, "The given end transaction cannot be null");

      List<ChangeItem> changes = orcsApi.getTransactionFactory().compareTxs(txId1, txId2);
      if (changes.isEmpty()) {
         throw new OseeCoreException("Change report is empty");
      }
      TransactionBuilderData tbd = new TransactionBuilderData();
      for (ChangeItem change : changes) {
         if (isGoodChange(change)) {
            ChangeType ct = change.getChangeType();

            if (ct.isArtifactChange()) {
               ChangeVersion net = change.getNetChange();
               ModificationType mt = net.getModType();
               if (ModificationType.NEW.equals(mt)) {
                  tbd = newArtifact(change, tbd);
               } else if (ModificationType.MODIFIED.equals(mt)) {
                  tbd = modifyArtifact(change, tbd);
               } else if (ModificationType.DELETED.equals(mt)) {
                  tbd = deleteArtifact(change, tbd);
               }
            } else if (ct.isAttributeChange()) {
               attributeChanges.add(change);
            } else if (ct.isRelationChange()) {
               relationChanges.add(change);
            } else if (ct.isTupleChange()) {
               tupleChanges.add(change);
            } else if (ct.equals(ChangeType.Unknown)) {
               results.errorf("unknown change not handled: %s", change.toString());
            }
         }
      }
      tbd = handleAttributeChanges(tbd);
      tbd = handleRelationChanges(tbd);
      tbd = handleTupleChanges(tbd);
      tbd.setBranch(currentBranch.getIdString());
      orcsApi.getTransactionFactory().getTx(txId2);
      TransactionReadable txReadable = orcsApi.getTransactionFactory().getTx(txId2);
      if (txReadable.isValid()) {
         tbd.setTxComment(txReadable.getComment());
      } else {
         tbd.setTxComment(String.format("Set from JSON data that exports a change report from txId %s to txId %s",
            txId1.getIdString(), txId2.getIdString()));
      }
      ArtifactId commitArt = txReadable.getCommitArt();
      if (commitArt.isValid()) {
         tbd.setTxCommitArtId(commitArt.getId());
      } else {
         tbd.setTxCommitArtId(ArtifactId.SENTINEL.getId());
      }

      if (results.isFailed()) {
         tbd.setResults("Failed");
      }
      return tbd;
   }

   public XResultData getResults() {
      return results;
   }

   public TransactionBuilder loadFromJson(String json) {
      return this.loadFromJson(json, null);
   }

   public TransactionBuilder loadFromJson(String json, TransactionBuilder tx) {
      if (Objects.isNull(json)) {
         throw new OseeArgumentException("Invalid String in loadFromJson");
      }
      JsonNode readTree = orcsApi.jaxRsApi().readTree(json);
      BranchId branch = BranchId.valueOf(readTree.get("branch").asLong());
      Map<String, ArtifactToken> artifactsByName = new HashMap<>();
      Map<String, ArtifactToken> artifactsByKeys = new HashMap<>();

      if (tx == null) {
         String txComment = readTree.get("txComment").asText();
         if (Strings.isInValid(txComment)) {
            txComment = "create transaction REST call";
         }
         tx = orcsApi.getTransactionFactory().createTransaction(branch, txComment);
      }

      createArtifacts(readTree, artifactsByName, artifactsByKeys, tx);
      modifyArtifacts(readTree, artifactsByName, tx);
      deleteArtifacts(readTree, tx);
      deleteRelations(readTree, tx);
      addRelations(readTree, artifactsByKeys, tx);
      JsonNode commitArtNode = readTree.get("txCommitArtId");
      if (commitArtNode != null && (commitArtNode.isLong() || commitArtNode.isInt())) {
         Long commitArtId = commitArtNode.asLong();
         ArtifactId commitArt = ArtifactId.valueOf(commitArtId);
         if (commitArt.isValid()) {
            tx.setCommitArtId(commitArt);
         }
      }
      return tx;
   }

   private boolean isGoodChange(ChangeItem change) {
      Boolean good =
         change.getIgnoreType().isNone() || change.getIgnoreType().isResurrected() || change.getIgnoreType().isAlreadyOnDestination();
      if (this.currentBranch == null) {
         this.currentBranch = change.getCurrentVersion().getTransactionToken().getBranch();
      } else {
         TransactionToken currentTransaction = change.getCurrentVersion().getTransactionToken();
         BranchId changeBranch = currentTransaction.getBranch();
         if (this.currentBranch != changeBranch) {
            // synthetic artifact changes have no branch set in the change, just log it
            results.logf("branch switch during creation: was %s is %s", this.currentBranch, changeBranch);
         }
      }
      return good;
   }

   private TransactionBuilderData deleteArtifact(ChangeItem change, TransactionBuilderData tbd) {
      List<Long> artifacts = tbd.getDeleteArtifacts();
      if (artifacts == null) {
         artifacts = new ArrayList<>();
         tbd.setDeleteArtifacts(artifacts);
      }
      Long id = change.getArtId().getId();
      if (!artifacts.contains(id)) {
         artifacts.add(id);
      }
      return tbd;
   }

   private TransactionBuilderData modifyArtifact(ChangeItem change, TransactionBuilderData tbd) {
      ArtifactSortContainer artSort = addArtFromChange(change);
      List<ModifyArtifact> artifacts = tbd.getModifyArtifacts();
      if (artifacts == null) {
         artifacts = new ArrayList<>();
         tbd.setModifyArtifacts(artifacts);
      }
      ModifyArtifact modifiedArt = new ModifyArtifact();
      modifiedArt.setApplicabilityId(change.getCurrentVersion().getApplicabilityToken().getIdString());
      modifiedArt.setId(change.getArtId().getIdString());
      if (artSort instanceof ArtifactSortContainerModify) {
         ((ArtifactSortContainerModify) artSort).setModifyArt(modifiedArt);
      }
      return tbd;
   }

   private TransactionBuilderData newArtifact(ChangeItem change, TransactionBuilderData tbd) {
      addArtFromChange(change);
      List<CreateArtifact> artifacts = tbd.getCreateArtifacts();
      if (artifacts == null) {
         artifacts = new ArrayList<>();
         tbd.setCreateArtifacts(artifacts);
      }
      return tbd;
   }

   private ArtifactSortContainer addArtFromChange(ChangeItem change) {
      ArtifactSortContainer artSort;
      ArtifactReadable art;
      ArtifactId artId = change.getArtId();
      ModificationType modType = change.getNetChange().getModType();
      if (workingArtsById.containsKey(artId)) {
         artSort = workingArtsById.get(artId);
      } else {
         if (currentBranch.isInvalid()) {
            results.error("current branch is invalid");
            throw new OseeCoreException("Branch in TransactionBuilderDataFactory invalid");
         }
         art = orcsApi.getQueryFactory().fromBranch(currentBranch).fromTransaction(currentTransaction).andId(
            artId).asArtifact();
         if (!art.isValid()) {
            results.errorf("Can't find artifact for art id: ", artId.toString());
            throw new OseeCoreException("Artifact for art id %s failed in query", artId.toString());
         } else if (modType.equals(ModificationType.NEW)) {
            // special case if ignoreType is already on destination - then it will be the modify case not the new
            if (change.getIgnoreType().isAlreadyOnDestination()) {
               artSort = new ArtifactSortContainerModify(art);
               ModifyArtifact modifiedArt = new ModifyArtifact();
               modifiedArt.setId(art.getIdString());
               ((ArtifactSortContainerModify) artSort).setModifyArt(modifiedArt);
               workingArtsById.put(art, artSort);
            } else {
               ArtifactSortContainerCreate artSortCreate = new ArtifactSortContainerCreate(art);
               CreateArtifact createdArt = new CreateArtifact();
               createdArt.setId(art.getIdString());
               createdArt.setApplicabilityId(change.getCurrentVersion().getApplicabilityToken().getIdString());
               createdArt.setName(art.getName());
               createdArt.setTypeId(change.getItemTypeId().getIdString());
               createdArt.setkey(art.getIdString());
               artSortCreate.setCreateArtifact(createdArt);
               workingArtsById.put(artId, artSortCreate);
               return artSortCreate;
            }
         } else if (modType.equals(ModificationType.MODIFIED)) {
            artSort = new ArtifactSortContainerModify(art);
            ModifyArtifact modifiedArt = new ModifyArtifact();
            modifiedArt.setId(art.getIdString());
            modifiedArt.setApplicabilityId(change.getCurrentVersion().getApplicabilityToken().getIdString());
            workingArtsById.put(art, artSort);
         } else {
            artSort = new ArtifactSortContainer(art);
            workingArtsById.put(artId, artSort);
         }
      }
      return artSort;
   }

   private TransactionBuilderData handleAttributeChanges(TransactionBuilderData tbd) {
      for (ChangeItem change : attributeChanges) {
         ChangeVersion net = change.getNetChange();
         ModificationType mt = net.getModType();
         if (ModificationType.NEW.equals(mt)) {
            newAttribute(change, tbd);
         } else if (ModificationType.MODIFIED.equals(mt)) {
            modifyAttribute(change, tbd);
         } else if (ModificationType.DELETED.equals(mt)) {
            deleteAttribute(change, tbd);
         }
      }
      return fillData(tbd);
   }

   private TransactionBuilderData fillData(TransactionBuilderData tbd) {
      workingArtsById.forEach((artId, sortArt) -> addSortContent(tbd, sortArt));
      return tbd;
   }

   private void addSortContent(TransactionBuilderData tbd, ArtifactSortContainer sortArt) {
      if (sortArt instanceof ArtifactSortContainerCreate) {
         List<CreateArtifact> createArts = tbd.getCreateArtifacts();
         if (createArts != null) {
            createArts.add(((ArtifactSortContainerCreate) sortArt).getCreateArt());
         }
      } else if (sortArt instanceof ArtifactSortContainerModify) {
         List<ModifyArtifact> modArts = tbd.getModifyArtifacts();
         if (modArts != null) {
            modArts.add(((ArtifactSortContainerModify) sortArt).getModifyArt());
         }
      } else {
         results.errorf("default sort container not handled %s", sortArt.getArtifact().getIdString());
      }
   }

   private TransactionBuilderData deleteAttribute(ChangeItem change, TransactionBuilderData tbd) {
      ArtifactSortContainer sortArt = workingArtsById.get(change.getArtId());
      if (sortArt == null) {
         results.errorf("artifact not in change report: %s", change.getArtId());
      } else {
         if (sortArt instanceof ArtifactSortContainerCreate) {
            results.errorf("must not delete attribute in creation container: %s", change);
         } else if (sortArt instanceof ArtifactSortContainerModify) {
            ModifyArtifact art = ((ArtifactSortContainerModify) sortArt).getModifyArt();
            List<DeleteAttribute> attrs = art.getDeleteAttributes();
            if (attrs == null) {
               attrs = new ArrayList<>();
               art.setDeleteAttributes(attrs);
            }
            DeleteAttribute attr = new DeleteAttribute();
            attr.setTypeId(change.getItemTypeId().getIdString());
            attrs.add(attr);
         } else {
            results.errorf("incorrect default sort container for deleted attribute %s", change.getItemId().toString());
         }
      }
      return tbd;
   }

   private TransactionBuilderData modifyAttribute(ChangeItem change, TransactionBuilderData tbd) {
      ArtifactSortContainer sortArt = workingArtsById.get(change.getArtId());
      if (sortArt == null) {
         results.errorf("artifact not in change report: %s", change.getArtId());
      } else {
         if (sortArt instanceof ArtifactSortContainerCreate) {
            results.warningf("modify attribute in create artifact, change: %s", change);
            CreateArtifact art = ((ArtifactSortContainerCreate) sortArt).getCreateArt();
            List<Attribute> attrs = art.getAttributes();
            if (attrs == null) {
               attrs = new ArrayList<>();
               art.setAttributes(attrs);
            }

            // check to see if there is already a SetAttribute for the TypeId
            // if so, the new value should already be in the list
            boolean found = false;
            for (Attribute attr : attrs) {
               if (attr.getTypeId().equals(change.getItemTypeId().getIdString())) {
                  found = true;
               }
            }
            if (!found) {
               attrs.add((Attribute) setupTransferArtifact(change, sortArt.getArtifact(), Attribute::new));
            }

         } else if (sortArt instanceof ArtifactSortContainerModify) {
            ModifyArtifact art = ((ArtifactSortContainerModify) sortArt).getModifyArt();
            List<SetAttribute> attrs = art.getSetAttributes();
            if (attrs == null) {
               attrs = new ArrayList<>();
               art.setSetAttributes(attrs);
            }
            // check to see if there is already a SetAttribute for the TypeId
            // if so, the new value should already be in the list
            boolean found = false;
            for (SetAttribute attr : attrs) {
               if (attr.getTypeId().equals(change.getItemTypeId().getIdString())) {
                  found = true;
               }
            }
            if (!found) {
               attrs.add((SetAttribute) setupTransferArtifact(change, sortArt.getArtifact(), SetAttribute::new));
            }
         } else {
            results.errorf("incorrect default sort container for modified attribute %s", change.getItemId().toString());
         }
      }
      return tbd;
   }

   private AttributeTransfer setupTransferArtifact(ChangeItem change, ArtifactReadable art,
      Function<String, AttributeTransfer> attrTrans) {
      AttributeTypeGeneric<?> attrType = orcsApi.tokenService().getAttributeType(change.getItemTypeId().getId());
      AttributeTransfer item = attrTrans.apply(attrType.getIdString());
      ChangeVersion cv = change.getNetChange();

      if (cv != null && cv.isValid()) {
         String value = cv.getValue();
         String uri = cv.getUri();
         boolean validValue = Strings.isValidAndNonBlank(value);
         boolean validURI = Strings.isValidAndNonBlank(uri);
         //both blank
         if (!validValue && !validURI) {
            // check the current change
            cv = change.getCurrentVersion();
            value = cv.getValue();
            uri = cv.getUri();
            validValue = Strings.isValidAndNonBlank(value);
            validURI = Strings.isValidAndNonBlank(uri);
         }
         if (validValue) {
            item.setValue(Arrays.asList(value));
         } else if (validURI) {
            if (attrType.isInputStream()) {
               // get the data from the change if possible
               List<String> list = new ArrayList<>();
               Encoder encoder = Base64.getEncoder();
               byte[] data = resourceManager.acquire(new DataResource("application/zip", "UTF-8", ".zip", uri));
               list.add(encoder.encodeToString(data));
               item.setValue(list);
            } else {
               try {
                  List<String> list = new ArrayList<>();
                  byte[] data = resourceManager.acquire(new DataResource("application/zip", "UTF-8", ".zip", uri));
                  if (data == null) {
                     throw new OseeCoreException("invalid data for zip: %s", uri.toString());
                  }
                  ByteBuffer decompressed = ByteBuffer.wrap(Zip.decompressBytes(new ByteArrayInputStream(data)));
                  list.add(new String(decompressed.array(), "UTF-8"));
                  item.setValue(list);
               } catch (IOException ex) {
                  OseeCoreException.wrapAndThrow(ex);
               }
            }
         } else {
            results.errorf("change %s has invalid net change", change.toString());
         }
      } else {
         results.errorf("unhandled data for art %s setting type %s", art, attrType);
      }
      return item;
   }

   private void addToTransferArtifact(ChangeItem change, ArtifactReadable art, Attribute item) {
      ArtifactTypeToken token = art.getArtifactType();
      AttributeTypeGeneric<?> attrType = orcsApi.tokenService().getAttributeType(change.getItemTypeId().getId());
      if (token.getMax(attrType) > 1) {
         ChangeVersion cv = change.getNetChange();
         List<String> list = new ArrayList<>(item.getValue());
         if (cv != null && cv.isValid()) {
            String value = cv.getValue();
            String uri = cv.getUri();
            if (Strings.isValidAndNonBlank(value)) {
               list.add(value);
            } else if (Strings.isValidAndNonBlank(uri)) {
               if (attrType.isInputStream()) {
                  Encoder encoder = Base64.getEncoder();
                  byte[] data = resourceManager.acquire(new DataResource("application/zip", "UTF-8", ".zip", uri));
                  list.add(encoder.encodeToString(data));
               } else {
                  results.errorf("uri %s is not attr type input stream", uri);
               }
            } else {
               results.errorf("change %s has invalid net change", change.toString());
            }
            item.setValue(list);
         } else {
            results.errorf("unhandled data for art %s setting type %s", art, attrType);
         }
      } else {
         results.errorf("Attempting to add multiple values to single valued item %s", change.toString());
      }
   }

   private TransactionBuilderData newAttribute(ChangeItem change, TransactionBuilderData tbd) {
      ArtifactSortContainer sortArt = workingArtsById.get(change.getArtId());
      if (sortArt == null) {
         results.errorf("artifact not in change report: %s", change.getArtId());
      } else {
         if (sortArt instanceof ArtifactSortContainerCreate) {
            CreateArtifact art = ((ArtifactSortContainerCreate) sortArt).getCreateArt();
            List<Attribute> attrs = art.getAttributes();
            if (attrs == null) {
               attrs = new ArrayList<>();
               art.setAttributes(attrs);
            }
            // check to see if there is already a SetAttribute for the TypeId
            // if so, the new value should already be in the list
            boolean found = false;
            for (Attribute attr : attrs) {
               if (attr.getTypeId().equals(change.getItemTypeId().getIdString())) {
                  found = true;
                  addToTransferArtifact(change, sortArt.getArtifact(), attr);
               }
            }
            if (!found) {
               attrs.add((Attribute) setupTransferArtifact(change, sortArt.getArtifact(), Attribute::new));
            }
         } else if (sortArt instanceof ArtifactSortContainerModify) {
            ModifyArtifact art = ((ArtifactSortContainerModify) sortArt).getModifyArt();
            List<AddAttribute> attrs = art.getAddAttributes();
            if (attrs == null) {
               attrs = new ArrayList<>();
               art.setAddAttributes(attrs);
            }
            boolean found = false;
            for (AddAttribute attr : attrs) {
               if (attr.getTypeId().equals(change.getItemTypeId().getIdString())) {
                  found = true;
                  // TODO add multivalue modify
               }
            }
            if (!found) {
               attrs.add((AddAttribute) setupTransferArtifact(change, sortArt.getArtifact(), AddAttribute::new));
            }
         } else {
            results.errorf("incorrect default sort container for modified attribute %s", change.getItemId().toString());
         }
      }
      return tbd;
   }

   private TransactionBuilderData handleRelationChanges(TransactionBuilderData tbd) {
      for (ChangeItem change : relationChanges) {
         ChangeVersion current = change.getCurrentVersion();
         ModificationType mt = current.getModType();
         if (ModificationType.NEW.equals(mt) || ModificationType.MODIFIED.equals(mt)) {
            tbd = newRelation(change, tbd);
         } else if (ModificationType.DELETED.equals(mt)) {
            tbd = deleteRelation(change, tbd);
         }
      }
      return tbd;
   }

   private TransactionBuilderData deleteRelation(ChangeItem change, TransactionBuilderData tbd) {
      List<DeleteRelation> rels = tbd.getDeleteRelations();
      if (rels == null) {
         rels = new ArrayList<>();
         tbd.setDeleteRelations(rels);
      }
      DeleteRelation dr = new DeleteRelation();
      dr.setaArtId(change.getArtId().getIdString());
      dr.setbArtId(change.getArtIdB().toString());
      dr.setTypeId(change.getItemTypeId().getIdString());
      rels.add(dr);
      return tbd;
   }

   private TransactionBuilderData newRelation(ChangeItem change, TransactionBuilderData tbd) {
      List<AddRelation> rels = tbd.getAddRelations();
      if (rels == null) {
         rels = new ArrayList<>();
         tbd.setAddRelations(rels);
      }
      AddRelation dr = new AddRelation();
      dr.setaArtId(change.getArtId().getIdString());
      dr.setbArtId(change.getArtIdB().toString());
      dr.setTypeId(change.getItemTypeId().getIdString());
      rels.add(dr);
      return tbd;
   }

   private TransactionBuilderData handleTupleChanges(TransactionBuilderData tbd) {
      //      for (ChangeItem change : tupleChanges) {
      //         results.errorf("tuple changes not handled for change: %s", change);
      //      }
      return tbd;
   }

   // transferred from TransactionBuilderMessageReader to here to simplify api calls
   private void createArtifacts(JsonNode root, Map<String, ArtifactToken> artifactsByName,
      Map<String, ArtifactToken> artifactsByKeys, TransactionBuilder tx) {
      if (root.has("createArtifacts")) {
         for (JsonNode artifactJson : root.get("createArtifacts")) {
            ApplicabilityId appId;
            ArtifactId artId;
            ArtifactToken artifact;
            if (artifactJson.has("applicabilityId")) {
               appId = ApplicabilityId.valueOf(artifactJson.get("applicabilityId").asLong());
            } else {
               appId = ApplicabilityId.BASE;
            }
            ArtifactTypeToken artifactType = getArtifactType(artifactJson);
            if (artifactJson.has("id")) {
               artId = ArtifactId.valueOf(artifactJson.get("id").asLong());
               artifact = tx.createArtifact(artifactType, artifactJson.get("name").asText(), artId, appId);
            } else {
               artifact = tx.createArtifact(artifactType, artifactJson.get("name").asText(), appId);
            }
            artifactsByName.put(artifact.getName(), artifact);

            if (artifactJson.has("key")) {
               artifactsByKeys.put(artifactJson.get("key").asText(), artifact);
            }

            readAttributes(tx, artifactJson, artifact, "attributes");
            readrelations(tx, artifactsByName, artifactsByKeys, artifactJson, artifact);
         }
      }
   }

   private void modifyArtifacts(JsonNode root, Map<String, ArtifactToken> artifactsByName, TransactionBuilder tx) {
      if (root.has("modifyArtifacts")) {
         for (JsonNode artifactJson : root.get("modifyArtifacts")) {
            ArtifactToken artifact = orcsApi.getQueryFactory().fromBranch(tx.getBranch()).andUuid(
               artifactJson.get("id").asLong()).asArtifactToken();
            artifactsByName.put(artifact.getName(), artifact);

            if (artifactJson.has("applicabilityId")) {
               tx.setApplicability(artifact, ApplicabilityId.valueOf(artifactJson.get("applicabilityId").asLong()));
            }

            readAttributes(tx, artifactJson, artifact, "setAttributes");
            addAttributes(tx, artifactJson, artifact, "addAttributes");
            deleteAttributes(tx, artifactJson, artifact, "deleteAttributes");
         }
      }
   }

   private void deleteArtifacts(JsonNode root, TransactionBuilder tx) {
      if (root.has("deleteArtifacts")) {
         for (JsonNode artifactId : root.get("deleteArtifacts")) {
            tx.deleteArtifact(ArtifactId.valueOf(artifactId.asLong()));
         }
      }
   }

   private void deleteRelations(JsonNode root, TransactionBuilder tx) {
      if (root.has("deleteRelations")) {
         for (JsonNode relation : root.get("deleteRelations")) {
            RelationTypeToken relationType = getRelationType(relation);
            ArtifactId artA = ArtifactId.valueOf(relation.get("aArtId").asLong());
            ArtifactId artB = ArtifactId.valueOf(relation.get("bArtId").asLong());
            if (artA.isValid() && artB.isValid()) {
               if (relation.has("gamma")) {
                  tx.unrelate(artA, relationType, artB, GammaId.valueOf(relation.get("gamma").asText()));
               } else {
                  tx.unrelate(artA, relationType, artB);
               }
            }
         }
      }
      if (root.has("deleteInvalidRelations")) {
         for (JsonNode relation : root.get("deleteInvalidRelations")) {
            ArtifactId artA = ArtifactId.valueOf(relation.get("validArt").asLong());
            ArtifactId artB = ArtifactId.valueOf(relation.get("invalidArt").asLong());
            if (artA.isValid() && artB.isValid()) {
               tx.unrelateFromInvalidArtifact(artA, artB);
            }
         }
      }
   }

   private void addRelations(JsonNode root, Map<String, ArtifactToken> artifactsByKeys, TransactionBuilder tx) {
      if (root.has("addRelations")) {
         for (JsonNode relation : root.get("addRelations")) {
            RelationTypeToken relationType = getRelationType(relation);

            String rationale = relation.has("rationale") ? relation.get("rationale").asText() : "";
            ArtifactId relatedArtifact = relation.has("relatedArtifact") ? ArtifactId.valueOf(
               relation.get("relatedArtifact").asLong()) : ArtifactId.SENTINEL;
            String afterArtifact = relation.has("afterArtifact") ? relation.get("afterArtifact").asText() : "end";

            ArtifactId artA;
            ArtifactId artB;
            if (artifactsByKeys.containsKey(relation.get("aArtId").asText())) {
               artA = ArtifactId.valueOf(artifactsByKeys.get(relation.get("aArtId").asText()).getId());
            } else {
               artA = ArtifactId.valueOf(relation.get("aArtId").asLong());
            }
            if (artifactsByKeys.containsKey(relation.get("bArtId").asText())) {
               artB = ArtifactId.valueOf(artifactsByKeys.get(relation.get("bArtId").asText()).getId());
            } else {
               artB = ArtifactId.valueOf(relation.get("bArtId").asLong());
            }

            boolean hasGamma = relation.has("gamma");
            if (relationType.isNewRelationTable() && !hasGamma) {
               tx.relate(artA, relationType, artB, relatedArtifact, afterArtifact);
            } else if (!hasGamma) {
               tx.relate(artA, relationType, artB, rationale);
            } else if (relationType.isNewRelationTable()) {
               tx.relate(artA, relationType, artB, relatedArtifact, afterArtifact,
                  GammaId.valueOf(relation.get("gamma").asText()));
            } else {
               tx.relate(artA, relationType, artB, rationale, GammaId.valueOf(relation.get("gamma").asText()));
            }
         }
      }

   }

   private <R extends NamedId> R getToken(JsonNode node, Function<Long, R> getById, Function<String, R> getByName) {
      JsonNode id = node.get("typeId");
      if (id == null) {
         JsonNode typeNode = node.get("typeName");
         if (typeNode == null || Strings.isInValid(typeNode.asText())) {
            throw new OseeArgumentException("The type must be specified");
         }
         return getByName.apply(typeNode.asText());
      }
      return getById.apply(id.asLong());
   }

   private void readAttributes(TransactionBuilder tx, JsonNode artifactJson, ArtifactToken artifact,
      String attributesNodeName) {
      if (artifactJson.has(attributesNodeName)) {
         for (JsonNode attribute : artifactJson.get(attributesNodeName)) {
            AttributeTypeGeneric<?> attributeType = getAttributeType(attribute);
            JsonNode value = attribute.get("value");
            boolean hasGamma = attribute.has("gamma");
            if (value.isArray()) {
               ArrayList<String> values = new ArrayList<>();
               ArrayList<InputStream> streams = new ArrayList<>();
               ArrayList<GammaId> gammas = new ArrayList<>();
               for (JsonNode attrValue : value) {
                  if (attributeType.isInputStream()) {
                     Decoder decoder = Base64.getDecoder();
                     ByteArrayInputStream bais = new ByteArrayInputStream(decoder.decode(attrValue.asText()));
                     streams.add(bais);
                  } else {
                     values.add(attrValue.asText());
                  }
               }
               if (hasGamma) {
                  JsonNode gamma = attribute.get("gamma");
                  if (gamma.isArray()) {
                     //if it isn't an array we probably don't want to try to process the gammas
                     for (JsonNode gammaValue : gamma) {
                        gammas.add(GammaId.valueOf(gammaValue.asText()));
                     }
                  }

               }
               if (!values.isEmpty() && !hasGamma) {
                  tx.setAttributesFromStrings(artifact, attributeType, values);
               }
               if (!streams.isEmpty() && !hasGamma) {
                  tx.setAttributesFromValues(artifact, attributeType, streams);
               }
               if (!values.isEmpty() && hasGamma && !gammas.isEmpty()) {
                  tx.setAttributesFromStrings(artifact, attributeType, values, gammas);
               }
               if (!streams.isEmpty() && hasGamma && !gammas.isEmpty()) {
                  tx.setAttributesFromValues(artifact, attributeType, streams, gammas);
               }
            } else if (hasGamma) {
               JsonNode gamma = attribute.get("gamma");
               tx.setSoleAttributeFromString(artifact, attributeType, value.asText(), GammaId.valueOf(gamma.asText()));
            } else {
               tx.setSoleAttributeFromString(artifact, attributeType, value.asText());
            }
         }
      }
   }

   private void addAttributes(TransactionBuilder tx, JsonNode artifactJson, ArtifactToken artifact,
      String attributesNodeName) {
      if (artifactJson.has(attributesNodeName)) {
         for (JsonNode attribute : artifactJson.get(attributesNodeName)) {
            AttributeTypeGeneric<?> attributeType = getAttributeType(attribute);
            JsonNode value = attribute.get("value");
            if (value.isArray()) {
               for (JsonNode attrValue : value) {
                  tx.createAttributeFromString(artifact, attributeType, attrValue.asText());
               }
            } else {
               tx.createAttributeFromString(artifact, attributeType, value.asText());
            }
         }
      }
   }

   private void deleteAttributes(TransactionBuilder tx, JsonNode artifactJson, ArtifactToken artifact,
      String attributesNodeName) {
      if (artifactJson.has(attributesNodeName)) {
         for (JsonNode attribute : artifactJson.get(attributesNodeName)) {
            if (attribute.has("gamma")) {
               tx.deleteAttributes(artifact, getAttributeType(attribute),
                  GammaId.valueOf(attribute.get("gamma").asText()));
            } else {
               tx.deleteAttributes(artifact, getAttributeType(attribute));
            }
         }
      }
   }

   private void readrelations(TransactionBuilder tx, Map<String, ArtifactToken> artifactsByName,
      Map<String, ArtifactToken> artifactsByKey, JsonNode artifactJson, ArtifactToken artifact) {
      if (artifactJson.has("relations")) {
         for (JsonNode relation : artifactJson.get("relations")) {
            RelationTypeToken relationType = getRelationType(relation);
            if (relation.isTextual() || relation.isArray()) {
               relate(tx, artifactsByName, artifactsByKey, relation, relationType, artifact, RelationSide.SIDE_A, "",
                  ArtifactId.SENTINEL, "end");
            } else if (relation.isObject()) {
               String rationale = relation.has("rationale") ? relation.get("rationale").asText() : "";
               ArtifactId relatedArtifact = relation.has("relatedArtifact") ? ArtifactId.valueOf(
                  relation.get("relatedArtifact").asLong()) : ArtifactId.SENTINEL;
               String afterArtifact = relation.has("afterArtifact") ? relation.get("afterArtifact").asText() : "end";

               if (relation.has("sideA")) {
                  relate(tx, artifactsByName, artifactsByKey, relation.get("sideA"), relationType, artifact,
                     RelationSide.SIDE_B, rationale, relatedArtifact, afterArtifact);
               }
               if (relation.has("sideB")) { //both sides are allowed for the same relation entry
                  relate(tx, artifactsByName, artifactsByKey, relation.get("sideB"), relationType, artifact,
                     RelationSide.SIDE_A, rationale, relatedArtifact, afterArtifact);
               }
            } else {
               throw new OseeStateException("Json Node of unexpected type %s", relation.getNodeType());
            }
         }
      }
   }

   private void relate(TransactionBuilder tx, Map<String, ArtifactToken> artifactsByName,
      Map<String, ArtifactToken> artifactsByKey, JsonNode relations, RelationTypeToken relationType,
      ArtifactToken artifact, RelationSide side, String rationale, ArtifactId relatedArtifact, String afterArtifact) {
      if (relations.isTextual()) {
         relateOne(tx, artifactsByName, artifactsByKey, relations, relationType, artifact, side, rationale,
            relatedArtifact, afterArtifact);
      } else if (relations.isArray()) {
         for (JsonNode name : relations) {
            relateOne(tx, artifactsByName, artifactsByKey, name, relationType, artifact, side, rationale,
               relatedArtifact, afterArtifact);
         }
      } else {
         throw new OseeStateException("Json Node of unexpected type %s", relations.getNodeType());
      }
   }

   private void relate(TransactionBuilder tx, RelationTypeToken relationType, ArtifactToken artifact, RelationSide side,
      String rationale, ArtifactId otherArtifact, ArtifactId relatedArtifact, String afterArtifact) {
      if (side.isSideA()) {
         if (relationType.isNewRelationTable()) {
            tx.relate(artifact, relationType, otherArtifact, relatedArtifact, afterArtifact);
         } else {
            tx.relate(artifact, relationType, otherArtifact, rationale);
         }
      } else {
         if (relationType.isNewRelationTable()) {
            tx.relate(otherArtifact, relationType, artifact, relatedArtifact, afterArtifact);
         } else {

            tx.relate(otherArtifact, relationType, artifact, rationale);
         }
      }
   }

   private void relateOne(TransactionBuilder tx, Map<String, ArtifactToken> artifactsByName,
      Map<String, ArtifactToken> artifactsByKey, JsonNode relation, RelationTypeToken relationType,
      ArtifactToken artifact, RelationSide side, String rationale, ArtifactId relatedArtifact, String afterArtifact) {
      ArtifactId otherArtifact;
      if (Strings.isNumeric(relation.asText(""))) {
         otherArtifact = ArtifactId.valueOf(relation.asLong());
      } else {
         otherArtifact = getArtifactByName(tx, artifactsByName, artifactsByKey, relation.asText());
      }
      relate(tx, relationType, artifact, side, rationale, otherArtifact, relatedArtifact, afterArtifact);
   }

   private ArtifactToken getArtifactByName(TransactionBuilder tx, Map<String, ArtifactToken> artifactsByName,
      Map<String, ArtifactToken> artifactsByKey, String name) {
      ArtifactToken artifact = artifactsByName.get(name);
      if (artifact != null) {
         return artifact;
      }
      artifact = artifactsByKey.get(name);
      if (artifact != null) {
         return artifact;
      }
      return orcsApi.getQueryFactory().fromBranch(tx.getBranch()).andNameEquals(name).asArtifactToken();
   }

   private ArtifactTypeToken getArtifactType(JsonNode artifactJson) {
      return getToken(artifactJson, tokenService::getArtifactType, tokenService::getArtifactType);
   }

   private AttributeTypeGeneric<?> getAttributeType(JsonNode attribute) {
      return getToken(attribute, tokenService::getAttributeType, tokenService::getAttributeType);
   }

   private RelationTypeToken getRelationType(JsonNode relation) {
      return getToken(relation, tokenService::getRelationType, tokenService::getRelationType);
   }
}
