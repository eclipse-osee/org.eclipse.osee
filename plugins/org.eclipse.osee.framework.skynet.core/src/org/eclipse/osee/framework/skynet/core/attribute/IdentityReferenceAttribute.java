/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.framework.skynet.core.attribute;

import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Roberto E. Escobar
 */
public abstract class IdentityReferenceAttribute extends CharacterBackedAttribute<Id> {
   @Override
   public Id getValue() {
      return (Id) getAttributeDataProvider().getValue();
   }

   @Override
   public String convertToStorageString(Id rawValue) {
      return rawValue.getIdString();
   }
}
