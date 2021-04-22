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
import org.eclipse.osee.framework.core.enums.token.InterfaceMessageRateAttributeType.InterfaceMessageRateEnum;

/**
 * @author Audrey Denk
 */

public class InterfaceMessageRateAttributeType extends AttributeTypeEnum<InterfaceMessageRateEnum> {

   public final InterfaceMessageRateEnum one = new InterfaceMessageRateEnum(0, "1");
   public final InterfaceMessageRateEnum five = new InterfaceMessageRateEnum(1, "5");
   public final InterfaceMessageRateEnum ten = new InterfaceMessageRateEnum(2, "10");
   public final InterfaceMessageRateEnum twenty = new InterfaceMessageRateEnum(3, "20");

   public InterfaceMessageRateAttributeType(NamespaceToken namespace, int enumCount) {
      super(2455059983007225763L, namespace, "Interface Message Rate", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public InterfaceMessageRateAttributeType() {
      this(NamespaceToken.OSEE, 4);
   }

   public class InterfaceMessageRateEnum extends EnumToken {
      public InterfaceMessageRateEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}