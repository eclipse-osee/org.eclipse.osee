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
import org.eclipse.osee.framework.core.enums.token.CuiLimitedDisseminationControlIndicatorAttributeType.CuiLimitedDisseminationControlIndicatorEnum;
import org.eclipse.osee.framework.core.publishing.CuiLimitedDisseminationControlIndicator;

/**
 * Initialize multi-level enumeration of CUI Limited Dissemination Control Indicator
 *
 * @author Md I. Khan
 */

public class CuiLimitedDisseminationControlIndicatorAttributeType extends AttributeTypeEnum<CuiLimitedDisseminationControlIndicatorEnum> {

   public final CuiLimitedDisseminationControlIndicatorEnum[] enumList =
      new CuiLimitedDisseminationControlIndicatorEnum[CuiLimitedDisseminationControlIndicator.values().length];

   public CuiLimitedDisseminationControlIndicatorAttributeType(NamespaceToken namespace, int enumCount) {
      super(6036586745962781830L, namespace, "CUI Limited Dissemination Control", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);

      for (int i = 0; i < enumList.length; i++) {
         enumList[i] = new CuiLimitedDisseminationControlIndicatorEnum(i,
            CuiLimitedDisseminationControlIndicator.values()[i].name());
      }
   }

   public CuiLimitedDisseminationControlIndicatorAttributeType() {
      this(NamespaceToken.OSEE, CuiLimitedDisseminationControlIndicator.values().length);
   }

   public class CuiLimitedDisseminationControlIndicatorEnum extends EnumToken {

      public CuiLimitedDisseminationControlIndicatorEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }

   }
}
