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
import org.eclipse.osee.framework.core.enums.token.RequiredIndicatorFrequencyAttributeType.RequiredIndicatorFrequencyEnum;
import org.eclipse.osee.framework.core.publishing.RequiredIndicatorFrequencyIndicator;

/**
 * Initialize multi-level enumeration of Required Indicator Frequency
 *
 * @author Md I. Khan
 */

public class RequiredIndicatorFrequencyAttributeType extends AttributeTypeEnum<RequiredIndicatorFrequencyEnum> {

   public final RequiredIndicatorFrequencyEnum[] requiredIndicatorFrequencyEnumList =
      new RequiredIndicatorFrequencyEnum[RequiredIndicatorFrequencyIndicator.values().length];

   public RequiredIndicatorFrequencyAttributeType(NamespaceToken namespace, int enumCount) {
      super(1722628299042865586L, namespace, "Required Indicator Frequency", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);

      for (int i = 0; i < requiredIndicatorFrequencyEnumList.length; i++) {
         requiredIndicatorFrequencyEnumList[i] =
            new RequiredIndicatorFrequencyEnum(i, RequiredIndicatorFrequencyIndicator.values()[i].name());
      }
   }

   public RequiredIndicatorFrequencyAttributeType() {
      this(NamespaceToken.OSEE, RequiredIndicatorFrequencyIndicator.values().length);
   }

   public class RequiredIndicatorFrequencyEnum extends EnumToken {

      public RequiredIndicatorFrequencyEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }

   }

}
