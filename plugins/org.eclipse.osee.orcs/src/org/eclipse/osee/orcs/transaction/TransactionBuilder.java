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
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.TransactionReadable;

/**
 * @author Roberto E. Escobar
 */
public interface TransactionBuilder {

   BranchId getBranch();

   String getComment();

   void setComment(String comment) throws OseeCoreException;

   /**
    * @return TransactionRecord or null of no changes made
    */
   TransactionReadable commit() throws OseeCoreException;

   boolean isCommitInProgress();

   // ARTIFACT

   ArtifactToken createArtifact(IArtifactType artifactType, String name) throws OseeCoreException;

   ArtifactToken createArtifact(IArtifactType artifactType, String name, String guid) throws OseeCoreException;

   ArtifactToken createArtifact(IArtifactType artifactType, String name, String guid, long uuid) throws OseeCoreException;

   ArtifactToken createArtifact(ArtifactToken configsFolder);

   void deleteArtifact(ArtifactId sourceArtifact) throws OseeCoreException;

   ArtifactToken copyArtifact(ArtifactReadable sourceArtifact) throws OseeCoreException;

   ArtifactToken copyArtifact(ArtifactReadable sourceArtifact, Collection<AttributeTypeId> attributesToDuplicate) throws OseeCoreException;

   ArtifactToken copyArtifact(BranchId fromBranch, ArtifactId sourceArtifact) throws OseeCoreException;

   ArtifactToken copyArtifact(BranchId fromBranch, ArtifactId sourceArtifact, Collection<AttributeTypeId> attributesToDuplicate) throws OseeCoreException;

   ArtifactToken introduceArtifact(BranchId fromBranch, ArtifactId sourceArtifact) throws OseeCoreException;

   ArtifactToken replaceWithVersion(ArtifactReadable sourceArtifact, ArtifactReadable destination) throws OseeCoreException;

   // ATTRIBUTE

   void setName(ArtifactId art, String value) throws OseeCoreException;

   AttributeId createAttribute(ArtifactId art, AttributeTypeId attributeType) throws OseeCoreException;

   <T> AttributeId createAttribute(ArtifactId art, AttributeTypeId attributeType, T value) throws OseeCoreException;

   AttributeId createAttributeFromString(ArtifactId art, AttributeTypeId attributeType, String value) throws OseeCoreException;

   <T> void setSoleAttributeValue(ArtifactId art, AttributeTypeId attributeType, T value) throws OseeCoreException;

   void setSoleAttributeFromStream(ArtifactId art, AttributeTypeId attributeType, InputStream stream) throws OseeCoreException;

   void setSoleAttributeFromString(ArtifactId art, AttributeTypeId attributeType, String value) throws OseeCoreException;

   <T> void setAttributesFromValues(ArtifactId art, AttributeTypeId attributeType, T... values) throws OseeCoreException;

   <T> void setAttributesFromValues(ArtifactId art, AttributeTypeId attributeType, Collection<T> values) throws OseeCoreException;

   void setAttributesFromStrings(ArtifactId art, AttributeTypeId attributeType, String... values) throws OseeCoreException;

   void setAttributesFromStrings(ArtifactId art, AttributeTypeId attributeType, Collection<String> values) throws OseeCoreException;

   <T> void setAttributeById(ArtifactId art, AttributeId attrId, T value) throws OseeCoreException;

   void setAttributeById(ArtifactId art, AttributeId attrId, String value) throws OseeCoreException;

   void setAttributeById(ArtifactId art, AttributeId attrId, InputStream stream) throws OseeCoreException;

   void setAttributeApplicability(ArtifactId art, AttributeId attrId, ApplicabilityId applicId);

   void deleteByAttributeId(ArtifactId art, AttributeId attrId) throws OseeCoreException;

   void deleteSoleAttribute(ArtifactId art, AttributeTypeId attributeType) throws OseeCoreException;

   void deleteAttributes(ArtifactId art, AttributeTypeId attributeType) throws OseeCoreException;

   void deleteAttributesWithValue(ArtifactId art, AttributeTypeId attributeType, Object value) throws OseeCoreException;

   /// TX

   void addChildren(ArtifactId artA, Iterable<? extends ArtifactId> children) throws OseeCoreException;

   void addChildren(ArtifactId artA, ArtifactId... children) throws OseeCoreException;

   void relate(ArtifactId artA, IRelationType relType, ArtifactId artB) throws OseeCoreException;

   void relate(ArtifactId artA, IRelationType relType, ArtifactId artB, String rationale) throws OseeCoreException;

   void relate(ArtifactId artA, IRelationType relType, ArtifactId artB, RelationSorter sortType) throws OseeCoreException;

   void relate(ArtifactId artA, IRelationType relType, ArtifactId artB, String rationale, RelationSorter sortType) throws OseeCoreException;

   void setRelations(ArtifactId artA, IRelationType relType, Iterable<? extends ArtifactId> artBs) throws OseeCoreException;

   void setRationale(ArtifactId artA, IRelationType relType, ArtifactId artB, String rationale) throws OseeCoreException;

   void unrelate(ArtifactId artA, IRelationType relType, ArtifactId artB) throws OseeCoreException;

   void unrelateFromAll(ArtifactId art) throws OseeCoreException;

   void unrelateFromAll(RelationTypeSide typeSide, ArtifactId art) throws OseeCoreException;

   void setRelationApplicability(ArtifactId artA, IRelationType relType, ArtifactId artB, ApplicabilityId applicId);

   // Applic
   void setApplicability(ArtifactId art, ApplicabilityId applicId);

   // Tuples
   <E1, E2> GammaId addTuple2(Tuple2Type<E1, E2> tupleType, E1 e1, E2 e2);

   <E1, E2, E3> GammaId addTuple3(Tuple3Type<E1, E2, E3> tupleType, E1 e1, E2 e2, E3 e3);

   <E1, E2, E3, E4> GammaId addTuple4(Tuple4Type<E1, E2, E3, E4> tupleType, E1 e1, E2 e2, E3 e3, E4 e4);

   boolean deleteTuple(Long gammaId);

   <E1, E2> boolean deleteTuple2(Tuple2Type<E1, E2> tupleType, E1 e1, E2 e2);

   <E1, E2, E3> boolean deleteTupple3(Tuple3Type<E1, E2, E3> tupleType, E1 e1, E2 e2, E3 e3);

   <E1, E2, E3, E4> boolean deleteTupple4(Tuple4Type<E1, E2, E3, E4> tupleType, E1 e1, E2 e2, E3 e3, E4 e4);

}
