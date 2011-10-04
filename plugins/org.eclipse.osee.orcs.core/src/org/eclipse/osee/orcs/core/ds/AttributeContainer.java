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
package org.eclipse.osee.orcs.core.ds;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.internal.attribute.Attribute;
import org.eclipse.osee.orcs.data.ReadableAttribute;

/**
 * @author Roberto E. Escobar
 */
public interface AttributeContainer {

   void add(IAttributeType type, Attribute<?> attribute);

   int getCount(IAttributeType type) throws OseeCoreException;

   Collection<IAttributeType> getAttributeTypes() throws OseeCoreException;

   <T> List<ReadableAttribute<T>> getAttributes(IAttributeType attributeType) throws OseeCoreException;

   <T> ReadableAttribute<T> getSoleAttribute(IAttributeType attributeType) throws OseeCoreException;

   //TODO is this needed
   boolean isLoaded();

   void setLoaded(boolean value);
}
