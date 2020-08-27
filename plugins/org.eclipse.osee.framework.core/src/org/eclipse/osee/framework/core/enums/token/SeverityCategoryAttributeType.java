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

   public SeverityCategoryAttributeType(NamespaceToken namespace, int enumCount) {
      super(1152921504606847114L, namespace, "Severity Category", MediaType.TEXT_PLAIN,
         "Severity Category Classification", TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public SeverityCategoryAttributeType() {
      this(NamespaceToken.OSEE, 6);
   }

   public class SeverityCategoryEnum extends EnumToken {
      public SeverityCategoryEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}