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

import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.data.AtsTypeTokenProvider;
import org.eclipse.osee.ats.api.data.enums.token.AgileChangeTypeAttributeType.AgileChangeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;

/**
 * @author Stephen J. Molaro
 */
public class AgileChangeTypeAttributeType extends AttributeTypeEnum<AgileChangeTypeEnum> {

   public final AgileChangeTypeEnum Improvement = new AgileChangeTypeEnum(0, "Improvement");
   public final AgileChangeTypeEnum Problem = new AgileChangeTypeEnum(1, "Problem");
   public final AgileChangeTypeEnum Support = new AgileChangeTypeEnum(2, "Support");
   public final AgileChangeTypeEnum Refinement = new AgileChangeTypeEnum(3, "Refinement");
   public final AgileChangeTypeEnum Epic = new AgileChangeTypeEnum(4, "Epic");
   public final AgileChangeTypeEnum Story = new AgileChangeTypeEnum(5, "Story");
   public final AgileChangeTypeEnum Impediment = new AgileChangeTypeEnum(6, "Impediment");
   public final AgileChangeTypeEnum Task = new AgileChangeTypeEnum(7, "Task");
   public final AgileChangeTypeEnum CustomerFeature = new AgileChangeTypeEnum(8, "Customer Feature");
   public final AgileChangeTypeEnum Requirement = new AgileChangeTypeEnum(9, "Requirement");

   public AgileChangeTypeAttributeType(NamespaceToken namespace, int enumCount) {
      super(1152921504606851584L, namespace, "agile.Change Type", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public AgileChangeTypeAttributeType() {
      this(AtsTypeTokenProvider.ATS, 10);
   }

   public class AgileChangeTypeEnum extends EnumToken {
      public AgileChangeTypeEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}