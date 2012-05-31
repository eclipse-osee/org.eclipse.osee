/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.presenter.mocks;

import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.orcs.data.AttributeReadable;

/**
 * @author John R. Misinco
 */
public class MockAttribute implements AttributeReadable<String> {

   private final IAttributeType attrType;
   private final String value;

   public MockAttribute(IAttributeType attrType, String value) {
      this.attrType = attrType;
      this.value = value;
   }

   @Override
   public long getGammaId() {
      return 0;
   }

   @Override
   public ModificationType getModificationType() {
      return null;
   }

   @Override
   public int getId() {
      return 0;
   }

   @Override
   public IAttributeType getAttributeType() {
      return attrType;
   }

   @Override
   public boolean isOfType(IAttributeType otherAttributeType) {
      return attrType.matches(otherAttributeType);
   }

   @Override
   public String getValue() {
      return value;
   }

   @Override
   public String getDisplayableString() {
      return value;
   }

   @Override
   public boolean isDeleted() {
      return false;
   }
}