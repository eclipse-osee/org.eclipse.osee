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

import org.eclipse.osee.ats.api.data.enums.token.ChangeTypeAttributeType.ChangeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;

/**
 * @author Stephen J. Molaro
 */
public class ChangeTypeAttributeType extends AttributeTypeEnum<ChangeTypeEnum> {

   public final ChangeTypeEnum Improvement = new ChangeTypeEnum(0, "Improvement");
   public final ChangeTypeEnum Problem = new ChangeTypeEnum(1, "Problem");
   public final ChangeTypeEnum Support = new ChangeTypeEnum(2, "Support");
   public final ChangeTypeEnum Refinement = new ChangeTypeEnum(3, "Refinement");

   public ChangeTypeAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1152921504606847180L, namespace, "ats.Change Type", mediaType, "", taggerType, 4);
   }

   public class ChangeTypeEnum extends EnumToken {
      public ChangeTypeEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}