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
import org.eclipse.osee.ats.api.data.enums.token.ReviewFormalTypeAttributeType.ReviewFormalTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;

/**
 * @author Stephen J. Molaro
 */
public class ReviewFormalTypeAttributeType extends AttributeTypeEnum<ReviewFormalTypeEnum> {

   public final ReviewFormalTypeEnum InFormal = new ReviewFormalTypeEnum(0, "InFormal");
   public final ReviewFormalTypeEnum Formal = new ReviewFormalTypeEnum(1, "Formal");

   public ReviewFormalTypeAttributeType(NamespaceToken namespace, int enumCount) {
      super(1152921504606847177L, namespace, "ats.Review Formal Type", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public ReviewFormalTypeAttributeType() {
      this(AtsTypeTokenProvider.ATS, 2);
   }

   public class ReviewFormalTypeEnum extends EnumToken {
      public ReviewFormalTypeEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}