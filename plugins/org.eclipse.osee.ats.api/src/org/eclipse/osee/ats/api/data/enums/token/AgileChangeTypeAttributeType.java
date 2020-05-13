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

import org.eclipse.osee.ats.api.data.enums.token.AgileChangeTypeAttributeType.ChangeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;

/**
 * @author Stephen J. Molaro
 */
public class AgileChangeTypeAttributeType extends AttributeTypeEnum<ChangeTypeEnum> {

   public final ChangeTypeEnum Improvement = new ChangeTypeEnum(0, "Improvement");
   public final ChangeTypeEnum Problem = new ChangeTypeEnum(1, "Problem");
   public final ChangeTypeEnum Support = new ChangeTypeEnum(2, "Support");
   public final ChangeTypeEnum Refinement = new ChangeTypeEnum(3, "Refinement");
   public final ChangeTypeEnum Epic = new ChangeTypeEnum(4, "Epic");
   public final ChangeTypeEnum Story = new ChangeTypeEnum(5, "Story");
   public final ChangeTypeEnum Impediment = new ChangeTypeEnum(6, "Impediment");
   public final ChangeTypeEnum Task = new ChangeTypeEnum(7, "Task");
   public final ChangeTypeEnum CustomerFeature = new ChangeTypeEnum(8, "Customer Feature");
   public final ChangeTypeEnum Requirement = new ChangeTypeEnum(9, "Requirement");

   public AgileChangeTypeAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1152921504606851584L, namespace, "agile.Change Type", mediaType, "", taggerType, 10);
   }

   public class ChangeTypeEnum extends EnumToken {
      public ChangeTypeEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}