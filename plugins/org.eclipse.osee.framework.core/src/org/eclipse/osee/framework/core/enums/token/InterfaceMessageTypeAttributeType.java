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
import org.eclipse.osee.framework.core.enums.token.InterfaceMessageTypeAttributeType.InterfaceMessageTypeEnum;

/**
 * @author Audrey Denk
 */
public class InterfaceMessageTypeAttributeType extends AttributeTypeEnum<InterfaceMessageTypeEnum> {

   public final InterfaceMessageTypeEnum Connection = new InterfaceMessageTypeEnum(0, "Connection");
   public final InterfaceMessageTypeEnum Operational = new InterfaceMessageTypeEnum(1, "Operational");

   public InterfaceMessageTypeAttributeType(NamespaceToken namespace, int enumCount) {
      super(2455059983007225770L, namespace, "Interface Message Type", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public InterfaceMessageTypeAttributeType() {
      this(NamespaceToken.OSEE, 2);
   }

   public class InterfaceMessageTypeEnum extends EnumToken {
      public InterfaceMessageTypeEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}