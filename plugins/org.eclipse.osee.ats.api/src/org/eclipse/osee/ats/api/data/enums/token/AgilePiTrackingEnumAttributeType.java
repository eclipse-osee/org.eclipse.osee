/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import org.eclipse.osee.ats.api.data.enums.token.AgilePiTrackingEnumAttributeType.CancelReasonEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;

/**
 * @author Donald G. Dunne
 */
public class AgilePiTrackingEnumAttributeType extends AttributeTypeEnum<CancelReasonEnum> {

   public final CancelReasonEnum OutofScope = new CancelReasonEnum(0, "Out of Scope");
   public final CancelReasonEnum UpScope = new CancelReasonEnum(1, "Up-Scope");
   public final CancelReasonEnum PiPlanning = new CancelReasonEnum(2, "PI Planning");

   public AgilePiTrackingEnumAttributeType(NamespaceToken namespace, int enumCount) {
      super(873746263437976359L, namespace, "ats.Agile PI Tracking", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public AgilePiTrackingEnumAttributeType() {
      this(AtsTypeTokenProvider.ATS, 3);
   }

   public class CancelReasonEnum extends EnumToken {
      public CancelReasonEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}