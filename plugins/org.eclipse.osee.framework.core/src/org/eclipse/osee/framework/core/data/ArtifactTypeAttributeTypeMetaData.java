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
   private final String defaultValue;

   public ArtifactTypeAttributeTypeMetaData(Multiplicity multiplicity, String defaultValue) {
      this.multiplicity = multiplicity;
      this.defaultValue = defaultValue;
   }

   public String getDefaultValue() {
      return defaultValue;
   }

   public Multiplicity getMultiplicity() {
      return multiplicity;
   }
}