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
import org.eclipse.osee.framework.core.enums.token.InterfaceStructureCategoryAttribute.InterfaceStructureCategoryEnum;

/**
 * @author Audrey Denk
 */
public class InterfaceStructureCategoryAttribute extends AttributeTypeEnum<InterfaceStructureCategoryEnum> {

   public final InterfaceStructureCategoryEnum spare_0 = new InterfaceStructureCategoryEnum(0, "spare");
   public final InterfaceStructureCategoryEnum Miscellaneous = new InterfaceStructureCategoryEnum(1, "Miscellaneous");
   public final InterfaceStructureCategoryEnum TacticalStatus =
      new InterfaceStructureCategoryEnum(2, "Tactical Status");
   public final InterfaceStructureCategoryEnum BitStatus = new InterfaceStructureCategoryEnum(3, "BIT Status");
   public final InterfaceStructureCategoryEnum FlightTest = new InterfaceStructureCategoryEnum(4, "Flight Test");
   public final InterfaceStructureCategoryEnum Trackfile = new InterfaceStructureCategoryEnum(5, "Trackfile");
   public final InterfaceStructureCategoryEnum Taskfile = new InterfaceStructureCategoryEnum(6, "Taskfile");
   public final InterfaceStructureCategoryEnum Network = new InterfaceStructureCategoryEnum(7, "Network");
   public final InterfaceStructureCategoryEnum NotApplicable = new InterfaceStructureCategoryEnum(8, "N/A");

   public InterfaceStructureCategoryAttribute(NamespaceToken namespace, int enumCount) {
      super(2455059983007225764L, namespace, "Interface Structure Category", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public InterfaceStructureCategoryAttribute() {
      this(NamespaceToken.OSEE, 9);
   }

   public class InterfaceStructureCategoryEnum extends EnumToken {
      public InterfaceStructureCategoryEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}