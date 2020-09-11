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

package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeMultiplicitySelectionOption;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.artifact.EnumSelectionDialog;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jeremy A. Midvidy
 */
public class EnumAttributeMultiplicityTest {

   private Artifact artifact;
   private final Collection<Artifact> artifacts = new ArrayList<Artifact>();

   @Before
   public void setup() {
      artifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.AbstractSpecRequirement, CoreBranches.COMMON);

      // Currently tests on strictly Enums, but should hold for all attr types
      // with different multiplicities

      // 0 ... 1 :: ZeroOROne, Singelton, Removable
      artifact.addAttribute(CoreAttributeTypes.DoorsHierarchy);

      // 0 ... inf :: any :: Non-Singelton, Removable
      artifact.addAttribute(CoreAttributeTypes.DoorsId);

      // 1 ... 1 :: ExactlyOne, Singelton, Not-Removable
      //already exists on artifact creation

      // 1 ... inf :: Not-Singelton, Not-Removable
      //already exists on artifact creation

      artifacts.add(artifact);
   }

   @Test
   public void enumAttrMultiplicityTest() {
      EnumSelectionDialog enumSelection;
      for (Attribute<?> attr : artifact.getAttributes()) {
         // core test logic
         AttributeTypeGeneric<?> attributeTypeGen =
            AttributeTypeManager.getAttributeType(attr.getAttributeType().getId());
         if (!attributeTypeGen.isEnumerated()) {
            continue;
         }
         AttributeTypeEnum<?> attributeType = attributeTypeGen.toEnum();
         assertTrue("attrIdToken is null", attr != null);
         enumSelection = new EnumSelectionDialog(attributeType.toEnum(), artifacts);
         HashMap<AttributeMultiplicitySelectionOption, Boolean> optionMap =
            AttributeMultiplicitySelectionOption.getOptionMap();
         int minVal = artifact.getArtifactType().getMin(attributeType);
         int maxVal = artifact.getArtifactType().getMax(attributeType);
         boolean isSingelton = EnumSelectionDialog.isSingletonAttribute(attributeType, artifacts);
         boolean isRemovalAllowed = enumSelection.isRemovalAllowed();

         if (minVal == 0 && maxVal == 1) { // 0 ... 1 :: Singelton, Removable
            assertTrue(isRemovalAllowed == true);
            optionMap.put(AttributeMultiplicitySelectionOption.AddSelection, true);
         } else if (minVal == 0 && maxVal == Integer.MAX_VALUE) { // 0 ... inf :: Non-Singelton, Removable
            assertTrue(isRemovalAllowed == true);
            for (AttributeMultiplicitySelectionOption key : optionMap.keySet()) {
               optionMap.put(key, true);
            }
         } else if (minVal == 1 && maxVal == 1) { // 1 ... 1 :: Singelton, Not-Removable
            assertTrue(isRemovalAllowed == false);
            optionMap.put(AttributeMultiplicitySelectionOption.ReplaceAll, true);
         } else if (minVal == 1 && maxVal == Integer.MAX_VALUE) { // 1 ... inf :: Not-Singelton, Not-Removable
            assertTrue(isRemovalAllowed == false);
            optionMap.put(AttributeMultiplicitySelectionOption.AddSelection, true);
            optionMap.put(AttributeMultiplicitySelectionOption.ReplaceAll, true);
         }

         Set<AttributeMultiplicitySelectionOption> selOptions = enumSelection.getSelectionOptions();
         if (!isSingelton && optionMap.get(AttributeMultiplicitySelectionOption.AddSelection).equals(true)) {
            assertTrue(selOptions.contains(AttributeMultiplicitySelectionOption.AddSelection));
         }
         if (!isSingelton && optionMap.get(AttributeMultiplicitySelectionOption.DeleteSelected).equals(true)) {
            assertTrue(selOptions.contains(AttributeMultiplicitySelectionOption.DeleteSelected));
         }
         if (optionMap.get(AttributeMultiplicitySelectionOption.ReplaceAll).equals(true)) {
            assertTrue(selOptions.contains(AttributeMultiplicitySelectionOption.ReplaceAll));
         }
         if (isRemovalAllowed && optionMap.get(AttributeMultiplicitySelectionOption.RemoveAll).equals(true)) {
            assertTrue(selOptions.contains(AttributeMultiplicitySelectionOption.RemoveAll));
         }
      }
   }

   @After
   public void tearDown() {
      if (artifact != null) {
         artifact.deleteAndPersist();
      }
   }

}
