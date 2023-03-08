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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.applicability.NameValuePair;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;

public class CrossReference extends PLGenericDBObject {

   public static final CrossReference SENTINEL = new CrossReference();

   private String crossReferenceValue;
   private String crossReferenceArrayValues;
   private String crossReferenceAdditionalContent;

   public CrossReference(ArtifactToken art) {
      super(art);
   }

   public CrossReference(ArtifactReadable art) {
      super(art);
      if (art.isValid()) {
         this.setCrossReferenceValue(art.getSoleAttributeAsString(CoreAttributeTypes.CrossReferenceValue, ""));
         this.setCrossReferenceArrayValues(
            art.getSoleAttributeAsString(CoreAttributeTypes.CrossReferenceArrayValues, ""));
         this.setCrossReferenceAdditionalContent(
            art.getSoleAttributeAsString(CoreAttributeTypes.CrossReferenceAdditionalContent, ""));
      } else {
         this.setCrossReferenceValue("");
         this.setCrossReferenceArrayValues("");
         this.setCrossReferenceAdditionalContent("");
      }
   }

   public CrossReference(Long id, String name, String value, String arrayValues, String additionalContent) {
      super(id, name);
      this.setCrossReferenceValue(value);
      this.setCrossReferenceArrayValues(arrayValues);
      this.setCrossReferenceAdditionalContent(additionalContent);
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

   public String getCrossReferenceAdditionalContent() {
      return crossReferenceAdditionalContent;
   }

   public void setCrossReferenceAdditionalContent(String crossReferenceAdditionalContent) {
      this.crossReferenceAdditionalContent = crossReferenceAdditionalContent;
   }

   @JsonIgnore
   public List<NameValuePair> getNameValuePairs() {
      return Arrays.asList(getCrossReferenceArrayValues().split(";")).stream().map(arv -> {
         if (Strings.isValid(arv)) {
            return new NameValuePair(arv.split("=")[1], arv.split("=")[0]);
         }
         return null;
      }).collect(Collectors.toList());
   }

}
