/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.enums.token;

import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.token.HazardSeverityAttributeType.HazardSeverityEnum;

/**
 * @author Stephen J. Molaro
 */
public class HazardSeverityAttributeType extends AttributeTypeEnum<HazardSeverityEnum> {

   public final HazardSeverityEnum CatastrophicI = new HazardSeverityEnum(0, "Catastrophic, I");
   public final HazardSeverityEnum SevereMajorIi = new HazardSeverityEnum(1, "Severe-Major, II");
   public final HazardSeverityEnum CriticalIi = new HazardSeverityEnum(2, "Critical, II");
   public final HazardSeverityEnum MajorIii = new HazardSeverityEnum(3, "Major, III");
   public final HazardSeverityEnum MarginalIii = new HazardSeverityEnum(4, "Marginal, III");
   public final HazardSeverityEnum MinorIv = new HazardSeverityEnum(5, "Minor, IV");
   public final HazardSeverityEnum NegligibleIv = new HazardSeverityEnum(6, "Negligible, IV");
   public final HazardSeverityEnum NoEffectV = new HazardSeverityEnum(7, "No Effect, V");
   public final HazardSeverityEnum Unspecified = new HazardSeverityEnum(8, "Unspecified");

   public HazardSeverityAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1152921504606847141L, namespace, "Hazard Severity", mediaType, "", taggerType, 9);
   }

   public class HazardSeverityEnum extends EnumToken {
      public HazardSeverityEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}