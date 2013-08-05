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
package org.eclipse.osee.orcs.core.internal.attribute;

import java.util.List;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.Identifiable;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.data.AttributesWriteable;
import org.eclipse.osee.orcs.data.HasLocalId;

/**
 * @author Roberto E. Escobar
 */
public interface AttributeManager extends Identifiable, AttributesWriteable, HasLocalId {

   void add(IAttributeType type, Attribute<? extends Object> attribute);

   void remove(IAttributeType type, Attribute<? extends Object> attribute);

   boolean isLoaded();

   void setLoaded(boolean value) throws OseeCoreException;

   String getExceptionString();

   @Override
   String toString();

   /////////////////////////////////////////////////////////////////

   boolean areAttributesDirty();

   void setAttributesNotDirty();

   int getMaximumAttributeTypeAllowed(IAttributeType attributeType) throws OseeCoreException;

   int getMinimumAttributeTypeAllowed(IAttributeType attributeType) throws OseeCoreException;

   List<Attribute<Object>> getAttributesDirty() throws OseeCoreException;

   void deleteAttributesByArtifact() throws OseeCoreException;

   void unDeleteAttributesByArtifact() throws OseeCoreException;
}
