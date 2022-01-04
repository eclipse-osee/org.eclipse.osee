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

package org.eclipse.osee.framework.core.data.computed;

import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.ComputedCharacteristicEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.token.SafetySeverityAttributeType.SafetySeverityEnum;
import org.eclipse.osee.framework.core.enums.token.SoftwareControlCategoryAttributeType.SoftwareControlCategoryEnum;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Stephen J. Molaro
 */
public final class ComputedSoftwareCriticalityIndex extends ComputedCharacteristicEnum<EnumToken> {

   public final SoftwareCriticalityIndexEnum Unspecified = new SoftwareCriticalityIndexEnum(0, "Unspecified");
   public final SoftwareCriticalityIndexEnum SwCI1 = new SoftwareCriticalityIndexEnum(1, "SwCI 1");
   public final SoftwareCriticalityIndexEnum SwCI2 = new SoftwareCriticalityIndexEnum(2, "SwCI 2");
   public final SoftwareCriticalityIndexEnum SwCI3 = new SoftwareCriticalityIndexEnum(3, "SwCI 3");
   public final SoftwareCriticalityIndexEnum SwCI4 = new SoftwareCriticalityIndexEnum(4, "SwCI 4");
   public final SoftwareCriticalityIndexEnum SwCI5 = new SoftwareCriticalityIndexEnum(5, "SwCI 5");

   public class SoftwareCriticalityIndexEnum extends EnumToken {
      public SoftwareCriticalityIndexEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }

   public final SoftwareCriticalityIndexEnum[][] softwareCriticalityMatrix = {
      {SwCI1, SwCI1, SwCI3, SwCI4},
      {SwCI1, SwCI2, SwCI3, SwCI4},
      {SwCI2, SwCI3, SwCI4, SwCI4},
      {SwCI3, SwCI4, SwCI4, SwCI4},
      {SwCI5, SwCI5, SwCI5, SwCI5}};

   public ComputedSoftwareCriticalityIndex(Long id, String name, TaggerTypeToken taggerType, NamespaceToken namespace, String description, List<AttributeTypeGeneric<EnumToken>> typesToCompute) {
      super(id, name, taggerType, namespace, description, typesToCompute, 6);
   }

   @Override
   public boolean isMultiplicityValid(ArtifactTypeToken artifactType) {
      return exactlyTwoValues(artifactType);
   }

   @Override
   public EnumToken calculate(List<EnumToken> computingValues) {
      //Considered for switch to Long but don't believe this enum stores an Id, instead stores a enum value into a Array
      int softwareControlCategory;
      int safetySeverity;
      if (computingValues.get(0) instanceof SoftwareControlCategoryEnum && computingValues.get(
         1) instanceof SafetySeverityEnum) {
         softwareControlCategory = computingValues.get(0).getIdIntValue();
         safetySeverity = computingValues.get(1).getIdIntValue();
      } else if (computingValues.get(0) instanceof SafetySeverityEnum && computingValues.get(
         1) instanceof SoftwareControlCategoryEnum) {
         safetySeverity = computingValues.get(0).getIdIntValue();
         softwareControlCategory = computingValues.get(1).getIdIntValue();
      } else {
         throw new OseeCoreException("Invalid inputs [%s] and [%s] used for calculation of Software Criticality Index",
            computingValues.get(0).getName(), computingValues.get(1).getName());
      }
      try {
         return softwareCriticalityMatrix[softwareControlCategory - 1][safetySeverity];
      } catch (Exception ex) {
         return Unspecified;
      }
   }
}