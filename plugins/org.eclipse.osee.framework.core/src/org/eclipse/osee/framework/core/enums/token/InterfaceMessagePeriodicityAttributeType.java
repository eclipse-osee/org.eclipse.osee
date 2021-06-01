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
import org.eclipse.osee.framework.core.enums.token.InterfaceMessagePeriodicityAttributeType.InterfaceMessagePeriodicityEnum;

/**
 * @author Audrey Denk
 */

public class InterfaceMessagePeriodicityAttributeType extends AttributeTypeEnum<InterfaceMessagePeriodicityEnum> {

   public final InterfaceMessagePeriodicityEnum Aperiodic = new InterfaceMessagePeriodicityEnum(0, "Aperiodic");
   public final InterfaceMessagePeriodicityEnum Periodic = new InterfaceMessagePeriodicityEnum(1, "Periodic");
   public final InterfaceMessagePeriodicityEnum OnDemand = new InterfaceMessagePeriodicityEnum(2, "OnDemand");

   public InterfaceMessagePeriodicityAttributeType(NamespaceToken namespace, int enumCount) {
      super(3899709087455064789L, namespace, "Interface Message Periodicity", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public InterfaceMessagePeriodicityAttributeType() {
      this(NamespaceToken.OSEE, 3);
   }

   public class InterfaceMessagePeriodicityEnum extends EnumToken {
      public InterfaceMessagePeriodicityEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}