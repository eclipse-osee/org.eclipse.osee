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

package org.eclipse.osee.framework.core.enums.token;

import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.token.SoftwareControlCategoryAttributeType.SoftwareControlCategoryEnum;

/**
 * @author Stephen J. Molaro
 */
public class SoftwareControlCategoryAttributeType extends AttributeTypeEnum<SoftwareControlCategoryEnum> {

   public final SoftwareControlCategoryEnum Unspecified = new SoftwareControlCategoryEnum(0, "Unspecified");
   public final SoftwareControlCategoryEnum _1At = new SoftwareControlCategoryEnum(1, "1(AT)");
   public final SoftwareControlCategoryEnum _2Sat = new SoftwareControlCategoryEnum(2, "2(SAT)");
   public final SoftwareControlCategoryEnum _3Rft = new SoftwareControlCategoryEnum(3, "3(RFT)");
   public final SoftwareControlCategoryEnum _4In = new SoftwareControlCategoryEnum(4, "4(IN)");
   public final SoftwareControlCategoryEnum _5Nsi = new SoftwareControlCategoryEnum(5, "5(NSI)");

   public SoftwareControlCategoryAttributeType(NamespaceToken namespace, int enumCount) {
      super(1958401980089733639L, namespace, "Software Control Category", MediaType.TEXT_PLAIN,
         "Software Control Category Classification", TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public SoftwareControlCategoryAttributeType() {
      this(NamespaceToken.OSEE, 6);
   }

   public class SoftwareControlCategoryEnum extends EnumToken {
      public SoftwareControlCategoryEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}