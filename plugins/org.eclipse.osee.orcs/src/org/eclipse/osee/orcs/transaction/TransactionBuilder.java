/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.transaction;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchCategoryToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.OrcsTypeJoin;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.Tuple2Type;
import org.eclipse.osee.framework.core.data.Tuple3Type;
import org.eclipse.osee.framework.core.data.Tuple4Type;
import org.eclipse.osee.framework.core.data.TupleTypeId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.jdk.core.type.NamedId;

/**
 * @author Roberto E. Escobar
 */
public interface TransactionBuilder {

   BranchId getBranch();

   String getComment();

   void setComment(String comment);

   UserId getAuthor();

   ArtifactId getCommitArtId();

   void setCommitArtId(ArtifactId commitArt);

   /**
    * @return TransactionRecord or null of no changes made
    */
   TransactionToken commit();

   boolean isCommitInProgress();

   // ARTIFACT

   /**
    * If parent is invalid, the artifact will be created with no parent
    */
   ArtifactToken createArtifact(ArtifactId parent, ArtifactTypeToken artifactType, String name);

   ArtifactToken createArtifact(ArtifactTypeToken artifactType, String name);

   ArtifactToken createArtifact(ArtifactTypeToken artifactType, String name, ApplicabilityId appId);

   ArtifactToken createArtifact(ArtifactTypeToken artifactType, String name, ArtifactId artifactId);

   ArtifactToken createArtifact(ArtifactTypeToken artifactType, String name, ArtifactId artifactId,
      ApplicabilityId appId);

   ArtifactToken createArtifactWithNoName(ArtifactTypeToken artifactType, ArtifactId artifactId, ApplicabilityId appId);

   ArtifactToken createArtifact(ArtifactToken token);

   ArtifactToken createArtifact(ArtifactTypeToken artifactType, String name, String guid);

   ArtifactToken createArtifact(ArtifactTypeToken artifactType, String name, String guid, ApplicabilityId appId);

   /**
    * If parent is invalid, the artifact will be created with no parent
    */
   ArtifactToken createArtifact(ArtifactId parent, ArtifactToken artifact);

   void deleteArtifact(ArtifactId sourceArtifact);

   ArtifactToken copyArtifact(ArtifactReadable sourceArtifact);

   ArtifactToken copyArtifact(ArtifactReadable sourceArtifact, Collection<AttributeTypeToken> attributesToDuplicate);

   ArtifactToken copyArtifact(BranchId fromBranch, ArtifactId sourceArtifact);

   ArtifactToken copyArtifact(BranchId fromBranch, ArtifactId sourceArtifact,
      Collection<AttributeTypeToken> attributesToDuplicate);

   // Fix server implementation of introduce
   ArtifactToken introduceArtifact(BranchId fromBranch, ArtifactId sourceArtifact);

   ArtifactToken replaceWithVersion(ArtifactReadable sourceArtifact, ArtifactReadable destination);

   // ATTRIBUTE

   void setName(ArtifactId art, String value);

   AttributeId createAttribute(ArtifactId art, AttributeTypeToken attributeType);

   <T> AttributeId createAttribute(ArtifactId art, AttributeTypeToken attributeType, T value);

   <T> AttributeId createAttribute(ArtifactId art, AttributeTypeToken attributeType, AttributeId attributeId, T value);

   <T> AttributeId createAttributeFromString(ArtifactId artifact, AttributeTypeToken attributeType, String value,
      AttributeId attributeId);

   <T> AttributeId createAttributeFromString(ArtifactId art, AttributeTypeToken attributeType, String value);

   <T> AttributeId createAttribute(ArtifactId art, AttributeTypeToken attributeType, UserToken userToken, T value);

   <T> void setSoleAttributeValue(ArtifactId art, AttributeTypeToken attributeType, T value);

   void setSoleAttributeFromStream(ArtifactId art, AttributeTypeToken attributeType, InputStream stream);

   void setSoleAttributeFromString(ArtifactId art, AttributeTypeToken attributeType, String value);

   <T> void setAttributesFromValues(ArtifactId art, AttributeTypeToken attributeType, Collection<T> values);

   void setAttributesFromStrings(ArtifactId art, AttributeTypeToken attributeType, String... values);

   void setAttributesFromStrings(ArtifactId art, AttributeTypeToken attributeType, Collection<String> values);

   <T> void setAttributeById(ArtifactId art, AttributeId attrId, T value);

   void setAttributeById(ArtifactId art, AttributeId attrId, String value);

   void setAttributeById(ArtifactId art, AttributeId attrId, InputStream stream);

   void setAttributeApplicability(ArtifactId art, AttributeId attrId, ApplicabilityId applicId);

   <T> void setSoleAttributeValue(ArtifactId art, AttributeTypeToken attributeType, T value, GammaId gammaId);

   void setSoleAttributeFromStream(ArtifactId art, AttributeTypeToken attributeType, InputStream stream,
      GammaId gammaId);

   void setSoleAttributeFromString(ArtifactId art, AttributeTypeToken attributeType, String value, GammaId gammaId);

   <T> void setAttributesFromValues(ArtifactId art, AttributeTypeToken attributeType, Collection<T> values,
      Collection<GammaId> gammaIds);

   void setAttributesFromStrings(ArtifactId art, AttributeTypeToken attributeType, GammaId gammaId, String... values);

   void setAttributesFromStrings(ArtifactId art, AttributeTypeToken attributeType, Collection<String> values,
      Collection<GammaId> gammaIds);

   <T> void setAttributeById(ArtifactId art, AttributeId attrId, T value, GammaId gammaId);

   void setAttributeById(ArtifactId art, AttributeId attrId, String value, GammaId gammaId);

   void setAttributeById(ArtifactId art, AttributeId attrId, InputStream stream, GammaId gammaId);

   void setAttributeApplicability(ArtifactId art, AttributeId attrId, ApplicabilityId applicId, GammaId gammaId);

   void deleteByAttributeId(ArtifactId art, AttributeId attrId);

   void deleteSoleAttribute(ArtifactId art, AttributeTypeToken attributeType);

   void deleteAttributes(ArtifactId art, AttributeTypeToken attributeType);

   void deleteAttributesWithValue(ArtifactId art, AttributeTypeToken attributeType, Object value);

   void deleteByAttributeId(ArtifactId art, AttributeId attrId, GammaId gammaId);

   void deleteSoleAttribute(ArtifactId art, AttributeTypeToken attributeType, GammaId gammaId);

   void deleteAttributes(ArtifactId art, AttributeTypeToken attributeType, GammaId gammaId);

   void deleteAttributesWithValue(ArtifactId art, AttributeTypeToken attributeType, Object value, GammaId gammaId);

   /// TX

   void addChild(ArtifactId parent, ArtifactId child);

   void relate(ArtifactId artA, RelationTypeToken relType, ArtifactId artB);

   void relate(ArtifactId artA, RelationTypeToken relType, ArtifactId artB, String rationale);

   void relate(ArtifactId artA, RelationTypeToken relType, ArtifactId artB, String rationale, RelationId id);

   void relate(ArtifactId artA, RelationTypeToken relType, ArtifactId artB, RelationSorter sortType);

   void relate(ArtifactId artA, RelationTypeToken relType, ArtifactId artB, String rationale, RelationSorter sortType);

   void relate(ArtifactId artA, RelationTypeToken relType, ArtifactId artB, ArtifactId relatedArtifact,
      String insertType, ArtifactId afterArtifact);

   void relate(ArtifactId artA, RelationTypeToken relType, ArtifactId artB, ArtifactId relatedArtifact,
      String afterArtifact);

   void validateRelationsAll();

   void validateRelationsRelTypeArtA(RelationTypeToken relType, ArtifactId artA);

   void setRelations(ArtifactId artA, RelationTypeToken relType, Iterable<? extends ArtifactId> artBs);

   void setRationale(ArtifactId artA, RelationTypeToken relType, ArtifactId artB, String rationale);

   void unrelate(ArtifactId artA, RelationTypeToken relType, ArtifactId artB);

   void unrelateFromAll(ArtifactId art);

   void unrelateFromAll(RelationTypeSide typeSide, ArtifactId art);

   void relate(ArtifactId artA, RelationTypeToken relType, ArtifactId artB, GammaId gammaId);

   void relate(ArtifactId artA, RelationTypeToken relType, ArtifactId artB, String rationale, GammaId gammaId);

   void relate(ArtifactId artA, RelationTypeToken relType, ArtifactId artB, RelationSorter sortType, GammaId gammaId);

   void relate(ArtifactId artA, RelationTypeToken relType, ArtifactId artB, String rationale, RelationSorter sortType,
      GammaId gammaId);

   void relate(ArtifactId artA, RelationTypeToken relType, ArtifactId artB, ArtifactId relatedArtifact,
      String insertType, ArtifactId afterArtifact, GammaId gammaId);

   void relate(ArtifactId artA, RelationTypeToken relType, ArtifactId artB, ArtifactId relatedArtifact,
      String afterArtifact, GammaId gammaId);

   void setRelations(ArtifactId artA, RelationTypeToken relType, Iterable<? extends ArtifactId> artBs, GammaId gammaId);

   void setRationale(ArtifactId artA, RelationTypeToken relType, ArtifactId artB, String rationale, GammaId gammaId);

   void unrelate(ArtifactId artA, RelationTypeToken relType, ArtifactId artB, GammaId gammaId);

   void unrelateFromAll(ArtifactId art, GammaId gammaId);

   void unrelateFromAll(RelationTypeSide typeSide, ArtifactId art, GammaId gammaId);

   void unrelateFromInvalidArtifact(ArtifactId validArt, ArtifactId invalidArt);

   void setRelationApplicability(ArtifactId artA, RelationTypeToken relType, ArtifactId artB, ApplicabilityId applicId);

   void setRelationsAndOrder(ArtifactId artifact, RelationTypeSide relationSide, List<? extends ArtifactId> artifacts);

   void setRelationApplicability(ArtifactId artA, RelationTypeToken relType, ArtifactId artB, ApplicabilityId applicId,
      GammaId gammaId);

   void setRelationsAndOrder(ArtifactId artifact, RelationTypeSide relationSide, List<? extends ArtifactId> artifacts,
      GammaId gammaId);

   // Applicability

   void setApplicability(ArtifactId art, ApplicabilityId applicId);

   void setApplicabilityReference(HashMap<ArtifactId, List<ApplicabilityId>> artifacts);

   void setApplicability(ApplicabilityId applicId, List<? extends ArtifactId> artifacts);

   ArtifactToken createView(BranchId branch, String viewName);

   void createApplicabilityForView(ArtifactId viewId, String applicability);

   //Branch Category
   void createBranchCategory(BranchId branch, BranchCategoryToken category);

   boolean deleteBranchCategory(BranchId branch, BranchCategoryToken category);

   // Tuples
   <E1, E2> GammaId addTuple2(Tuple2Type<E1, E2> tupleType, E1 e1, E2 e2);

   <J extends OrcsTypeJoin<J, T>, T extends NamedId> void addOrcsTypeJoin(J typeJoin);

   <E1, E2, E3> GammaId addTuple3(Tuple3Type<E1, E2, E3> tupleType, E1 e1, E2 e2, E3 e3);

   <E1, E2, E3, E4> GammaId addTuple4(Tuple4Type<E1, E2, E3, E4> tupleType, E1 e1, E2 e2, E3 e3, E4 e4);

   void deleteTuple2(GammaId gammaId);

   void deleteTuple3(GammaId gammaId);

   void deleteTuple4(GammaId gammaId);

   <E1, E2> boolean deleteTuple2(Tuple2Type<E1, E2> tupleType, E1 e1, E2 e2);

   <E1, E2, E3> boolean deleteTuple3(Tuple3Type<E1, E2, E3> tupleType, E1 e1, E2 e2, E3 e3);

   <E1, E2, E3, E4> boolean deleteTuple4(Tuple4Type<E1, E2, E3, E4> tupleType, E1 e1, E2 e2, E3 e3, E4 e4);

   <E1, E2, E3, E4> boolean deleteTuple4ByE1E2(Tuple4Type<E1, E2, E3, E4> tupleType, E1 e1, E2 e2);

   void addKeyValueOps(Long id, String name);

   public List<ArtifactReadable> getTxDataReadables();

   /**
    * @return writable artifact or sentinal
    */
   ArtifactToken getWriteable(ArtifactId artifact);

   List<ArtifactToken> createArtifacts(ArtifactTypeToken artifactType, ArtifactId parent, List<String> names);

   void introduceTuple(TupleTypeId tupleType, GammaId tupleGamma);

   <T> AttributeId createAttributeNoAccess(ArtifactId artifact, AttributeTypeToken attributeType, T value);

   public List<GammaId> getGammaIdsFailed();

   void deleteByAttributeIdIfExists(ArtifactId artifact, AttributeId attrId);

   void createOrIntroduceArtifact(ArtifactId parent, ArtifactToken sourceArtifact);

}