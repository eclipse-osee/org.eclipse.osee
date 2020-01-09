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

import org.eclipse.osee.ats.api.data.enums.token.ReviewBlocksAttributeType.ReviewBlocksEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;

/**
 * @author Stephen J. Molaro
 */
public class ReviewBlocksAttributeType extends AttributeTypeEnum<ReviewBlocksEnum> {

   // @formatter:off
	public final ReviewBlocksEnum None = new ReviewBlocksEnum(0, "None");
	public final ReviewBlocksEnum Transition = new ReviewBlocksEnum(1, "Transition");
	public final ReviewBlocksEnum Commit = new ReviewBlocksEnum(2, "Commit");
	// @formatter:on

   public ReviewBlocksAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1152921504606847176L, namespace, "ats.Review Blocks", mediaType, "", taggerType);
   }

   public class ReviewBlocksEnum extends EnumToken {
      public ReviewBlocksEnum(int ordinal, String name) {
         super(ordinal, name);
      }
   }
}
