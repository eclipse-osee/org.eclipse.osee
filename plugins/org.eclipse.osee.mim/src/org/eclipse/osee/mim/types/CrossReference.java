/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.mim.types;

import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

public class CrossReference extends PLGenericDBObject {

   public static final CrossReference SENTINEL = new CrossReference();

   private String crossReferenceValue;
   private String crossReferenceArrayValues;

   public CrossReference(ArtifactToken art) {
      super(art);
   }

   public CrossReference(ArtifactReadable art) {
      super(art);
      if (art.isValid()) {
         this.setCrossReferenceValue(art.getSoleAttributeAsString(CoreAttributeTypes.CrossReferenceValue, ""));
         this.setCrossReferenceArrayValues(
            art.getSoleAttributeAsString(CoreAttributeTypes.CrossReferenceArrayValues, ""));
      } else {
         this.setCrossReferenceValue("");
         this.setCrossReferenceArrayValues("");
      }
   }

   public CrossReference(Long id, String name) {
      super(id, name);
   }

   public CrossReference() {
   }

   public String getCrossReferenceValue() {
      return crossReferenceValue;
   }

   public void setCrossReferenceValue(String crossReferenceValue) {
      this.crossReferenceValue = crossReferenceValue;
   }

   public String getCrossReferenceArrayValues() {
      return crossReferenceArrayValues;
   }

   public void setCrossReferenceArrayValues(String crossReferenceArrayValues) {
      this.crossReferenceArrayValues = crossReferenceArrayValues;
   }

}
