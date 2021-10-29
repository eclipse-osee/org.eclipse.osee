/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import org.eclipse.osee.ats.api.data.enums.token.RiskFactorAttributeType.RiskAnalysisEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;

/**
 * @author Donald G. Dunne
 */
public class RiskFactorAttributeType extends AttributeTypeEnum<RiskAnalysisEnum> {

   public final RiskAnalysisEnum Log = new RiskAnalysisEnum(0, "Low");
   public final RiskAnalysisEnum Medium = new RiskAnalysisEnum(1, "Medium");
   public final RiskAnalysisEnum High = new RiskAnalysisEnum(2, "High");

   public RiskFactorAttributeType(NamespaceToken namespace, int enumCount) {
      super(191191074127412492L, namespace, "ats.Risk Factor", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public RiskFactorAttributeType() {
      this(AtsTypeTokenProvider.ATS, 3);
   }

   public class RiskAnalysisEnum extends EnumToken {
      public RiskAnalysisEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}