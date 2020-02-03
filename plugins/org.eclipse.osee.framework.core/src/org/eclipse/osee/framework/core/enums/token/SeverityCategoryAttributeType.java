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
import org.eclipse.osee.framework.core.enums.token.SeverityCategoryAttributeType.SeverityCategoryEnum;

/**
 * @author Stephen J. Molaro
 */
public class SeverityCategoryAttributeType extends AttributeTypeEnum<SeverityCategoryEnum> {

   public final SeverityCategoryEnum I = new SeverityCategoryEnum(0, "I");
   public final SeverityCategoryEnum II = new SeverityCategoryEnum(1, "II");
   public final SeverityCategoryEnum III = new SeverityCategoryEnum(2, "III");
   public final SeverityCategoryEnum IV = new SeverityCategoryEnum(3, "IV");
   public final SeverityCategoryEnum NH = new SeverityCategoryEnum(4, "NH");
   public final SeverityCategoryEnum Unspecified = new SeverityCategoryEnum(5, "Unspecified");

   public SeverityCategoryAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1152921504606847114L, namespace, "Severity Category", mediaType, "Severity Category Classification",
         taggerType, 6);
   }

   public class SeverityCategoryEnum extends EnumToken {
      public SeverityCategoryEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}