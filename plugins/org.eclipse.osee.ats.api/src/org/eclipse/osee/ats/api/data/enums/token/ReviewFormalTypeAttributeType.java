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

   public ReviewFormalTypeAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1152921504606847177L, namespace, "ats.Review Formal Type", mediaType, "", taggerType, 2);
   }

   public class ReviewFormalTypeEnum extends EnumToken {
      public ReviewFormalTypeEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}