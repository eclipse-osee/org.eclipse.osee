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

   public final InterfaceLogicalTypeEnum Boolean = new InterfaceLogicalTypeEnum(0, "boolean");
   public final InterfaceLogicalTypeEnum Character = new InterfaceLogicalTypeEnum(1, "character");
   public final InterfaceLogicalTypeEnum Enumeration = new InterfaceLogicalTypeEnum(2, "enumeration");
   public final InterfaceLogicalTypeEnum Octet = new InterfaceLogicalTypeEnum(3, "octet");
   public final InterfaceLogicalTypeEnum Hex = new InterfaceLogicalTypeEnum(4, "hex");
   public final InterfaceLogicalTypeEnum Integer = new InterfaceLogicalTypeEnum(5, "integer");
   public final InterfaceLogicalTypeEnum UnsignedInteger = new InterfaceLogicalTypeEnum(6, "unsigned integer");
   public final InterfaceLogicalTypeEnum Short = new InterfaceLogicalTypeEnum(7, "short");
   public final InterfaceLogicalTypeEnum UnsignedShort = new InterfaceLogicalTypeEnum(8, "unsigned short");
   public final InterfaceLogicalTypeEnum Long = new InterfaceLogicalTypeEnum(9, "long");
   public final InterfaceLogicalTypeEnum UnsignedLong = new InterfaceLogicalTypeEnum(10, "unsigned long");
   public final InterfaceLogicalTypeEnum LongLong = new InterfaceLogicalTypeEnum(11, "long long");
   public final InterfaceLogicalTypeEnum UnsignedLongLong = new InterfaceLogicalTypeEnum(12, "unsigned long long");
   public final InterfaceLogicalTypeEnum Double = new InterfaceLogicalTypeEnum(13, "double");
   public final InterfaceLogicalTypeEnum LongDouble = new InterfaceLogicalTypeEnum(14, "long double");
   public final InterfaceLogicalTypeEnum Float = new InterfaceLogicalTypeEnum(15, "float");

   public InterfaceLogicalTypeAttribute(NamespaceToken namespace, int enumCount) {
      super(2455059983007225762L, namespace, "Interface Logical Type", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public InterfaceLogicalTypeAttribute() {
      this(NamespaceToken.OSEE, 16);
   }

   public class InterfaceLogicalTypeEnum extends EnumToken {

      public InterfaceLogicalTypeEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}