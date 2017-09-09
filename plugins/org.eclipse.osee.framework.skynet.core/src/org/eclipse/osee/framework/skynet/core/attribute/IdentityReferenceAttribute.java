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

import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;

public abstract class IdentityReferenceAttribute extends CharacterBackedAttribute<Id> {
   @Override
   public Id getValue() throws OseeCoreException {
      return ServiceUtil.getAttributeAdapterService().adapt(this, (Id) getAttributeDataProvider().getValue());
   }

   @Override
   public String convertToStorageString(Id rawValue) {
      return rawValue.getIdString();
   }
}