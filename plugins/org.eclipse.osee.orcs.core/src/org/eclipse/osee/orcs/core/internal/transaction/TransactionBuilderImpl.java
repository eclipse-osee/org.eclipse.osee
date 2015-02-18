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
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationSorterId;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.relation.RelationUtil;
import org.eclipse.osee.orcs.data.ArtifactId;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeId;
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

   public TransactionBuilderImpl(TxCallableFactory txFactory, TxDataManager dataManager, TxData txData) {
      super();
      this.txFactory = txFactory;
      this.txManager = dataManager;
      this.txData = txData;
   }

   private Artifact getForWrite(ArtifactId artifactId) throws OseeCoreException {
      return txManager.getForWrite(txData, artifactId);
   }

   @Override
   public IOseeBranch getBranch() {
      return txData.getBranch();
   }

   @Override
   public ArtifactReadable getAuthor() {
      return txData.getAuthor();
   }

   @Override
   public String getComment() {
      return txData.getComment();
   }

   @Override
   public void setComment(String comment) throws OseeCoreException {
      txManager.setComment(txData, comment);
   }

   public void setAuthor(ArtifactReadable author) throws OseeCoreException {
      txManager.setAuthor(txData, author);
   }

   @Override
   public ArtifactId createArtifact(IArtifactType artifactType, String name) throws OseeCoreException {
      return createArtifact(artifactType, name, null);
   }

   @Override
   public ArtifactId createArtifact(IArtifactType artifactType, String name, String guid) throws OseeCoreException {
      return txManager.createArtifact(txData, artifactType, name, guid);
   }

   @Override
   public ArtifactId createArtifact(IArtifactToken token) throws OseeCoreException {
      return txManager.createArtifact(txData, token.getArtifactType(), token.getName(), token.getGuid());
   }

   @Override
   public ArtifactId copyArtifact(ArtifactReadable sourceArtifact) throws OseeCoreException {
      return copyArtifact(sourceArtifact.getBranch(), sourceArtifact);
   }

   @Override
   public ArtifactId copyArtifact(IOseeBranch fromBranch, ArtifactId artifactId) throws OseeCoreException {
      return txManager.copyArtifact(txData, fromBranch, artifactId);
   }

   @Override
   public ArtifactId copyArtifact(ArtifactReadable sourceArtifact, Collection<? extends IAttributeType> attributesToDuplicate) throws OseeCoreException {
      return copyArtifact(sourceArtifact.getBranch(), sourceArtifact, attributesToDuplicate);
   }

   @Override
   public ArtifactId copyArtifact(IOseeBranch fromBranch, ArtifactId artifactId, Collection<? extends IAttributeType> attributesToDuplicate) throws OseeCoreException {
      return txManager.copyArtifact(txData, fromBranch, artifactId, attributesToDuplicate);
   }

   @Override
   public ArtifactId introduceArtifact(ArtifactReadable sourceArtifact) throws OseeCoreException {
      return introduceArtifact(sourceArtifact.getBranch(), sourceArtifact);
   }

   @Override
   public ArtifactId introduceArtifact(IOseeBranch fromBranch, ArtifactId artifactId) throws OseeCoreException {
      return txManager.introduceArtifact(txData, fromBranch, artifactId);
   }

   @Override
   public AttributeId createAttribute(ArtifactId sourceArtifact, IAttributeType attributeType) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      return asArtifact.createAttribute(attributeType);
   }

   @Override
   public <T> AttributeId createAttribute(ArtifactId sourceArtifact, IAttributeType attributeType, T value) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      return asArtifact.createAttribute(attributeType, value);
   }

   @Override
   public AttributeId createAttributeFromString(ArtifactId sourceArtifact, IAttributeType attributeType, String value) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      return asArtifact.createAttributeFromString(attributeType, value);
   }

   @Override
   public <T> void setSoleAttributeValue(ArtifactId sourceArtifact, IAttributeType attributeType, T value) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.setSoleAttributeValue(attributeType, value);
   }

   @Override
   public void setSoleAttributeFromStream(ArtifactId sourceArtifact, IAttributeType attributeType, InputStream stream) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.setSoleAttributeFromStream(attributeType, stream);
   }

   @Override
   public void setSoleAttributeFromString(ArtifactId sourceArtifact, IAttributeType attributeType, String value) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.setSoleAttributeFromString(attributeType, value);
   }

   @Override
   public void setName(ArtifactId sourceArtifact, String value) throws OseeCoreException {
      setSoleAttributeFromString(sourceArtifact, CoreAttributeTypes.Name, value);
   }

   @Override
   public <T> void setAttributesFromValues(ArtifactId sourceArtifact, IAttributeType attributeType, T... values) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.setAttributesFromValues(attributeType, values);
   }

   @Override
   public <T> void setAttributesFromValues(ArtifactId sourceArtifact, IAttributeType attributeType, Collection<T> values) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.setAttributesFromValues(attributeType, values);
   }

   @Override
   public void setAttributesFromStrings(ArtifactId sourceArtifact, IAttributeType attributeType, String... values) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.setAttributesFromStrings(attributeType, values);
   }

   @Override
   public void setAttributesFromStrings(ArtifactId sourceArtifact, IAttributeType attributeType, Collection<String> values) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.setAttributesFromStrings(attributeType, values);
   }

   @Override
   public <T> void setAttributeById(ArtifactId sourceArtifact, AttributeId attrId, T value) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.getAttributeById(attrId).setValue(value);
   }

   @Override
   public void setAttributeById(ArtifactId sourceArtifact, AttributeId attrId, String value) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.getAttributeById(attrId).setFromString(value);
   }

   @Override
   public void setAttributeById(ArtifactId sourceArtifact, AttributeId attrId, InputStream stream) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.getAttributeById(attrId).setValueFromInputStream(stream);
   }

   @Override
   public void deleteByAttributeId(ArtifactId sourceArtifact, AttributeId attrId) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.getAttributeById(attrId).delete();
   }

   @Override
   public void deleteSoleAttribute(ArtifactId sourceArtifact, IAttributeType attributeType) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.deleteSoleAttribute(attributeType);
   }

   @Override
   public void deleteAttributes(ArtifactId sourceArtifact, IAttributeType attributeType) throws OseeCoreException {
      Artifact asArtifact = getForWrite(sourceArtifact);
      asArtifact.deleteAttributes(attributeType);
   }

   @Override
   public void deleteAttributesWithValue(ArtifactId sourceArtifact, IAttributeType attributeType, Object value) throws OseeCoreException {
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
   public void relate(ArtifactId artA, IRelationType relType, ArtifactId artB, IRelationSorterId sortType) throws OseeCoreException {
      txManager.relate(txData, artA, relType, artB, sortType);
   }

   @Override
   public void relate(ArtifactId artA, IRelationType relType, ArtifactId artB, String rationale, IRelationSorterId sortType) throws OseeCoreException {
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
   public void unrelateFromAll(IRelationTypeSide typeAndSide, ArtifactId art) throws OseeCoreException {
      IRelationType type = RelationUtil.asRelationType(typeAndSide);
      txManager.unrelateFromAll(txData, type, art, typeAndSide.getSide());
   }

   @Override
   public void unrelateFromAll(ArtifactId artA) throws OseeCoreException {
      txManager.unrelateFromAll(txData, artA);
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
      TransactionReadable tx = null;
      try {
         CancellableCallable<TransactionReadable> callable = txFactory.createTx(txData);
         tx = callable.call();
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return tx;
   }

}
