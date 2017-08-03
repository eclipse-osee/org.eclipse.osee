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
import org.eclipse.osee.framework.core.data.RelationTypeId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Megumi Telles
 * @author Roberto E. Escobar
 * @author Andrew M. Finkbeiner
 */
public interface ArtifactReadable extends ArtifactToken, HasTransaction, OrcsReadable {

   TransactionId getLastModifiedTransaction();

   boolean isOfType(ArtifactTypeId... otherTypes) throws OseeCoreException;

   ////////////////////

   int getAttributeCount(AttributeTypeId type) throws OseeCoreException;

   int getAttributeCount(AttributeTypeId type, DeletionFlag deletionFlag) throws OseeCoreException;

   boolean isAttributeTypeValid(AttributeTypeId attributeType) throws OseeCoreException;

   Collection<AttributeTypeToken> getValidAttributeTypes() throws OseeCoreException;

   Collection<AttributeTypeToken> getExistingAttributeTypes() throws OseeCoreException;

   <T> T getSoleAttributeValue(AttributeTypeId attributeType);

   <T> T getSoleAttributeValue(AttributeTypeId attributeType, DeletionFlag flag, T defaultValue);

   <T> T getSoleAttributeValue(AttributeTypeId attributeType, T defaultValue);

   String getSoleAttributeAsString(AttributeTypeId attributeType) throws OseeCoreException;

   String getSoleAttributeAsString(AttributeTypeId attributeType, String defaultValue) throws OseeCoreException;

   Long getSoleAttributeId(AttributeTypeId attributeType);

   <T> List<T> getAttributeValues(AttributeTypeId attributeType) throws OseeCoreException;

   ////////////////////

   AttributeReadable<Object> getAttributeById(AttributeId attributeId) throws OseeCoreException;

   ResultSet<? extends AttributeReadable<Object>> getAttributes() throws OseeCoreException;

   <T> ResultSet<? extends AttributeReadable<T>> getAttributes(AttributeTypeId attributeType) throws OseeCoreException;

   ResultSet<? extends AttributeReadable<Object>> getAttributes(DeletionFlag deletionFlag) throws OseeCoreException;

   String getAttributeValuesAsString(AttributeTypeId attributeType);

   <T> ResultSet<? extends AttributeReadable<T>> getAttributes(AttributeTypeId attributeType, DeletionFlag deletionFlag) throws OseeCoreException;

   ////////////////////
   int getMaximumRelationAllowed(RelationTypeSide relationTypeSide) throws OseeCoreException;

   Collection<RelationTypeId> getValidRelationTypes() throws OseeCoreException;

   Collection<RelationTypeId> getExistingRelationTypes() throws OseeCoreException;

   ArtifactReadable getParent() throws OseeCoreException;

   List<ArtifactReadable> getDescendants() throws OseeCoreException;

   void getDescendants(List<ArtifactReadable> descendants) throws OseeCoreException;

   List<ArtifactReadable> getAncestors() throws OseeCoreException;

   ResultSet<ArtifactReadable> getChildren() throws OseeCoreException;

   ResultSet<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide) throws OseeCoreException;

   List<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide, ArtifactTypeId artifactType);

   ResultSet<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide, DeletionFlag deletionFlag) throws OseeCoreException;

   boolean areRelated(RelationTypeSide typeAndSide, ArtifactReadable readable) throws OseeCoreException;

   int getRelatedCount(RelationTypeSide typeAndSide) throws OseeCoreException;

   String getRationale(RelationTypeSide typeAndSide, ArtifactReadable readable) throws OseeCoreException;

   ResultSet<RelationReadable> getRelations(RelationTypeSide relationTypeSide);

   Collection<Long> getChildrentIds();

   Collection<Long> getRelatedIds(RelationTypeSide relationTypeSide);

   boolean isHistorical();

   ApplicabilityId getApplicability();
}