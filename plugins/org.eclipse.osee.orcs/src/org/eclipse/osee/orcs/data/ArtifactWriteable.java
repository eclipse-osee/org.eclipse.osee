/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.data;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.Writeable;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 * @author Andrew M. Finkbeiner
 */
public interface ArtifactWriteable extends Writeable, ArtifactReadable {

   void setArtifactType(IArtifactType artifactType) throws OseeCoreException;

   // Attributes

   <T> List<AttributeWriteable<T>> getWriteableAttributes() throws OseeCoreException;

   <T> List<AttributeWriteable<T>> getWriteableAttributes(IAttributeType attributeType) throws OseeCoreException;

   void setName(String name) throws OseeCoreException;

   void createAttribute(IAttributeType attributeType) throws OseeCoreException;

   <T> void createAttribute(IAttributeType attributeType, T value) throws OseeCoreException;

   void createAttributeFromString(IAttributeType attributeType, String value) throws OseeCoreException;

   <T> void setSoleAttribute(IAttributeType attributeType, T value) throws OseeCoreException;

   void setSoleAttributeFromStream(IAttributeType attributeType, InputStream inputStream) throws OseeCoreException;

   void setSoleAttributeFromString(IAttributeType attributeType, String value) throws OseeCoreException;

   void setAttributes(IAttributeType attributeType, Collection<String> values) throws OseeCoreException;

   void deleteSoleAttribute(IAttributeType attributeType) throws OseeCoreException;

   void deleteAttributes(IAttributeType attributeType) throws OseeCoreException;

   void deleteAttributesWithValue(IAttributeType attributeType, Object value) throws OseeCoreException;

}