/*********************************************************************
 * Copyright (c) 2013 Boeing
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

   <T> void add(AttributeTypeToken attributeType, Attribute<T> attribute);

   <T> void remove(AttributeTypeToken type, Attribute<T> attribute);

   boolean isLoaded();

   void setLoaded(boolean value);

   String getExceptionString();

   boolean areAttributesDirty();

   int getAttributeCount(AttributeTypeToken type);

   int getAttributeCount(AttributeTypeToken type, DeletionFlag deletionFlag);

   Collection<AttributeTypeToken> getValidAttributeTypes();

   Collection<AttributeTypeToken> getExistingAttributeTypes();
}