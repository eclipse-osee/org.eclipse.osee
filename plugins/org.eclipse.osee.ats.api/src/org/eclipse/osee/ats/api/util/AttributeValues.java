/*******************************************************************************
 * Copyright (c) 2022 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Donald G. Dunne
 */
public class AttributeValues {

   List<AttributeValue> attributes = new ArrayList<>();

   public AttributeValues() {
      // for jax-rs
   }

   public List<AttributeValue> getAttributes() {
      return attributes;
   }

   public void setAttributes(List<AttributeValue> attributes) {
      this.attributes = attributes;
   }

   public boolean isNotEmpty() {
      return !attributes.isEmpty();
   }

   @JsonIgnore
   public void addAttrValue(AttributeTypeToken attrType, String... values) {
      attributes.add(new AttributeValue(attrType, values));
   }

   @JsonIgnore
   public void addAttrValue(AttributeTypeToken attrType, boolean notExists) {
      attributes.add(new AttributeValue(attrType, true));
   }

}
