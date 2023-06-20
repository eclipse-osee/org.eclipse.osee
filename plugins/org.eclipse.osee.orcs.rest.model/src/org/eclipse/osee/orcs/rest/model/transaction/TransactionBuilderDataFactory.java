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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeType;
import org.eclipse.osee.framework.core.model.change.ChangeVersion;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;

public class TransactionBuilderDataFactory {
   private final OrcsApi orcsApi;
   private BranchId currentBranch;
   private TransactionId currentTransaction;
   private final HashMap<ArtifactId, ArtifactSortContainer> workingArtsById = new HashMap<>();
   private final ArrayList<ChangeItem> attributeChanges = new ArrayList<>();
   private final ArrayList<ChangeItem> relationChanges = new ArrayList<>();
   private final ArrayList<ChangeItem> tupleChanges = new ArrayList<>();
   private final XResultData results = new XResultData();

   public TransactionBuilderDataFactory(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public TransactionBuilderData loadFromChanges(TransactionId txId1, TransactionId txId2) {

      Objects.requireNonNull(txId1, "The given start transaction cannot be null");
      Objects.requireNonNull(txId2, "The given end transaction cannot be null");

      List<ChangeItem> changes = orcsApi.getTransactionFactory().compareTxs(txId1, txId2);
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
      tbd.setTxComment(String.format("Set from JSON data that exports a change report from txId %s to txId %s",
         txId1.getIdString(), txId2.getIdString()));
      return tbd;
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
            results.errorf("branch switch during creation: was %s is %s", this.currentBranch, changeBranch);
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
               workingArtsById.put(art, artSort);
            } else {
               ArtifactSortContainerCreate artSortCreate = new ArtifactSortContainerCreate(art);
               CreateArtifact createdArt = new CreateArtifact();
               createdArt.setId(art.getIdString());
               createdArt.setApplicabilityId(change.getCurrentVersion().getApplicabilityToken().getIdString());
               createdArt.setName(art.getName());
               createdArt.setTypeId(change.getItemTypeId().getIdString());
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
         tbd.getCreateArtifacts().add(((ArtifactSortContainerCreate) sortArt).getCreateArt());
      } else if (sortArt instanceof ArtifactSortContainerModify) {
         tbd.getModifyArtifacts().add(((ArtifactSortContainerModify) sortArt).getModifyArt());
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

      if (attrType.isDate()) {
         List<Date> dates = art.getAttributeValues(attrType);
         item.setValue(Arrays.asList(Long.valueOf(dates.get(0).getTime()).toString()));
      } else if (attrType.isInputStream()) {
         Encoder encoder = Base64.getEncoder();
         List<String> list = new ArrayList<>();
         List<InputStream> isList = art.getAttributeValues(attrType);
         for (InputStream is : isList) {
            try {
               list.add(encoder.encodeToString(Lib.inputStreamToBytes(is)));
            } catch (IOException ex) {
               throw new OseeCoreException("Could not encode binary attribute");
            }
         }

         item.setValue(list);
      } else {
         item.setValue(art.fetchAttributesAsStringList(attrType));
      }
      return item;
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
         if (ModificationType.NEW.equals(mt)) {
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
      for (ChangeItem change : tupleChanges) {
         results.errorf("tuple changes not handled for change: %s", change);
      }
      return tbd;
   }
}
