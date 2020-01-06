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
package org.eclipse.osee.framework.core.enums.token;

import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.token.VerificationEventAttributeType.VerificationEventEnum;

/**
 * @author Stephen J. Molaro
 */
public class VerificationEventAttributeType extends AttributeTypeEnum<VerificationEventEnum> {

   // @formatter:off
	public final VerificationEventEnum Unspecified = new VerificationEventEnum(0, "Unspecified");
	// @formatter:on

   public VerificationEventAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1152921504606847124L, namespace, "Verification Event", mediaType, "", taggerType);
   }

   public class VerificationEventEnum extends EnumToken {
      public VerificationEventEnum(int ordinal, String name) {
         super(ordinal, name);
      }
   }
}
