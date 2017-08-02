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
package org.eclipse.osee.orcs.core.internal.transaction;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.Tuple2Type;
import org.eclipse.osee.framework.core.data.Tuple3Type;
import org.eclipse.osee.framework.core.data.Tuple4Type;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.KeyValueOps;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.search.QueryModule;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Roberto E. Escobar
 * @author Megumi Telles
 */
public class TransactionBuilderImpl implements TransactionBuilder {

   private final TxCallableFactory txFactory;
   private final TxDataManager txManager;
   private final TxData txData;
   private final QueryModule query;
   private final KeyValueOps keyValueOps;

   public TransactionBuilderImpl(TxCallableFactory txFactory, TxDataManager dataManager, TxData txData, QueryModule query, KeyValueOps keyValueOps) {
      super();
      this.txFactory = txFactory;
      this.txManager = dataManager;
      this.txData = txData;
      this.query = query;
      this.keyValueOps = keyValueOps;
   }

   private Artifact getForWrite(ArtifactId artifactId) throws OseeCoreException {
      return txManager.getForWrite(txData, artifactId);
   }

   @Override
   public BranchId getBranch() {
      return txData.getBranch();
   }

   @Override
   public String getComment() {
      return txData.getComment();
   }

   @Override
   public void setComment(String comment) throws OseeCoreException {
      txManager.setComment(txData, comment);
   }

   public void setAuthor(ArtifactId author) throws OseeCoreException {
      txManager.setAuthor(txData, author);
   }

   @Override
   public ArtifactToken createArtifact(IArtifactType artifactType, String name) throws OseeCoreException {
      return createArtifact(artifactType, name, null);
   }

   @Override
   public ArtifactToken createArtifact(IArtifactType artifactType, String name, String guid) throws OseeCoreException {
      return txManager.createArtifact(txData, artifactType, name, guid);
   }

   @Override
   public ArtifactToken createArtifact(IArtifactType artifactType, String name, String guid, long uuid) throws OseeCoreException {
      Conditions.checkExpressionFailOnTrue(uuid <= 0L, "Invalid Uuid %d. Must be > 0", uuid);
      return txManager.createArtifact(txData, artifactType, name, guid, uuid);
   }

   @Override
   public ArtifactToken createArtifact(ArtifactToken token) throws OseeCoreException {
      Conditions.checkExpressionFailOnTrue(token.isInvalid(), "Invalid Id %d. Must be > 0", token.getId());
      return txManager.createArtifact(txData, token.getArtifactType(), token.getName(), token.getGuid(), token.getId());
   }

   @Override
   public ArtifactToken copyArtifact(ArtifactReadable sourceArtifact) throws OseeCoreException {
      return copyArtifact(sourceArtifact.getBranch(), sourceArtifact);
   }

   @Override
   public ArtifactToken copyArtifact(BranchId fromBranch, ArtifactId artifactId) throws OseeCoreException {
      return txManager.copyArtifact(txData, fromBranch, artifactId);
   }

   @Override
   public ArtifactToken copyArtifact(ArtifactReadable sourceArtifact, Collection<AttributeTypeId> attributesToDuplicate) throws OseeCoreException {
      return copyArtifact(sourceArtifact.getBranch(), sourceArtifact, attributesToDuplicate);
   }

   @Override
   public ArtifactToken copyArtifact(BranchId fromBranch, ArtifactId artifactId, Collection<AttributeTypeId> attributesToDuplicate) throws OseeCoreException {
      return txManager.copyArtifact(txData, fromBranch, artifactId, attributesToDuplicate);
   }

   @Override
   public ArtifactToken introduceArtifact(BranchId fromBranch, ArtifactId sourceArtifact) throws OseeCoreException {
      checkAreOnDifferentBranches(txData, fromBranch);
      ArtifactReadable source = getArtifactReadable(txData.getSession(), query, fromBranch, sourceArtifact);
      Conditions.checkNotNull(source, "Source Artifact");
      ArtifactReadable destination =
         getArtifactReadable(txData.getSession(), query, txData.getBranch(), sourceArtifact);
      return txManager.introduceArtifact(txData, fromBranch, source, destination);
   }

   @Override
   public ArtifactToken replaceWithVersion(ArtifactReadable sourceArtifact, ArtifactReadable destination) throws OseeCoreException {
      return txManager.replaceWithVersion(txData, sourceArtifact.getBranch(), sourceArtifact, destination);
   }

   @Override
   public AttributeId createAttribute(ArtifactId sourceArtifact, AttributeTypeId attributeType) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      return asArtifact.createAttribute(attributeType);
   }

   @Override
   public <T> AttributeId createAttribute(ArtifactId sourceArtifact, AttributeTypeId attributeType, T value) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      return asArtifact.createAttribute(attributeType, value);
   }

   @Override
   public AttributeId createAttributeFromString(ArtifactId sourceArtifact, AttributeTypeId attributeType, String value) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      return asArtifact.createAttributeFromString(attributeType, value);
   }

   @Override
   public <T> void setSoleAttributeValue(ArtifactId sourceArtifact, AttributeTypeId attributeType, T value) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.setSoleAttributeValue(attributeType, value);
   }

   @Override
   public void setSoleAttributeFromStream(ArtifactId sourceArtifact, AttributeTypeId attributeType, InputStream stream) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.setSoleAttributeFromStream(attributeType, stream);
   }

   @Override
   public void setSoleAttributeFromString(ArtifactId sourceArtifact, AttributeTypeId attributeType, String value) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.setSoleAttributeFromString(attributeType, value);
   }

   @Override
   public void setName(ArtifactId sourceArtifact, String value) throws OseeCoreException {
      setSoleAttributeFromString(sourceArtifact, CoreAttributeTypes.Name, value);
   }

   @Override
   public <T> void setAttributesFromValues(ArtifactId sourceArtifact, AttributeTypeId attributeType, T... values) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.setAttributesFromValues(attributeType, values);
   }

   @Override
   public <T> void setAttributesFromValues(ArtifactId sourceArtifact, AttributeTypeId attributeType, Collection<T> values) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.setAttributesFromValues(attributeType, values);
   }

   @Override
   public void setAttributesFromStrings(ArtifactId sourceArtifact, AttributeTypeId attributeType, String... values) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.setAttributesFromStrings(attributeType, values);
   }

   @Override
   public void setAttributesFromStrings(ArtifactId sourceArtifact, AttributeTypeId attributeType, Collection<String> values) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.setAttributesFromStrings(attributeType, values);
   }

   @Override
   public <T> void setAttributeById(ArtifactId sourceArtifact, AttributeId attrId, T value) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.getAttributeById(attrId.getLocalId()).setValue(value);
   }

   @Override
   public void setAttributeById(ArtifactId sourceArtifact, AttributeId attrId, String value) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.getAttributeById(attrId.getLocalId()).setFromString(value);
   }

   @Override
   public void setAttributeById(ArtifactId sourceArtifact, AttributeId attrId, InputStream stream) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.getAttributeById(attrId.getLocalId()).setValueFromInputStream(stream);
   }

   @Override
   public void setAttributeApplicability(ArtifactId art, AttributeId attrId, ApplicabilityId applicId) {
      Artifact asArtifact = getForWrite(art);
      Attribute<Object> attribute = asArtifact.getAttributeById(attrId.getLocalId());
      attribute.getOrcsData().setApplicabilityId(applicId);
   }

   @Override
   public void deleteByAttributeId(ArtifactId sourceArtifact, AttributeId attrId) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.getAttributeById(attrId.getLocalId()).delete();
   }

   @Override
   public void deleteSoleAttribute(ArtifactId sourceArtifact, AttributeTypeId attributeType) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.deleteSoleAttribute(attributeType);
   }

   @Override
   public void deleteAttributes(ArtifactId sourceArtifact, AttributeTypeId attributeType) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.deleteAttributes(attributeType);
   }

   @Override
   public void deleteAttributesWithValue(ArtifactId sourceArtifact, AttributeTypeId attributeType, Object value) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.deleteAttributesWithValue(attributeType, value);
   }

   @Override
   public void addChildren(ArtifactId artA, ArtifactId... children) throws OseeCoreException {
      addChildren(artA, Arrays.asList(children));
   }

   @Override
   public void addChildren(ArtifactId artA, Iterable<? extends ArtifactId> children) throws OseeCoreException {
      txManager.addChildren(txData, artA, children);
   }

   @Override
   public void relate(ArtifactId artA, IRelationType relType, ArtifactId artB) throws OseeCoreException {
      txManager.relate(txData, artA, relType, artB);
   }

   @Override
   public void relate(ArtifactId artA, IRelationType relType, ArtifactId artB, String rationale) throws OseeCoreException {
      txManager.relate(txData, artA, relType, artB, rationale);
   }

   @Override
   public void relate(ArtifactId artA, IRelationType relType, ArtifactId artB, RelationSorter sortType) throws OseeCoreException {
      txManager.relate(txData, artA, relType, artB, sortType);
   }

   @Override
   public void relate(ArtifactId artA, IRelationType relType, ArtifactId artB, String rationale, RelationSorter sortType) throws OseeCoreException {
      txManager.relate(txData, artA, relType, artB, rationale, sortType);
   }

   @Override
   public void setRelations(ArtifactId artA, IRelationType relType, Iterable<? extends ArtifactId> artBs) throws OseeCoreException {
      txManager.setRelations(txData, artA, relType, artBs);
   }

   @Override
   public void setRationale(ArtifactId artA, IRelationType relType, ArtifactId artB, String rationale) throws OseeCoreException {
      txManager.setRationale(txData, artA, relType, artB, rationale);
   }

   @Override
   public void unrelate(ArtifactId artA, IRelationType relType, ArtifactId artB) throws OseeCoreException {
      txManager.unrelate(txData, artA, relType, artB);
   }

   @Override
   public void unrelateFromAll(RelationTypeSide typeAndSide, ArtifactId art) throws OseeCoreException {
      IRelationType type = typeAndSide.getRelationType();
      txManager.unrelateFromAll(txData, type, art, typeAndSide.getSide());
   }

   @Override
   public void unrelateFromAll(ArtifactId artA) throws OseeCoreException {
      txManager.unrelateFromAll(txData, artA);
   }

   @Override
   public void setRelationApplicability(ArtifactId artA, IRelationType relType, ArtifactId artB, ApplicabilityId applicId) {
      txManager.setRelationApplicabilityId(txData, artA, relType, artB, applicId);
   }

   @Override
   public void deleteArtifact(ArtifactId sourceArtifact) throws OseeCoreException {
      txManager.deleteArtifact(txData, sourceArtifact);
   }

   @Override
   public boolean isCommitInProgress() {
      return txData.isCommitInProgress();
   }

   @Override
   public TransactionReadable commit() throws OseeCoreException {
      try {
         CancellableCallable<TransactionReadable> callable = txFactory.createTx(txData);
         return callable.call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   private void checkAreOnDifferentBranches(TxData txData, BranchId sourceBranch) throws OseeCoreException {
      boolean isOnSameBranch = txData.isOnBranch(sourceBranch);
      Conditions.checkExpressionFailOnTrue(isOnSameBranch, "Source branch is same branch as transaction branch[%s]",
         txData.getBranch());
   }

   protected ArtifactReadable getArtifactReadable(OrcsSession session, QueryModule query, BranchId branch, ArtifactId id) {
      return query.createQueryFactory(session).fromBranch(branch).includeDeletedArtifacts().andId(
         id).getResults().getOneOrNull();
   }

   @Override
   public void setApplicability(ArtifactId artId, ApplicabilityId applicId) {
      txManager.setApplicabilityId(txData, artId, applicId);
   }

   private Long insertValue(String value) {
      return keyValueOps.putIfAbsent(value);
   }

   @Override
   public <E1, E2> GammaId addTuple2(Tuple2Type<E1, E2> tupleType, E1 e1, E2 e2) {
      return txManager.createTuple2(txData, tupleType, toLong(e1), toLong(e2));
   }

   @Override
   public <E1, E2, E3> GammaId addTuple3(Tuple3Type<E1, E2, E3> tupleType, E1 e1, E2 e2, E3 e3) {
      return txManager.createTuple3(txData, tupleType, toLong(e1), toLong(e2), toLong(e3));
   }

   @Override
   public <E1, E2, E3, E4> GammaId addTuple4(Tuple4Type<E1, E2, E3, E4> tupleType, E1 e1, E2 e2, E3 e3, E4 e4) {
      return txManager.createTuple4(txData, tupleType, toLong(e1), toLong(e2), toLong(e3), toLong(e4));
   }

   private Long toLong(Object element) {
      if (element instanceof String) {
         return insertValue((String) element);
      } else if (element instanceof Id) {
         return ((Id) element).getId();
      }
      return (Long) element;
   }

   @Override
   public boolean deleteTuple(Long gammaId) {
      return false;
   }

   @Override
   public <E1, E2> boolean deleteTuple2(Tuple2Type<E1, E2> tupleType, E1 element1, E2 element2) {
      return false;
   }

   @Override
   public <E1, E2, E3> boolean deleteTupple3(Tuple3Type<E1, E2, E3> tupleType, E1 element1, E2 element2, E3 element3) {
      return false;
   }

   @Override
   public <E1, E2, E3, E4> boolean deleteTupple4(Tuple4Type<E1, E2, E3, E4> tupleType, E1 element1, E2 element2, E3 element3, E4 element4) {
      return false;
   }
}