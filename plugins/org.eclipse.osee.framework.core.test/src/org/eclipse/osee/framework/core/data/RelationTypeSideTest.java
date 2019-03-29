/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class RelationTypeSideTest {

   private static final long uuid1 = 123123421493L;
   private static final long uuid2 = 867847363221L;
   private static final RelationSide sideA = RelationSide.SIDE_A;
   private static final RelationSide sideB = RelationSide.SIDE_B;

   @Test
   public void testHashCodeEquals() {
      IRelationType relType1 = RelationTypeToken.create(uuid1, "X");
      RelationTypeSide relTypeSide1 = RelationTypeSide.create(sideA, uuid1, uuid1 + "_sideA");

      Assert.assertTrue(relType1.equals(relTypeSide1));
      Assert.assertTrue(relTypeSide1.equals(relType1));

      Assert.assertEquals(relType1.hashCode(), relTypeSide1.hashCode());
   }

   @Test
   public void testMap() {
      RelationTypeSide relTypeSide1 = RelationTypeSide.create(sideA, uuid1, uuid1 + "_sideA");
      RelationTypeSide relTypeSide2 = RelationTypeSide.create(sideB, uuid1, uuid1 + "_sideB");
      RelationTypeSide relTypeSide3 = RelationTypeSide.create(sideA, uuid2, uuid2 + "_sideA");
      RelationTypeSide relTypeSide4 = RelationTypeSide.create(sideB, uuid2, uuid2 + "_sideB");

      Map<IRelationType, String> data = new HashMap<>();
      data.put(relTypeSide1, "a1");
      data.put(relTypeSide2, "b2");
      data.put(relTypeSide3, "c3");
      data.put(relTypeSide4, "d4");

      Assert.assertEquals(4, data.size());

      Assert.assertEquals("a1", data.get(relTypeSide1));
      Assert.assertEquals("b2", data.get(relTypeSide2));
      Assert.assertEquals("c3", data.get(relTypeSide3));
      Assert.assertEquals("d4", data.get(relTypeSide4));

      IRelationType relType1 = RelationTypeSide.create(sideB, uuid1, "");
      String removed = data.put(relType1, "hello");
      Assert.assertEquals("b2", removed);

      String value = data.get(relType1);
      Assert.assertEquals("hello", value);

      Assert.assertEquals("a1", data.get(relTypeSide1));

      Assert.assertEquals(4, data.size());
   }
}