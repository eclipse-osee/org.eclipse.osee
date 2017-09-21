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

import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.Attribute;

/**
 * @author Roberto E. Escobar
 */
public interface AttributeSetHelper<A, V> {

   boolean matches(Attribute<A> attribute, V value) throws OseeCoreException;

   void setAttributeValue(Attribute<A> attribute, V value) throws OseeCoreException;

   void createAttribute(AttributeTypeId attributeType, V value) throws OseeCoreException;
}