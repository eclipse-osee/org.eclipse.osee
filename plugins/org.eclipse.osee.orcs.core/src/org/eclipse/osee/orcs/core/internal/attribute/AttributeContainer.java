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
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.orcs.core.ds.Attribute;

/**
 * @author Roberto E. Escobar
 */
public interface AttributeContainer extends ArtifactToken {

   void add(AttributeTypeToken attributeType, Attribute<?> attribute);

   void remove(AttributeTypeToken type, Attribute<?> attribute);

   boolean isLoaded();

   void setLoaded(boolean value);

   String getExceptionString();

   boolean areAttributesDirty();

   int getAttributeCount(AttributeTypeToken type);

   int getAttributeCount(AttributeTypeToken type, DeletionFlag deletionFlag);

   boolean isAttributeTypeValid(AttributeTypeToken attributeType);

   Collection<AttributeTypeToken> getValidAttributeTypes();

   Collection<AttributeTypeToken> getExistingAttributeTypes();
}