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
import org.eclipse.osee.framework.core.enums.token.VerificationLevelAttributeType.VerificationLevelEnum;

/**
 * @author Stephen J. Molaro
 */
public class VerificationLevelAttributeType extends AttributeTypeEnum<VerificationLevelEnum> {

   // @formatter:off
	public final VerificationLevelEnum System = new VerificationLevelEnum(0, "System");
	public final VerificationLevelEnum Subsystem = new VerificationLevelEnum(1, "Subsystem");
	public final VerificationLevelEnum Component = new VerificationLevelEnum(2, "Component");
	public final VerificationLevelEnum Unspecified = new VerificationLevelEnum(3, "Unspecified");
	public final VerificationLevelEnum NA = new VerificationLevelEnum(4, "N/A");
	// @formatter:on

   public VerificationLevelAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1152921504606847115L, namespace, "Verification Level", mediaType, "", taggerType);
   }

   public class VerificationLevelEnum extends EnumToken {
      public VerificationLevelEnum(int ordinal, String name) {
         super(ordinal, name);
      }
   }
}
