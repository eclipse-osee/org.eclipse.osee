/*********************************************************************
 * Copyright (c) 2020 Boeing
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
import org.eclipse.osee.framework.core.enums.token.InterfaceStructureCategoryAttribute.InterfaceSubMessageCategoryEnum;

/**
 * @author Audrey Denk
 */
public class InterfaceStructureCategoryAttribute extends AttributeTypeEnum<InterfaceSubMessageCategoryEnum> {

   public final InterfaceSubMessageCategoryEnum spare_0 = new InterfaceSubMessageCategoryEnum(0, "spare");
   public final InterfaceSubMessageCategoryEnum Miscellaneous = new InterfaceSubMessageCategoryEnum(1, "Miscellaneous");
   public final InterfaceSubMessageCategoryEnum TacticalStatus =
      new InterfaceSubMessageCategoryEnum(2, "Tactical Status");
   public final InterfaceSubMessageCategoryEnum BitStatus = new InterfaceSubMessageCategoryEnum(3, "BIT Status");
   public final InterfaceSubMessageCategoryEnum FlightTest = new InterfaceSubMessageCategoryEnum(4, "Flight Test");
   public final InterfaceSubMessageCategoryEnum Trackfile = new InterfaceSubMessageCategoryEnum(5, "Trackfile");
   public final InterfaceSubMessageCategoryEnum Taskfile = new InterfaceSubMessageCategoryEnum(6, "Taskfile");
   public final InterfaceSubMessageCategoryEnum Network = new InterfaceSubMessageCategoryEnum(7, "Network");
   public final InterfaceSubMessageCategoryEnum NotApplicable = new InterfaceSubMessageCategoryEnum(8, "N/A");

   public InterfaceStructureCategoryAttribute(NamespaceToken namespace, int enumCount) {
      super(2455059983007225764L, namespace, "Interface SubMessage Category", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public InterfaceStructureCategoryAttribute() {
      this(NamespaceToken.OSEE, 9);
   }

   public class InterfaceSubMessageCategoryEnum extends EnumToken {
      public InterfaceSubMessageCategoryEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}