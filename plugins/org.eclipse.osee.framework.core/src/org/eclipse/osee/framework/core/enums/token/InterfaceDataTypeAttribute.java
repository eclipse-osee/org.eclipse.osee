/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.framework.core.enums.token;

import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.token.InterfaceDataTypeAttribute.InterfaceDataTypeEnum;

/**
 * @author Audrey Denk
 */
public class InterfaceDataTypeAttribute extends AttributeTypeEnum<InterfaceDataTypeEnum> {

   public final InterfaceDataTypeEnum Boolean = new InterfaceDataTypeEnum(0, "boolean", 1, "0", "1");
   public final InterfaceDataTypeEnum Character = new InterfaceDataTypeEnum(1, "character", 1, "0", "255");
   public final InterfaceDataTypeEnum uShort = new InterfaceDataTypeEnum(2, "uShort", 2, "0", "(2^16)-1");
   public final InterfaceDataTypeEnum sShort = new InterfaceDataTypeEnum(3, "sShort", 2, "-(2^15)", "(2^15)-1");
   public final InterfaceDataTypeEnum MiniEnum = new InterfaceDataTypeEnum(4, "minienum", 2, "", "");
   public final InterfaceDataTypeEnum Enumeration = new InterfaceDataTypeEnum(5, "enumeration", 4, "", "");
   public final InterfaceDataTypeEnum uInteger = new InterfaceDataTypeEnum(6, "uInteger", 4, "0", "(2^32)-1");
   public final InterfaceDataTypeEnum sInteger = new InterfaceDataTypeEnum(7, "sInteger", 4, "-(2^31)", "(2^31)-1");
   public final InterfaceDataTypeEnum Float = new InterfaceDataTypeEnum(8, "float", 4, "-3.4e+38", "3.4e+38");
   public final InterfaceDataTypeEnum Double = new InterfaceDataTypeEnum(9, "double", 8, "-1.8e+308", "1.8e+308");
   public final InterfaceDataTypeEnum uLong = new InterfaceDataTypeEnum(10, "uLong", 8, "0", "(2^64)-1");

   public InterfaceDataTypeAttribute(NamespaceToken namespace, int enumCount) {
      super(2455059983007225762L, namespace, "Interface Data Type Category", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public InterfaceDataTypeAttribute() {
      this(NamespaceToken.OSEE, 11);
   }

   public class InterfaceDataTypeEnum extends EnumToken {
      private Integer size;
      private String validMin;
      private String validMax;

      public InterfaceDataTypeEnum(int ordinal, String name, Integer byteSize, String min, String max) {
         super(ordinal, name);
         setSize(byteSize);
         setValidMin(min);
         setValidMax(max);
         addEnum(this);
      }

      public Integer getSize() {
         return size;
      }

      public void setSize(Integer size) {
         this.size = size;
      }

      public String getValidMin() {
         return validMin;
      }

      public void setValidMin(String validMin) {
         this.validMin = validMin;
      }

      public String getValidMax() {
         return validMax;
      }

      public void setValidMax(String validMax) {
         this.validMax = validMax;
      }
   }
}