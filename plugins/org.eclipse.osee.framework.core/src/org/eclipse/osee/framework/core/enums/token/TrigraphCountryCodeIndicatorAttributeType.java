/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import org.eclipse.osee.framework.core.enums.token.TrigraphCountryCodeIndicatorAttributeType.TrigraphCountryCodeIndicatorEnum;
import org.eclipse.osee.framework.core.publishing.TrigraphCountryCodeIndicator;

/**
 * Initialize new multi-value enumeration with the values from Tri-graph Country Code Indicator
 *
 * @author Md I. Khan
 */

public class TrigraphCountryCodeIndicatorAttributeType extends AttributeTypeEnum<TrigraphCountryCodeIndicatorEnum> {

   public final TrigraphCountryCodeIndicatorEnum[] enumList =
      new TrigraphCountryCodeIndicatorEnum[TrigraphCountryCodeIndicator.values().length];

   public TrigraphCountryCodeIndicatorAttributeType(NamespaceToken namespace, int enumCount) {
      super(8003115831873458510L, namespace, "CUI Release List", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);

      for (int i = 0; i < enumList.length; i++) {
         enumList[i] = new TrigraphCountryCodeIndicatorEnum(i, TrigraphCountryCodeIndicator.values()[i].toString());
      }
   }

   public TrigraphCountryCodeIndicatorAttributeType() {
      this(NamespaceToken.OSEE, TrigraphCountryCodeIndicator.values().length);
   }

   public class TrigraphCountryCodeIndicatorEnum extends EnumToken {

      public TrigraphCountryCodeIndicatorEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }

   }
}
