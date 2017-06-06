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

import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 * @author Andrew M. Finkbeiner
 */
public interface AttributeReadable<T> extends OrcsReadable, IAttribute<T> {

   long getGammaId();

   @Override
   ModificationType getModificationType();

   @Override
   AttributeTypeToken getAttributeType() throws OseeCoreException;

   boolean isOfType(AttributeTypeId otherAttributeType) throws OseeCoreException;

   @Override
   T getValue() throws OseeCoreException;

   String getDisplayableString() throws OseeCoreException;

   @Override
   String toString();

}