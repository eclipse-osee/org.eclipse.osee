/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.util;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.skynet.core.utility.JsonArtifactRepresentation;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link JsonArtifactRepresentation}.
 *
 * @author David W. Miller
 */
public class JsonArtifactTransferTest {
   public static class JsonArtRep {
      ArtifactTypeId artType;
      List<JsonAttrRep> attrs;

      public JsonArtRep() {

      }

      public ArtifactTypeId getArtType() {
         return artType;
      }

      public void setArtType(ArtifactTypeId artType) {
         this.artType = artType;
      }

      public List<JsonAttrRep> getAttrs() {
         return attrs;
      }

      public void setAttrs(List<JsonAttrRep> attrs) {
         this.attrs = attrs;
      }
   }
   public static class JsonAttrRep {
      AttributeTypeId type;
      String value;

      public JsonAttrRep() {

      }

      public AttributeTypeId getType() {
         return type;
      }

      public void setType(AttributeTypeId type) {
         this.type = type;
      }

      public String getValue() {
         return value;
      }

      public void setValue(String value) {
         this.value = value;
      }
   }

   @Test
   public void testConstruction() throws IOException {
      String output = generateJsonOutput();
      parseJsonOutput(output);
   }

   private String generateJsonOutput() {
      List<JsonArtRep> outputItems = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
         JsonArtRep rep = new JsonArtRep();
         rep.setArtType(CoreArtifactTypes.SoftwareDesignMsWord);
         List<JsonAttrRep> attrs = new ArrayList<>();
         for (int j = 0; j < 5; j++) {
            JsonAttrRep attr = new JsonAttrRep();
            attr.setType(CoreAttributeTypes.Active);
            attr.setValue(String.format("test %s", j));
            attrs.add(attr);
         }
         rep.setAttrs(attrs);
         outputItems.add(rep);
      }
      return JsonUtil.toJson(outputItems);
   }

   private void parseJsonOutput(String output) throws IOException {
      List<JsonArtRep> reqts = JsonUtil.getMapper().readValue(output, new TypeReference<List<JsonArtRep>>() { //
      });
      Assert.assertTrue(reqts.size() == 5);
   }
}