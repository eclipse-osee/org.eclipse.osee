/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.framework.core.enums;

import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.enums.token.PartitionAttributeType.PartitionEnum;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link AttributeTypeEnum}
 *
 * @author Donald G. Dunne
 */
public class AttributeTypeEnumTest {

   @Test
   public void testValueFromStorageString() {
      int initialCount = CoreAttributeTypes.Partition.getEnumValues().size();
      Assert.assertEquals(initialCount, CoreAttributeTypes.Partition.getEnumValues().size());
      Assert.assertEquals(initialCount, CoreAttributeTypes.Partition.getEnumStrValues().size());
      PartitionEnum enum1 = CoreAttributeTypes.Partition.valueFromStorageString("New Partition");
      Assert.assertNotNull(enum1);
      // New enum gets next sequential enum id; initialCount since first enum gets 0
      Assert.assertEquals(Long.valueOf(initialCount), enum1.getId());

      // Valid enums should not change
      Assert.assertEquals(initialCount, CoreAttributeTypes.Partition.getEnumValues().size());
      Assert.assertEquals(initialCount, CoreAttributeTypes.Partition.getEnumStrValues().size());

      // Loading same value again should not add another enum, but return same
      PartitionEnum enum2 = CoreAttributeTypes.Partition.valueFromStorageString("New Partition");
      Assert.assertNotNull(enum2);
      Assert.assertEquals(enum1.getId(), enum2.getId());
   }

}
