/*********************************************************************
 * Copyright (c) 2017 Boeing
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
 * @author David W. Miller
 */
public class JsonAttribute {
   private String typeName;
   private AttributeTypeId typeId;
   private String value;

   public JsonAttribute(AttributeTypeId typeId, String value) {
      this.setTypeId(typeId);
      this.value = value;
   }

   public JsonAttribute() {
      this(AttributeTypeId.SENTINEL, null);
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public String getTypeName() {
      return typeName;
   }

   public void setTypeName(String typeName) {
      this.typeName = typeName;
   }

   public AttributeTypeId getTypeId() {
      return typeId;
   }

   public void setTypeId(AttributeTypeId typeId) {
      this.typeId = typeId;
   }

}