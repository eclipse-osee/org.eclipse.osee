/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.data;

import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Allocation_Component;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Allocation_Requirement;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.DefaultHierarchical;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.DefaultHierarchical_Child;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.DefaultHierarchical_Parent;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class RelationTypeSideTest {

   @Test
   public void testHashCodeEquals() {
      RelationTypeToken relType1 = DefaultHierarchical;
      RelationTypeSide relTypeSide1 = DefaultHierarchical_Parent;

      Assert.assertTrue(relType1.equals(relTypeSide1));
      Assert.assertTrue(relTypeSide1.equals(relType1));

      Assert.assertEquals(relType1.hashCode(), relTypeSide1.hashCode());
   }

   @Test
   public void testMap() {
      RelationTypeSide relTypeSide1 = DefaultHierarchical_Parent;
      RelationTypeSide relTypeSide2 = DefaultHierarchical_Child;
      RelationTypeSide relTypeSide3 = Allocation_Requirement;
      RelationTypeSide relTypeSide4 = Allocation_Component;

      Map<RelationTypeToken, String> data = new HashMap<>();
      data.put(relTypeSide1, "a1");
      data.put(relTypeSide2, "b2");
      data.put(relTypeSide3, "c3");
      data.put(relTypeSide4, "d4");

      Assert.assertEquals(4, data.size());

      Assert.assertEquals("a1", data.get(relTypeSide1));
      Assert.assertEquals("b2", data.get(relTypeSide2));
      Assert.assertEquals("c3", data.get(relTypeSide3));
      Assert.assertEquals("d4", data.get(relTypeSide4));

      RelationTypeToken relType1 = DefaultHierarchical_Child;
      String removed = data.put(relType1, "hello");
      Assert.assertEquals("b2", removed);

      String value = data.get(relType1);
      Assert.assertEquals("hello", value);

      Assert.assertEquals("a1", data.get(relTypeSide1));

      Assert.assertEquals(4, data.size());
   }
}