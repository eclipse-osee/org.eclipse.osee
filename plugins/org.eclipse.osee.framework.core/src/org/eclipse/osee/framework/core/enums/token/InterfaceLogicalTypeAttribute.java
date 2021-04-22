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
import org.eclipse.osee.framework.core.enums.token.InterfaceLogicalTypeAttribute.InterfaceLogicalTypeEnum;

/**
 * @author Audrey Denk
 */
public class InterfaceLogicalTypeAttribute extends AttributeTypeEnum<InterfaceLogicalTypeEnum> {

   public final InterfaceLogicalTypeEnum Boolean = new InterfaceLogicalTypeEnum(0, "boolean", 1, "0", "1");
   public final InterfaceLogicalTypeEnum Character = new InterfaceLogicalTypeEnum(1, "character", 1, "0", "255");
   public final InterfaceLogicalTypeEnum uShort = new InterfaceLogicalTypeEnum(2, "uShort", 2, "0", "(2^16)-1");
   public final InterfaceLogicalTypeEnum sShort = new InterfaceLogicalTypeEnum(3, "sShort", 2, "-(2^15)", "(2^15)-1");
   public final InterfaceLogicalTypeEnum MiniEnum = new InterfaceLogicalTypeEnum(4, "minienum", 2, "", "");
   public final InterfaceLogicalTypeEnum Enumeration = new InterfaceLogicalTypeEnum(5, "enumeration", 4, "", "");
   public final InterfaceLogicalTypeEnum uInteger = new InterfaceLogicalTypeEnum(6, "uInteger", 4, "0", "(2^32)-1");
   public final InterfaceLogicalTypeEnum sInteger =
      new InterfaceLogicalTypeEnum(7, "sInteger", 4, "-(2^31)", "(2^31)-1");
   public final InterfaceLogicalTypeEnum Float = new InterfaceLogicalTypeEnum(8, "float", 4, "-3.4e+38", "3.4e+38");
   public final InterfaceLogicalTypeEnum Double = new InterfaceLogicalTypeEnum(9, "double", 8, "-1.8e+308", "1.8e+308");
   public final InterfaceLogicalTypeEnum uLong = new InterfaceLogicalTypeEnum(10, "uLong", 8, "0", "(2^64)-1");

   public InterfaceLogicalTypeAttribute(NamespaceToken namespace, int enumCount) {
      super(2455059983007225762L, namespace, "Interface Logical Type", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public InterfaceLogicalTypeAttribute() {
      this(NamespaceToken.OSEE, 11);
   }

   public class InterfaceLogicalTypeEnum extends EnumToken {

      public InterfaceLogicalTypeEnum(int ordinal, String name, Integer byteSize, String min, String max) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}