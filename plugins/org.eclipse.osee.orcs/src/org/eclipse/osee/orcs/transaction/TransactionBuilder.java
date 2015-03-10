/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.transaction;

import java.io.InputStream;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationSorterId;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.data.ArtifactId;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeId;
import org.eclipse.osee.orcs.data.TransactionReadable;

/**
 * @author Roberto E. Escobar
 */
public interface TransactionBuilder {

   IOseeBranch getBranch();

   Identifiable<String> getAuthor();

   String getComment();

   void setComment(String comment) throws OseeCoreException;

   TransactionReadable commit() throws OseeCoreException;

   boolean isCommitInProgress();

   // ARTIFACT

   ArtifactId createArtifact(IArtifactType artifactType, String name) throws OseeCoreException;

   ArtifactId createArtifact(IArtifactType artifactType, String name, String guid) throws OseeCoreException;

   ArtifactId createArtifact(IArtifactToken configsFolder);

   void deleteArtifact(ArtifactId sourceArtifact) throws OseeCoreException;

   ArtifactId copyArtifact(ArtifactReadable sourceArtifact) throws OseeCoreException;

   ArtifactId copyArtifact(ArtifactReadable sourceArtifact, Collection<? extends IAttributeType> attributesToDuplicate) throws OseeCoreException;

   ArtifactId copyArtifact(IOseeBranch fromBranch, ArtifactId sourceArtifact) throws OseeCoreException;

   ArtifactId copyArtifact(IOseeBranch fromBranch, ArtifactId sourceArtifact, Collection<? extends IAttributeType> attributesToDuplicate) throws OseeCoreException;

   ArtifactId introduceArtifact(IOseeBranch fromBranch, ArtifactId sourceArtifact) throws OseeCoreException;

   ArtifactId replaceWithVersion(ArtifactReadable sourceArtifact, ArtifactReadable destination) throws OseeCoreException;

   // ATTRIBUTE

   void setName(ArtifactId art, String value) throws OseeCoreException;

   AttributeId createAttribute(ArtifactId art, IAttributeType attributeType) throws OseeCoreException;

   <T> AttributeId createAttribute(ArtifactId art, IAttributeType attributeType, T value) throws OseeCoreException;

   AttributeId createAttributeFromString(ArtifactId art, IAttributeType attributeType, String value) throws OseeCoreException;

   <T> void setSoleAttributeValue(ArtifactId art, IAttributeType attributeType, T value) throws OseeCoreException;

   void setSoleAttributeFromStream(ArtifactId art, IAttributeType attributeType, InputStream stream) throws OseeCoreException;

   void setSoleAttributeFromString(ArtifactId art, IAttributeType attributeType, String value) throws OseeCoreException;

   <T> void setAttributesFromValues(ArtifactId art, IAttributeType attributeType, T... values) throws OseeCoreException;

   <T> void setAttributesFromValues(ArtifactId art, IAttributeType attributeType, Collection<T> values) throws OseeCoreException;

   void setAttributesFromStrings(ArtifactId art, IAttributeType attributeType, String... values) throws OseeCoreException;

   void setAttributesFromStrings(ArtifactId art, IAttributeType attributeType, Collection<String> values) throws OseeCoreException;

   <T> void setAttributeById(ArtifactId art, AttributeId attrId, T value) throws OseeCoreException;

   void setAttributeById(ArtifactId art, AttributeId attrId, String value) throws OseeCoreException;

   void setAttributeById(ArtifactId art, AttributeId attrId, InputStream stream) throws OseeCoreException;

   void deleteByAttributeId(ArtifactId art, AttributeId attrId) throws OseeCoreException;

   void deleteSoleAttribute(ArtifactId art, IAttributeType attributeType) throws OseeCoreException;

   void deleteAttributes(ArtifactId art, IAttributeType attributeType) throws OseeCoreException;

   void deleteAttributesWithValue(ArtifactId art, IAttributeType attributeType, Object value) throws OseeCoreException;

   /// TX

   void addChildren(ArtifactId artA, Iterable<? extends ArtifactId> children) throws OseeCoreException;

   void addChildren(ArtifactId artA, ArtifactId... children) throws OseeCoreException;

   void relate(ArtifactId artA, IRelationType relType, ArtifactId artB) throws OseeCoreException;

   void relate(ArtifactId artA, IRelationType relType, ArtifactId artB, String rationale) throws OseeCoreException;

   void relate(ArtifactId artA, IRelationType relType, ArtifactId artB, IRelationSorterId sortType) throws OseeCoreException;

   void relate(ArtifactId artA, IRelationType relType, ArtifactId artB, String rationale, IRelationSorterId sortType) throws OseeCoreException;

   void setRelations(ArtifactId artA, IRelationType relType, Iterable<? extends ArtifactId> artBs) throws OseeCoreException;

   void setRationale(ArtifactId artA, IRelationType relType, ArtifactId artB, String rationale) throws OseeCoreException;

   void unrelate(ArtifactId artA, IRelationType relType, ArtifactId artB) throws OseeCoreException;

   void unrelateFromAll(ArtifactId art) throws OseeCoreException;

   void unrelateFromAll(IRelationTypeSide typeSide, ArtifactId art) throws OseeCoreException;

}
