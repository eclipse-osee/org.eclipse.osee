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

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactReferenceAttribute extends IdentityReferenceAttribute {

   public static final String SENTINEL = "-1";

   @Override
   public ArtifactId convertStringToValue(String value) {
      return ArtifactId.valueOf(value);
   }

   @Override
   public Id getValue() {
      return (Id) getAttributeDataProvider().getValue();
   }

   @Override
   protected void setToDefaultValue() {
      String defaultValue = getAttributeType().getDefaultValue();
      if (defaultValue == null) {
         defaultValue = SENTINEL;
      }
      setFromStringNoDirty(defaultValue);
   }

   @Override
   public String toString() {
      if (getValue() == null) {
         return this.getAttributeType().toString();
      }
      return getValue().getIdString();
   }

}