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
package org.eclipse.osee.orcs.core.internal.attribute;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.HasLocalId;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface AttributeContainer extends Identifiable<String>, HasLocalId<Integer> {

   void add(AttributeTypeId attributeType, Attribute<? extends Object> attribute);

   void remove(IAttributeType type, Attribute<? extends Object> attribute);

   boolean isLoaded();

   void setLoaded(boolean value) throws OseeCoreException;

   String getExceptionString();

   @Override
   String toString();

   /////////////////////////////////////////////////////////////////

   boolean areAttributesDirty();

   int getMaximumAttributeTypeAllowed(IAttributeType attributeType) throws OseeCoreException;

   int getMinimumAttributeTypeAllowed(IAttributeType attributeType) throws OseeCoreException;

   /////////////////////////////////////////////////////////////////

   int getAttributeCount(AttributeTypeId type) throws OseeCoreException;

   int getAttributeCount(IAttributeType type, DeletionFlag deletionFlag) throws OseeCoreException;

   boolean isAttributeTypeValid(AttributeTypeId attributeType) throws OseeCoreException;

   Collection<? extends IAttributeType> getValidAttributeTypes() throws OseeCoreException;

   Collection<AttributeTypeToken> getExistingAttributeTypes() throws OseeCoreException;
}
