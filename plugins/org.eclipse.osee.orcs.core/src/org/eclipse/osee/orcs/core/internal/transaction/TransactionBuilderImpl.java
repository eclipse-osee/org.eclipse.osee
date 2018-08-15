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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.Tuple2Type;
import org.eclipse.osee.framework.core.data.Tuple3Type;
import org.eclipse.osee.framework.core.data.Tuple4Type;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.KeyValueOps;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.TupleQuery;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Roberto E. Escobar
 * @author Megumi Telles
 */
public class TransactionBuilderImpl implements TransactionBuilder {

   private final TxCallableFactory txFactory;
   private final TxDataManager txManager;
   private final TxData txData;
   private final QueryFactory queryFactory;
   private final OrcsApi orcsApi;
   private final KeyValueOps keyValueOps;

   public TransactionBuilderImpl(TxCallableFactory txFactory, TxDataManager dataManager, TxData txData, OrcsApi orcsApi, KeyValueOps keyValueOps) {
      this.txFactory = txFactory;
      this.txManager = dataManager;
      this.txData = txData;
      this.orcsApi = orcsApi;
      this.queryFactory = orcsApi.getQueryFactory();
      this.keyValueOps = keyValueOps;
   }

   private Artifact getForWrite(ArtifactId artifactId) {
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
   public void setComment(String comment) {
      txManager.setComment(txData, comment);
   }

   public void setAuthor(UserId author) {
      txManager.setAuthor(txData, author);
   }

   @Override
   public ArtifactToken createArtifact(ArtifactId parent, ArtifactToken token) {
      ArtifactToken child = createArtifact(token);
      if (parent.isValid()) {
         addChild(parent, child);
      }
      return child;
   }

   @Override
   public ArtifactToken createArtifact(ArtifactId parent, IArtifactType artifactType, String name) {
      ArtifactToken child = createArtifact(artifactType, name);
      if (parent.isValid()) {
         addChild(parent, child);
      }
      return child;
   }

   @Override
   public ArtifactToken createArtifact(IArtifactType artifactType, String name) {
      return txManager.createArtifact(txData, artifactType, name, (String) null);
   }

   @Override
   public ArtifactToken createArtifact(ArtifactToken token) {
      return txManager.createArtifact(txData, token.getArtifactTypeId(), token.getName(), token.getUuid());
   }

   @Override
   public ArtifactToken createArtifact(IArtifactType artifactType, String name, Long artifactId) {
      return txManager.createArtifact(txData, artifactType, name, artifactId);
   }

   @Override
   public ArtifactToken createArtifact(IArtifactType artifactType, String name, String guid) {
      return txManager.createArtifact(txData, artifactType, name, guid);
   }

   @Override
   public ArtifactToken createArtifact(IArtifactType artifactType, String name, Long artifactId, String guid) {
      return txManager.createArtifact(txData, artifactType, name, artifactId, guid);
   }

   @Override
   public List<ArtifactToken> createArtifacts(ArtifactTypeId artifactType, ArtifactId parent, List<String> names) {
      ResultSet<ArtifactReadable> results =
         queryFactory.fromBranch(getBranch()).andTypeEquals(artifactType).and(CoreAttributeTypes.Name,
            names).getResults();
      if (!results.isEmpty()) {
         throw new OseeCoreException("Found %s artifacts of type %s with duplicate names: %s", results.size(),
            artifactType, results.getList());
      }

      List<ArtifactToken> tokens = new ArrayList<>(names.size());

      IArtifactType artifactTypeToken = orcsApi.getOrcsTypes().getArtifactTypes().get(artifactType);
      for (String name : names) {
         tokens.add(createArtifact(parent, artifactTypeToken, name));
      }
      return tokens;
   }

   @Override
   public ArtifactToken copyArtifact(ArtifactReadable sourceArtifact) {
      return copyArtifact(sourceArtifact.getBranch(), sourceArtifact);
   }

   @Override
   public ArtifactToken copyArtifact(BranchId fromBranch, ArtifactId artifactId) {
      return txManager.copyArtifact(txData, fromBranch, artifactId);
   }

   @Override
   public ArtifactToken copyArtifact(ArtifactReadable sourceArtifact, Collection<AttributeTypeId> attributesToDuplicate) {
      return copyArtifact(sourceArtifact.getBranch(), sourceArtifact, attributesToDuplicate);
   }

   @Override
   public ArtifactToken copyArtifact(BranchId fromBranch, ArtifactId artifactId, Collection<AttributeTypeId> attributesToDuplicate) {
      return txManager.copyArtifact(txData, fromBranch, artifactId, attributesToDuplicate);
   }

   @Override
   public ArtifactToken introduceArtifact(BranchId fromBranch, ArtifactId sourceArtifact) {
      checkAreOnDifferentBranches(txData, fromBranch);
      ArtifactReadable source = getArtifactReadable(txData.getSession(), queryFactory, fromBranch, sourceArtifact);
      Conditions.checkNotNull(source, "Source Artifact");
      ArtifactReadable destination =
         getArtifactReadable(txData.getSession(), queryFactory, txData.getBranch(), sourceArtifact);
      return txManager.introduceArtifact(txData, fromBranch, source, destination);
   }

   @Override
   public ArtifactToken replaceWithVersion(ArtifactReadable sourceArtifact, ArtifactReadable destination) {
      return txManager.replaceWithVersion(txData, sourceArtifact.getBranch(), sourceArtifact, destination);
   }

   @Override
   public AttributeId createAttribute(ArtifactId sourceArtifact, AttributeTypeToken attributeType) {
      Artifact asArtifact = getForWrite(sourceArtifact);
      return asArtifact.createAttribute(attributeType);
   }

   @Override
   public <T> AttributeId createAttribute(ArtifactId sourceArtifact, AttributeTypeToken attributeType, T value) {
      Artifact asArtifact = getForWrite(sourceArtifact);
      return asArtifact.createAttribute(attributeType, value);
   }

   @Override
   public <T> void setSoleAttributeValue(ArtifactId sourceArtifact, AttributeTypeToken attributeType, T value) {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.setSoleAttributeValue(attributeType, value);
   }

   @Override
   public void setSoleAttributeFromStream(ArtifactId sourceArtifact, AttributeTypeToken attributeType, InputStream stream) {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.setSoleAttributeFromStream(attributeType, stream);
   }

   @Override
   public void setSoleAttributeFromString(ArtifactId sourceArtifact, AttributeTypeToken attributeType, String value) {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.setSoleAttributeFromString(attributeType, value);
   }

   @Override
   public void setName(ArtifactId sourceArtifact, String value) {
      setSoleAttributeFromString(sourceArtifact, CoreAttributeTypes.Name, value);
   }

   @Override
   public <T> void setAttributesFromValues(ArtifactId sourceArtifact, AttributeTypeToken attributeType, Collection<T> values) {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.setAttributesFromValues(attributeType, values);
   }

   @Override
   public void setAttributesFromStrings(ArtifactId sourceArtifact, AttributeTypeToken attributeType, String... values) {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.setAttributesFromStrings(attributeType, values);
   }

   @Override
   public void setAttributesFromStrings(ArtifactId sourceArtifact, AttributeTypeToken attributeType, Collection<String> values) {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.setAttributesFromStrings(attributeType, values);
   }

   @Override
   public <T> void setAttributeById(ArtifactId sourceArtifact, AttributeId attrId, T value) {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.getAttributeById(attrId).setValue(value);
   }

   @Override
   public void setAttributeById(ArtifactId sourceArtifact, AttributeId attrId, String value) {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.getAttributeById(attrId).setFromString(value);
   }

   @Override
   public void setAttributeById(ArtifactId sourceArtifact, AttributeId attrId, InputStream stream) {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.getAttributeById(attrId).setValueFromInputStream(stream);
   }

   @Override
   public void setAttributeApplicability(ArtifactId art, AttributeId attrId, ApplicabilityId applicId) {
      Artifact asArtifact = getForWrite(art);
      Attribute<Object> attribute = asArtifact.getAttributeById(attrId);
      attribute.getOrcsData().setApplicabilityId(applicId);
   }

   @Override
   public void deleteByAttributeId(ArtifactId sourceArtifact, AttributeId attrId) {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.getAttributeById(attrId).delete();
   }

   @Override
   public void deleteSoleAttribute(ArtifactId sourceArtifact, AttributeTypeId attributeType) {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.deleteSoleAttribute(attributeType);
   }

   @Override
   public void deleteAttributes(ArtifactId sourceArtifact, AttributeTypeId attributeType) {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.deleteAttributes(attributeType);
   }

   @Override
   public void deleteAttributesWithValue(ArtifactId sourceArtifact, AttributeTypeId attributeType, Object value) {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.deleteAttributesWithValue(attributeType, value);
   }

   @Override
   public void addChild(ArtifactId parent, ArtifactId child) {
      txManager.addChild(txData, parent, child);
   }

   @Override
   public void relate(ArtifactId artA, RelationTypeToken relType, ArtifactId artB) {
      txManager.relate(txData, artA, relType, artB);
   }

   @Override
   public void relate(ArtifactId artA, RelationTypeToken relType, ArtifactId artB, String rationale) {
      txManager.relate(txData, artA, relType, artB, rationale);
   }

   @Override
   public void relate(ArtifactId artA, RelationTypeToken relType, ArtifactId artB, RelationSorter sortType) {
      txManager.relate(txData, artA, relType, artB, sortType);
   }

   @Override
   public void relate(ArtifactId artA, RelationTypeToken relType, ArtifactId artB, String rationale, RelationSorter sortType) {
      txManager.relate(txData, artA, relType, artB, rationale, sortType);
   }

   @Override
   public void setRelations(ArtifactId artA, RelationTypeToken relType, Iterable<? extends ArtifactId> artBs) {
      txManager.setRelations(txData, artA, relType, artBs);
   }

   @Override
   public void setRelationsAndOrder(ArtifactId artifact, RelationTypeSide relationSide, List<? extends ArtifactId> artifacts) {
      txManager.setRelationsAndOrder(txData, artifact, relationSide, artifacts);
   }

   @Override
   public void setRationale(ArtifactId artA, IRelationType relType, ArtifactId artB, String rationale) {
      txManager.setRationale(txData, artA, relType, artB, rationale);
   }

   @Override
   public void unrelate(ArtifactId artA, IRelationType relType, ArtifactId artB) {
      txManager.unrelate(txData, artA, relType, artB);
   }

   @Override
   public void unrelateFromAll(RelationTypeSide typeAndSide, ArtifactId art) {
      IRelationType type = typeAndSide.getRelationType();
      txManager.unrelateFromAll(txData, type, art, typeAndSide.getSide());
   }

   @Override
   public void unrelateFromAll(ArtifactId artA) {
      txManager.unrelateFromAll(txData, artA);
   }

   @Override
   public void setRelationApplicability(ArtifactId artA, IRelationType relType, ArtifactId artB, ApplicabilityId applicId) {
      txManager.setRelationApplicabilityId(txData, artA, relType, artB, applicId);
   }

   @Override
   public void deleteArtifact(ArtifactId sourceArtifact) {
      txManager.deleteArtifact(txData, sourceArtifact);
   }

   @Override
   public boolean isCommitInProgress() {
      return txData.isCommitInProgress();
   }

   @Override
   public TransactionReadable commit() {
      try {
         CancellableCallable<TransactionReadable> callable = txFactory.createTx(txData);
         return callable.call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   private void checkAreOnDifferentBranches(TxData txData, BranchId sourceBranch) {
      boolean isOnSameBranch = txData.isOnBranch(sourceBranch);
      Conditions.checkExpressionFailOnTrue(isOnSameBranch, "Source branch is same branch as transaction branch[%s]",
         txData.getBranch());
   }

   protected ArtifactReadable getArtifactReadable(OrcsSession session, QueryFactory queryFactory, BranchId branch, ArtifactId id) {
      return queryFactory.fromBranch(branch).includeDeletedArtifacts().andId(id).getResults().getOneOrNull();
   }

   @Override
   public void setApplicability(ArtifactId artId, ApplicabilityId applicId) {
      txManager.setApplicabilityId(txData, artId, applicId);
   }

   @Override
   public void setApplicabilityReference(HashMap<ArtifactId, List<ApplicabilityId>> artifacts) {
      TupleQuery tupleQuery = queryFactory.tupleQuery();

      for (Entry<? extends ArtifactId, List<ApplicabilityId>> entry : artifacts.entrySet()) {
         for (ApplicabilityId appId : entry.getValue()) {
            if (!tupleQuery.doesTuple2Exist(CoreTupleTypes.ArtifactReferenceApplicabilityType, entry.getKey(), appId)) {
               addTuple2(CoreTupleTypes.ArtifactReferenceApplicabilityType, entry.getKey(), appId);
            }
         }
      }
   }

   @Override
   public void setApplicability(ApplicabilityId applicId, List<? extends ArtifactId> artifacts) {
      for (ArtifactId artifact : artifacts) {
         setApplicability(artifact, applicId);
      }
   }

   @Override
   public ArtifactToken createView(BranchId branch, String viewName) {
      // Retrieve from transaction in case it has not be persisted yet
      ArtifactId plFolder = txData.getWriteable(CoreArtifactTokens.ProductsFolder);
      if (plFolder == null) {
         plFolder = CoreArtifactTokens.ProductsFolder;
      }
      return createArtifact(plFolder, CoreArtifactTypes.BranchView, viewName);
   }

   @Override
   public void createApplicabilityForView(ArtifactId viewId, String applicability) {
      addTuple2(CoreTupleTypes.ViewApplicability, viewId, applicability);

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
   public boolean deleteTuple(GammaId gammaId) {
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

   @Override
   public void addKeyValueOps(Long id, String name) {
      keyValueOps.putByKey(id, name);
   }

   @Override
   public ArtifactToken createArtifact(ArtifactToken parent, IArtifactType artifactType, String name, Long id) {
      ArtifactToken art = createArtifact(artifactType, name, id);
      txManager.addChild(txData, parent, art);
      return art;
   }

   @Override
   public ArtifactToken getWriteable(ArtifactId artifact) {
      ArtifactToken art = txData.getWriteable(artifact);
      if (art == null) {
         art = ArtifactToken.getSentinal();
      }
      return art;
   }

}