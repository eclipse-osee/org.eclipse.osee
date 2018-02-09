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
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.RelationTypeId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Megumi Telles
 * @author Roberto E. Escobar
 * @author Andrew M. Finkbeiner
 */
public interface ArtifactReadable extends ArtifactToken, HasTransaction, OrcsReadable {
   IArtifactType getArtifactType();

   TransactionId getLastModifiedTransaction();

   boolean isOfType(ArtifactTypeId... otherTypes);

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
}