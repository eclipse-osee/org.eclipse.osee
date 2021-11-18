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
package org.eclipse.osee.ats.api.data.enums.token;

import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.data.AtsTypeTokenProvider;
import org.eclipse.osee.ats.api.data.enums.token.BitStateEnumAttributeType.BitStateEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;

/**
 * @author Donald G. Dunne
 */
public class BitStateEnumAttributeType extends AttributeTypeEnum<BitStateEnum> {

   public final BitStateEnum Open = new BitStateEnum(0, "Open");
   public final BitStateEnum Analyzed = new BitStateEnum(1, "Analyzed");
   public final BitStateEnum InWork = new BitStateEnum(2, "InWork");
   public final BitStateEnum Promoted = new BitStateEnum(3, "Promoted");
   public final BitStateEnum Closed = new BitStateEnum(4, "Closed");
   public final BitStateEnum Deferred = new BitStateEnum(5, "Deferred");
   public final BitStateEnum Cancelled = new BitStateEnum(6, "Cancelled");

   public BitStateEnumAttributeType(NamespaceToken namespace, int enumCount) {
      super(1512539363664555249L, namespace, "ats.Bit State", MediaType.TEXT_PLAIN, "", TaggerTypeToken.PlainTextTagger,
         enumCount);
   }

   public BitStateEnumAttributeType() {
      this(AtsTypeTokenProvider.ATS, 4);
   }

   public class BitStateEnum extends EnumToken {
      public BitStateEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}