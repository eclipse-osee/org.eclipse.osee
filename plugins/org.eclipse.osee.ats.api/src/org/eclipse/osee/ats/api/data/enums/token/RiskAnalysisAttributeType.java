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
import org.eclipse.osee.ats.api.data.enums.token.RiskAnalysisAttributeType.RiskAnalysisEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;

/**
 * @author Donald G. Dunne
 */
public class RiskAnalysisAttributeType extends AttributeTypeEnum<RiskAnalysisEnum> {

   public final RiskAnalysisEnum Small = new RiskAnalysisEnum(0, "Small");
   public final RiskAnalysisEnum Medium = new RiskAnalysisEnum(1, "Medium");
   public final RiskAnalysisEnum Large = new RiskAnalysisEnum(2, "Large");

   public RiskAnalysisAttributeType(NamespaceToken namespace, int enumCount) {
      super(5584829913357566855L, namespace, "ats.Risk Analysis", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public RiskAnalysisAttributeType() {
      this(AtsTypeTokenProvider.ATS, 3);
   }

   public class RiskAnalysisEnum extends EnumToken {
      public RiskAnalysisEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}