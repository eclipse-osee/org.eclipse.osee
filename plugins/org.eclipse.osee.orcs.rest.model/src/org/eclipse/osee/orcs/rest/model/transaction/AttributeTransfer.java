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
package org.eclipse.osee.orcs.rest.model.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class AttributeTransfer {
   @JsonProperty("typeId")
   private String typeId;

   @JsonProperty("value")
   private List<String> value;

   @JsonProperty("typeId")
   public String getTypeId() {
      return typeId;
   }

   @JsonProperty("typeId")
   public void setTypeId(String typeId) {
      this.typeId = typeId;
   }

   @JsonProperty("value")
   public List<String> getValue() {
      return value;
   }

   @JsonProperty("value")
   public void setValue(List<String> value) {
      this.value = value;
   }

}
