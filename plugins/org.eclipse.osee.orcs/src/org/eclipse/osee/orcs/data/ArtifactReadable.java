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
package org.eclipse.osee.orcs.data;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Megumi Telles
 * @author Roberto E. Escobar
 * @author Andrew M. Finkbeiner
 */
public interface ArtifactReadable extends ArtifactToken, HasTransaction, OrcsReadable {
   ArtifactTypeToken getArtifactType();

   TransactionId getLastModifiedTransaction();

   boolean isOfType(ArtifactTypeId... otherTypes);

   ArtifactReadable SENTINEL = createSentinel();
   ////////////////////

   int getAttributeCount(AttributeTypeId type);

   int getAttributeCount(AttributeTypeId type, DeletionFlag deletionFlag);

   boolean isAttributeTypeValid(AttributeTypeId attributeType);

   Collection<AttributeTypeToken> getValidAttributeTypes();

   Collection<AttributeTypeToken> getExistingAttributeTypes();

   <T> T getSoleAttributeValue(AttributeTypeId attributeType);

   <T> T getSoleAttributeValue(AttributeTypeId attributeType, DeletionFlag flag, T defaultValue);

   <T> T getSoleAttributeValue(AttributeTypeId attributeType, T defaultValue);

   String getSoleAttributeAsString(AttributeTypeId attributeType);

   String getSoleAttributeAsString(AttributeTypeId attributeType, String defaultValue);

   Long getSoleAttributeId(AttributeTypeId attributeType);

   <T> List<T> getAttributeValues(AttributeTypeId attributeType);

   Iterable<Collection<? extends AttributeReadable<Object>>> getAttributeIterable();

   ////////////////////

   AttributeReadable<Object> getAttributeById(AttributeId attributeId);

   ResultSet<? extends AttributeReadable<Object>> getAttributes();

   <T> ResultSet<? extends AttributeReadable<T>> getAttributes(AttributeTypeId attributeType);

   ResultSet<? extends AttributeReadable<Object>> getAttributes(DeletionFlag deletionFlag);

   public <T> ResultSet<? extends AttributeReadable<T>> getAttributes(AttributeTypeId attributeType, DeletionFlag deletionFlag);

   String getAttributeValuesAsString(AttributeTypeId attributeType);

   ////////////////////
   int getMaximumRelationAllowed(RelationTypeSide relationTypeSide);

   Collection<RelationTypeId> getValidRelationTypes();

   Collection<RelationTypeId> getExistingRelationTypes();

   ArtifactReadable getParent();

   List<ArtifactReadable> getDescendants();

   void getDescendants(List<ArtifactReadable> descendants);

   List<ArtifactReadable> getAncestors();

   ResultSet<ArtifactReadable> getChildren();

   ResultSet<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide);

   List<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide, ArtifactTypeId artifactType);

   ResultSet<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide, DeletionFlag deletionFlag);

   boolean areRelated(RelationTypeSide typeAndSide, ArtifactReadable readable);

   int getRelatedCount(RelationTypeSide typeAndSide);

   String getRationale(RelationTypeSide typeAndSide, ArtifactReadable readable);

   ResultSet<RelationReadable> getRelations(RelationTypeSide relationTypeSide);

   Collection<Long> getChildrentIds();

   Collection<Long> getRelatedIds(RelationTypeSide relationTypeSide);

   boolean isHistorical();

   ApplicabilityId getApplicability();

   public static ArtifactReadable createSentinel() {
      final class ArtifactReadableSentinel extends NamedIdBase implements ArtifactReadable {

         public ArtifactReadableSentinel() {
            super(Id.SENTINEL, "SENTINEL");

         }

         @Override
         public BranchId getBranch() {
            return null;
         }

         @Override
         public TransactionId getTransaction() {
            return null;
         }

         @Override
         public ModificationType getModificationType() {
            return null;
         }

         @Override
         public ArtifactTypeToken getArtifactType() {
            return null;
         }

         @Override
         public TransactionId getLastModifiedTransaction() {
            return null;
         }

         @Override
         public boolean isOfType(ArtifactTypeId... otherTypes) {
            return false;
         }

         @Override
         public int getAttributeCount(AttributeTypeId type) {
            return 0;
         }

         @Override
         public int getAttributeCount(AttributeTypeId type, DeletionFlag deletionFlag) {
            return 0;
         }

         @Override
         public boolean isAttributeTypeValid(AttributeTypeId attributeType) {
            return false;
         }

         @Override
         public Collection<AttributeTypeToken> getValidAttributeTypes() {
            return null;
         }

         @Override
         public Collection<AttributeTypeToken> getExistingAttributeTypes() {
            return null;
         }

         @Override
         public <T> T getSoleAttributeValue(AttributeTypeId attributeType) {
            return null;
         }

         @Override
         public <T> T getSoleAttributeValue(AttributeTypeId attributeType, DeletionFlag flag, T defaultValue) {
            return null;
         }

         @Override
         public <T> T getSoleAttributeValue(AttributeTypeId attributeType, T defaultValue) {
            return null;
         }

         @Override
         public String getSoleAttributeAsString(AttributeTypeId attributeType) {
            return null;
         }

         @Override
         public String getSoleAttributeAsString(AttributeTypeId attributeType, String defaultValue) {
            return null;
         }

         @Override
         public Long getSoleAttributeId(AttributeTypeId attributeType) {
            return null;
         }

         @Override
         public <T> List<T> getAttributeValues(AttributeTypeId attributeType) {
            return null;
         }

         @Override
         public Iterable<Collection<? extends AttributeReadable<Object>>> getAttributeIterable() {
            return null;
         }

         @Override
         public AttributeReadable<Object> getAttributeById(AttributeId attributeId) {
            return null;
         }

         @Override
         public ResultSet<? extends AttributeReadable<Object>> getAttributes() {
            return null;
         }

         @Override
         public <T> ResultSet<? extends AttributeReadable<T>> getAttributes(AttributeTypeId attributeType) {
            return null;
         }

         @Override
         public ResultSet<? extends AttributeReadable<Object>> getAttributes(DeletionFlag deletionFlag) {
            return null;
         }

         @Override
         public <T> ResultSet<? extends AttributeReadable<T>> getAttributes(AttributeTypeId attributeType, DeletionFlag deletionFlag) {
            return null;
         }

         @Override
         public String getAttributeValuesAsString(AttributeTypeId attributeType) {
            return null;
         }

         @Override
         public int getMaximumRelationAllowed(RelationTypeSide relationTypeSide) {
            return 0;
         }

         @Override
         public Collection<RelationTypeId> getValidRelationTypes() {
            return null;
         }

         @Override
         public Collection<RelationTypeId> getExistingRelationTypes() {
            return null;
         }

         @Override
         public ArtifactReadable getParent() {
            return null;
         }

         @Override
         public List<ArtifactReadable> getDescendants() {
            return null;
         }

         @Override
         public void getDescendants(List<ArtifactReadable> descendants) {
         }

         @Override
         public List<ArtifactReadable> getAncestors() {
            return null;
         }

         @Override
         public ResultSet<ArtifactReadable> getChildren() {
            return null;
         }

         @Override
         public ResultSet<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide) {
            return null;
         }

         @Override
         public List<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide, ArtifactTypeId artifactType) {
            return null;
         }

         @Override
         public ResultSet<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide, DeletionFlag deletionFlag) {
            return null;
         }

         @Override
         public boolean areRelated(RelationTypeSide typeAndSide, ArtifactReadable readable) {
            return false;
         }

         @Override
         public int getRelatedCount(RelationTypeSide typeAndSide) {
            return 0;
         }

         @Override
         public String getRationale(RelationTypeSide typeAndSide, ArtifactReadable readable) {
            return null;
         }

         @Override
         public ResultSet<RelationReadable> getRelations(RelationTypeSide relationTypeSide) {
            return null;
         }

         @Override
         public Collection<Long> getChildrentIds() {
            return null;
         }

         @Override
         public Collection<Long> getRelatedIds(RelationTypeSide relationTypeSide) {
            return null;
         }

         @Override
         public boolean isHistorical() {
            return false;
         }

         @Override
         public ApplicabilityId getApplicability() {
            return null;
         }

      }
      return new ArtifactReadableSentinel();
   }

}