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
package org.eclipse.osee.framework.skynet.core.attribute;

import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;

public abstract class IdentityReferenceAttribute<T extends Id> extends CharacterBackedAttribute<T> {

   @Override
   public T getValue() throws OseeCoreException {
      return convertStringToValue(getAttributeDataProvider().getValueAsString());
   }

   @Override
   public T convertStringToValue(String value) {
      return ServiceUtil.getAttributeAdapterService().adapt(this, new BaseId(Long.valueOf(value)));
   }

   @Override
   protected boolean subClassSetValue(Id value) {
      return getAttributeDataProvider().setValue(value.getIdString());
   }
}