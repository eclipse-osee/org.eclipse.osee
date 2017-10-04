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
import java.util.HashMap;
import java.util.List;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.Tuple2Type;
import org.eclipse.osee.framework.core.data.Tuple3Type;
import org.eclipse.osee.framework.core.data.Tuple4Type;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.TransactionReadable;

/**
 * @author Roberto E. Escobar
 */
public interface TransactionBuilder {

   BranchId getBranch();

   String getComment();

   void setComment(String comment);

   /**
    * @return TransactionRecord or null of no changes made
    */
   TransactionReadable commit();

   boolean isCommitInProgress();

   // ARTIFACT

   ArtifactToken createArtifact(ArtifactTypeId artifactType, String name);

   ArtifactToken createArtifact(ArtifactTypeId artifactType, String name, String guid);

   ArtifactToken createArtifact(ArtifactTypeId artifactType, String name, String guid, long uuid);

   ArtifactToken createArtifact(ArtifactToken configsFolder);

   void deleteArtifact(ArtifactId sourceArtifact);

   ArtifactToken copyArtifact(ArtifactReadable sourceArtifact);

   ArtifactToken copyArtifact(ArtifactReadable sourceArtifact, Collection<AttributeTypeId> attributesToDuplicate);

   ArtifactToken copyArtifact(BranchId fromBranch, ArtifactId sourceArtifact);

   ArtifactToken copyArtifact(BranchId fromBranch, ArtifactId sourceArtifact, Collection<AttributeTypeId> attributesToDuplicate);

   ArtifactToken introduceArtifact(BranchId fromBranch, ArtifactId sourceArtifact);

   ArtifactToken replaceWithVersion(ArtifactReadable sourceArtifact, ArtifactReadable destination);

   // ATTRIBUTE

   void setName(ArtifactId art, String value);

   AttributeId createAttribute(ArtifactId art, AttributeTypeId attributeType);

   <T> AttributeId createAttribute(ArtifactId art, AttributeTypeId attributeType, T value);

   AttributeId createAttributeFromString(ArtifactId art, AttributeTypeId attributeType, String value);

   <T> void setSoleAttributeValue(ArtifactId art, AttributeTypeId attributeType, T value);

   void setSoleAttributeFromStream(ArtifactId art, AttributeTypeId attributeType, InputStream stream);

   void setSoleAttributeFromString(ArtifactId art, AttributeTypeId attributeType, String value);

   <T> void setAttributesFromValues(ArtifactId art, AttributeTypeId attributeType, T... values);

   <T> void setAttributesFromValues(ArtifactId art, AttributeTypeId attributeType, Collection<T> values);

   void setAttributesFromStrings(ArtifactId art, AttributeTypeId attributeType, String... values);

   void setAttributesFromStrings(ArtifactId art, AttributeTypeId attributeType, Collection<String> values);

   <T> void setAttributeById(ArtifactId art, AttributeId attrId, T value);

   void setAttributeById(ArtifactId art, AttributeId attrId, String value);

   void setAttributeById(ArtifactId art, AttributeId attrId, InputStream stream);

   void setAttributeApplicability(ArtifactId art, AttributeId attrId, ApplicabilityId applicId);

   void deleteByAttributeId(ArtifactId art, AttributeId attrId);

   void deleteSoleAttribute(ArtifactId art, AttributeTypeId attributeType);

   void deleteAttributes(ArtifactId art, AttributeTypeId attributeType);

   void deleteAttributesWithValue(ArtifactId art, AttributeTypeId attributeType, Object value);

   /// TX

   void addChildren(ArtifactId artA, Iterable<? extends ArtifactId> children);

   void addChildren(ArtifactId artA, ArtifactId... children);

   void relate(ArtifactId artA, IRelationType relType, ArtifactId artB);

   void relate(ArtifactId artA, IRelationType relType, ArtifactId artB, String rationale);

   void relate(ArtifactId artA, IRelationType relType, ArtifactId artB, RelationSorter sortType);

   void relate(ArtifactId artA, IRelationType relType, ArtifactId artB, String rationale, RelationSorter sortType);

   void setRelations(ArtifactId artA, IRelationType relType, Iterable<? extends ArtifactId> artBs);

   void setRationale(ArtifactId artA, IRelationType relType, ArtifactId artB, String rationale);

   void unrelate(ArtifactId artA, IRelationType relType, ArtifactId artB);

   void unrelateFromAll(ArtifactId art);

   void unrelateFromAll(RelationTypeSide typeSide, ArtifactId art);

   void setRelationApplicability(ArtifactId artA, IRelationType relType, ArtifactId artB, ApplicabilityId applicId);

   // Applicability
   void setApplicability(ArtifactId art, ApplicabilityId applicId);

   void setApplicabilityReference(HashMap<ArtifactId, List<ApplicabilityId>> artifacts);

   void setApplicability(ApplicabilityId applicId, List<? extends ArtifactId> artifacts);

   ArtifactId createView(BranchId branch, String viewName);

   void createApplicabilityForView(ArtifactId viewId, String applicability);

   void createDemoApplicability();

   // Tuples
   <E1, E2> GammaId addTuple2(Tuple2Type<E1, E2> tupleType, E1 e1, E2 e2);

   <E1, E2, E3> GammaId addTuple3(Tuple3Type<E1, E2, E3> tupleType, E1 e1, E2 e2, E3 e3);

   <E1, E2, E3, E4> GammaId addTuple4(Tuple4Type<E1, E2, E3, E4> tupleType, E1 e1, E2 e2, E3 e3, E4 e4);

   boolean deleteTuple(Long gammaId);

   <E1, E2> boolean deleteTuple2(Tuple2Type<E1, E2> tupleType, E1 e1, E2 e2);

   <E1, E2, E3> boolean deleteTupple3(Tuple3Type<E1, E2, E3> tupleType, E1 e1, E2 e2, E3 e3);

   <E1, E2, E3, E4> boolean deleteTupple4(Tuple4Type<E1, E2, E3, E4> tupleType, E1 e1, E2 e2, E3 e3, E4 e4);

}
