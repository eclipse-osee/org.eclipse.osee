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
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.Identifiable;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.orcs.data.ArtifactId;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeId;

/**
 * @author Roberto E. Escobar
 */
public interface TransactionBuilder {

   IOseeBranch getBranch();

   Identifiable getAuthor();

   String getComment();

   void setComment(String comment) throws OseeCoreException;

   TransactionRecord commit() throws OseeCoreException;

   boolean isCommitInProgress();

   // ARTIFACT

   ArtifactId createArtifact(IArtifactType artifactType, String name) throws OseeCoreException;

   ArtifactId createArtifact(IArtifactType artifactType, String name, String guid) throws OseeCoreException;

   void deleteArtifact(ArtifactId sourceArtifact) throws OseeCoreException;

   ArtifactId copyArtifact(ArtifactReadable sourceArtifact) throws OseeCoreException;

   ArtifactId copyArtifact(ArtifactReadable sourceArtifact, Collection<? extends IAttributeType> attributesToDuplicate) throws OseeCoreException;

   ArtifactId copyArtifact(IOseeBranch fromBranch, ArtifactId sourceArtifact) throws OseeCoreException;

   ArtifactId copyArtifact(IOseeBranch fromBranch, ArtifactId sourceArtifact, Collection<? extends IAttributeType> attributesToDuplicate) throws OseeCoreException;

   ArtifactId introduceArtifact(ArtifactReadable sourceArtifact) throws OseeCoreException;

   ArtifactId introduceArtifact(IOseeBranch fromBranch, ArtifactId artifactId) throws OseeCoreException;

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

}
