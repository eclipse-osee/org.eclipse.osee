/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.framework.core.data;

/**
 * @author Ryan D. Brooks
 */
public final class ArtifactTypeAttributeTypeMetaData<T> {
   private final Multiplicity multiplicity;
   private final T defaultValue;
   private final String[] validEnumValues;

   public ArtifactTypeAttributeTypeMetaData(Multiplicity multiplicity, T defaultValue) {
      this(multiplicity, defaultValue, new String[0]);
   }

   public ArtifactTypeAttributeTypeMetaData(Multiplicity multiplicity, T defaultValue, String[] enumValues) {
      this.multiplicity = multiplicity;
      this.defaultValue = defaultValue;
      this.validEnumValues = enumValues;
   }

   public String[] getValidEnumValues() {
      return validEnumValues;
   }

   public T getDefaultValue() {
      return defaultValue;
   }

   public Multiplicity getMultiplicity() {
      return multiplicity;
   }
}