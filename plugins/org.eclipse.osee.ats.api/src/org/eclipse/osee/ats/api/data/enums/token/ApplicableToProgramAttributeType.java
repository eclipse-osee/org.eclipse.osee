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

package org.eclipse.osee.ats.api.data.enums.token;

import org.eclipse.osee.ats.api.data.enums.token.ApplicableToProgramAttributeType.ApplicableToProgramEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;

/**
 * @author Stephen J. Molaro
 */
public class ApplicableToProgramAttributeType extends AttributeTypeEnum<ApplicableToProgramEnum> {

   public final ApplicableToProgramEnum Yes = new ApplicableToProgramEnum(0, "Yes");
   public final ApplicableToProgramEnum No = new ApplicableToProgramEnum(1, "No");

   public ApplicableToProgramAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1152921949227188394L, namespace, "ats.Applicable to Program", mediaType, "", taggerType, 2);
   }

   public class ApplicableToProgramEnum extends EnumToken {
      public ApplicableToProgramEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}