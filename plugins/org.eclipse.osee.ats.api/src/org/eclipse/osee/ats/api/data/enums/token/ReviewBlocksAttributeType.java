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
import org.eclipse.osee.ats.api.data.enums.token.ReviewBlocksAttributeType.ReviewBlocksEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;

/**
 * @author Stephen J. Molaro
 */
public class ReviewBlocksAttributeType extends AttributeTypeEnum<ReviewBlocksEnum> {

   public final ReviewBlocksEnum None = new ReviewBlocksEnum(0, "None");
   public final ReviewBlocksEnum Transition = new ReviewBlocksEnum(1, "Transition");
   public final ReviewBlocksEnum Commit = new ReviewBlocksEnum(2, "Commit");

   public ReviewBlocksAttributeType(NamespaceToken namespace, int enumCount) {
      super(1152921504606847176L, namespace, "ats.Review Blocks", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public ReviewBlocksAttributeType() {
      this(AtsTypeTokenProvider.ATS, 3);
   }

   public class ReviewBlocksEnum extends EnumToken {
      public ReviewBlocksEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}