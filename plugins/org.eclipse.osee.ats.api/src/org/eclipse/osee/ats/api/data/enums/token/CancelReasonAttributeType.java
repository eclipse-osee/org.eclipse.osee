/*********************************************************************
 * Copyright (c) 2019 Boeing
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
import org.eclipse.osee.ats.api.data.enums.token.CancelReasonAttributeType.CancelReasonEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;

/**
 * @author Stephen J. Molaro
 */
public class CancelReasonAttributeType extends AttributeTypeEnum<CancelReasonEnum> {

   public final CancelReasonEnum CanNotDuplicate = new CancelReasonEnum(0, "Can Not Duplicate");
   public final CancelReasonEnum Duplicate = new CancelReasonEnum(1, "Duplicate");
   public final CancelReasonEnum NotAProblem = new CancelReasonEnum(2, "Not a Problem");
   public final CancelReasonEnum OtherMustEnterCancelledDetails =
      new CancelReasonEnum(3, "Other (Must enter cancelled details)");

   public CancelReasonAttributeType(NamespaceToken namespace, int enumCount) {
      super(5718762723487704057L, namespace, "ats.Cancel Reason", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public CancelReasonAttributeType() {
      this(AtsTypeTokenProvider.ATS, 4);
   }

   public class CancelReasonEnum extends EnumToken {
      public CancelReasonEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}