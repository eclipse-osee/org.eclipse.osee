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

import org.eclipse.osee.ats.api.data.enums.token.ChangeTypeAttributeType.ChangeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;

/**
 * @author Stephen J. Molaro
 */
public class ChangeTypeAttributeType extends AttributeTypeEnum<ChangeTypeEnum> {

   // @formatter:off
	public final ChangeTypeEnum Improvement = new ChangeTypeEnum(0, "Improvement");
	public final ChangeTypeEnum Problem = new ChangeTypeEnum(1, "Problem");
	public final ChangeTypeEnum Support = new ChangeTypeEnum(2, "Support");
	public final ChangeTypeEnum Refinement = new ChangeTypeEnum(3, "Refinement");
	// @formatter:on

   public ChangeTypeAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1152921504606847180L, namespace, "ats.Change Type", mediaType, "", taggerType);
   }

   public class ChangeTypeEnum extends EnumToken {
      public ChangeTypeEnum(int ordinal, String name) {
         super(ordinal, name);
      }
   }
}
