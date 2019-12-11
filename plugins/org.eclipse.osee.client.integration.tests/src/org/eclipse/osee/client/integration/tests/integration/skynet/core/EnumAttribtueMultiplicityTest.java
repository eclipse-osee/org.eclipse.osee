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
package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import java.util.Set;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttribtueMultiplicityResolver;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeMultiplicitySelectionOption;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jeremy A. Midvidy
 */
public class EnumAttribtueMultiplicityTest {

   private Artifact artifact;

   @Before
   public void setup() {
      artifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.WorkFlowDefinition, CoreBranches.COMMON);

      // Currently tests on strictly Enums, but should hold for all attr types
      // with different multiplicities

      // 0 ... 1 :: Singelton, Removable
      artifact.addAttribute(CoreAttributeTypes.TestProcedureStatus);

      // 0 ... inf :: Non-Singelton, Removable
      artifact.addAttribute(CoreAttributeTypes.DoorsId);

      // 1 ... 1 :: Singelton, Not-Removable
      artifact.addAttribute(CoreAttributeTypes.FeatureValueType);

      // 1 ... inf :: Not-Singelton, Not-Removable
      artifact.addAttribute(CoreAttributeTypes.Partition);
   }

   @Test
   public void enumAttrMultiplicityTest() {
      AttribtueMultiplicityResolver resolver;
      for (Attribute<?> attr : artifact.getAttributes()) {
         // core test logic
         AttributeTypeId attrIdToken = attr.getAttributeTypeToken();
         assertTrue("attrIdToken is null", attr != null);

         resolver = new AttribtueMultiplicityResolver(attr.getAttributeTypeToken());
         HashMap<AttributeMultiplicitySelectionOption, Boolean> optionMap =
            AttributeMultiplicitySelectionOption.getOptionMap();

         int minVal = AttributeTypeManager.getMinOccurrences(attrIdToken);
         int maxVal = AttributeTypeManager.getMaxOccurrences(attrIdToken);
         boolean isSingelton = resolver.isSingeltonAttribute();
         boolean isRemovalAllowed = resolver.isRemovalAllowed();

         if (minVal == 0 && maxVal == 0) { // 0 ... 0 :: trival case
            assertTrue(isSingelton == false);
            assertTrue(isRemovalAllowed == false);
         } else if (minVal == 0 && maxVal == 1) { // 0 ... 1 :: Singelton, Removable
            assertTrue(isSingelton == true);
            assertTrue(isRemovalAllowed == true);
            optionMap.put(AttributeMultiplicitySelectionOption.AddSelection, true);
         } else if (minVal == 0 && maxVal == Integer.MAX_VALUE) { // 0 ... inf :: Non-Singelton, Removable
            assertTrue(isSingelton == false);
            assertTrue(isRemovalAllowed == true);
            for (AttributeMultiplicitySelectionOption key : optionMap.keySet()) {
               optionMap.put(key, true);
            }
         } else if (minVal == 1 && maxVal == 1) { // 1 ... 1 :: Singelton, Not-Removable
            assertTrue(isSingelton == true);
            assertTrue(isRemovalAllowed == false);
            optionMap.put(AttributeMultiplicitySelectionOption.ReplaceAll, true);
         } else if (minVal == 1 && maxVal == Integer.MAX_VALUE) { // 1 ... inf :: Not-Singelton, Not-Removable
            assertTrue(isSingelton == false);
            assertTrue(isRemovalAllowed == false);
            optionMap.put(AttributeMultiplicitySelectionOption.AddSelection, true);
            optionMap.put(AttributeMultiplicitySelectionOption.ReplaceAll, true);
         }

         Set<AttributeMultiplicitySelectionOption> selOptions = resolver.getSelectionOptions();
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
