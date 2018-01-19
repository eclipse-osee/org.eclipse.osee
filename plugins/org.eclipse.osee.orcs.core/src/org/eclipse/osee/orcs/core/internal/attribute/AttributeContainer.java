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
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.orcs.core.ds.Attribute;

/**
 * @author Roberto E. Escobar
 */
public interface AttributeContainer extends Identifiable<String> {

   void add(AttributeTypeId attributeType, Attribute<?> attribute);

   void remove(AttributeTypeId type, Attribute<?> attribute);

   boolean isLoaded();

   void setLoaded(boolean value);

   String getExceptionString();

   @Override
   String toString();

   /////////////////////////////////////////////////////////////////

   boolean areAttributesDirty();

   int getMaximumAttributeTypeAllowed(AttributeTypeId attributeType);

   int getMinimumAttributeTypeAllowed(AttributeTypeId attributeType);

   /////////////////////////////////////////////////////////////////

   int getAttributeCount(AttributeTypeId type);

   int getAttributeCount(AttributeTypeId type, DeletionFlag deletionFlag);

   boolean isAttributeTypeValid(AttributeTypeId attributeType);

   Collection<AttributeTypeToken> getValidAttributeTypes();

   Collection<AttributeTypeToken> getExistingAttributeTypes();
}
