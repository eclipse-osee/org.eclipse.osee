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

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.Identifiable;
import org.eclipse.osee.framework.core.data.Readable;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 * @author Andrew M. Finkbeiner
 */
public interface ArtifactReadable extends Readable, Identifiable, HasLocalId {

   IOseeBranch getBranch();

   String getHumanReadableId();

   int getTransactionId();

   IArtifactType getArtifactType();

   boolean isOfType(IArtifactType... otherTypes);

   int getAttributeCount(IAttributeType type) throws OseeCoreException;

   boolean isAttributeTypeValid(IAttributeType attributeType) throws OseeCoreException;

   Collection<? extends IAttributeType> getExistingAttributeTypes() throws OseeCoreException;

   List<AttributeReadable<Object>> getAttributes() throws OseeCoreException;

   <T> List<AttributeReadable<T>> getAttributes(IAttributeType attributeType) throws OseeCoreException;

   <T> T getSoleAttributeValue(IAttributeType attributeType) throws OseeCoreException;

   String getSoleAttributeAsString(IAttributeType attributeType) throws OseeCoreException;

   String getSoleAttributeAsString(IAttributeType attributeType, String defaultValue) throws OseeCoreException;

   <T> List<T> getAttributeValues(IAttributeType attributeType) throws OseeCoreException;

}