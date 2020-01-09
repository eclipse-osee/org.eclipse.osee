/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.data.enums.token;

import org.eclipse.osee.ats.api.data.enums.token.CancelReasonAttributeType.CancelReasonEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;

/**
 * @author Stephen J. Molaro
 */
public class CancelReasonAttributeType extends AttributeTypeEnum<CancelReasonEnum> {

   // @formatter:off
	public final CancelReasonEnum CanNotDuplicate = new CancelReasonEnum(0, "Can Not Duplicate");
	public final CancelReasonEnum Duplicate = new CancelReasonEnum(1, "Duplicate");
	public final CancelReasonEnum NotAProblem = new CancelReasonEnum(2, "Not a Problem");
	public final CancelReasonEnum OtherMustEnterCancelledDetails = new CancelReasonEnum(3, "Other (Must enter cancelled details)");
	// @formatter:on

   public CancelReasonAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(5718762723487704057L, namespace, "ats.Cancel Reason", mediaType, "", taggerType);
   }

   public class CancelReasonEnum extends EnumToken {
      public CancelReasonEnum(int ordinal, String name) {
         super(ordinal, name);
      }
   }
}
