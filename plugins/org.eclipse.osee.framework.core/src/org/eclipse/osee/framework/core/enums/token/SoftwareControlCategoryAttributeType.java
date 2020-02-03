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
import org.eclipse.osee.framework.core.enums.token.SoftwareControlCategoryAttributeType.SoftwareControlCategoryEnum;

/**
 * @author Stephen J. Molaro
 */
public class SoftwareControlCategoryAttributeType extends AttributeTypeEnum<SoftwareControlCategoryEnum> {

   public final SoftwareControlCategoryEnum _1At = new SoftwareControlCategoryEnum(0, "1(AT)");
   public final SoftwareControlCategoryEnum _2Sat = new SoftwareControlCategoryEnum(1, "2(SAT)");
   public final SoftwareControlCategoryEnum _3Rft = new SoftwareControlCategoryEnum(2, "3(RFT)");
   public final SoftwareControlCategoryEnum _4In = new SoftwareControlCategoryEnum(3, "4(IN)");
   public final SoftwareControlCategoryEnum _5Nsi = new SoftwareControlCategoryEnum(4, "5(NSI)");
   public final SoftwareControlCategoryEnum Unspecified = new SoftwareControlCategoryEnum(5, "Unspecified");

   public SoftwareControlCategoryAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1958401980089733639L, namespace, "Software Control Category", mediaType,
         "Software Control Category Classification", taggerType, 6);
   }

   public class SoftwareControlCategoryEnum extends EnumToken {
      public SoftwareControlCategoryEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}