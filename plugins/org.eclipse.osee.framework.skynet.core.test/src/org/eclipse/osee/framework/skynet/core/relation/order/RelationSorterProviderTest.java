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
package org.eclipse.osee.framework.skynet.core.relation.order;

import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_ASC;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_DESC;
import static org.eclipse.osee.framework.core.enums.RelationSorter.UNORDERED;
import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.relation.sorters.LexicographicalRelationSorter;
import org.eclipse.osee.framework.skynet.core.relation.sorters.UnorderedRelationSorter;
import org.eclipse.osee.framework.skynet.core.relation.sorters.UserDefinedRelationSorter;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Roberto E. Escobar
 */
public class RelationSorterProviderTest {
   @Rule
   public ExpectedException thrown = ExpectedException.none();

   @Test
   public void testGetAllRelationOrderIds() {
      RelationSorterProvider provider = new RelationSorterProvider();
      List<RelationSorter> actual = provider.getAllRelationOrderIds();

      List<RelationSorter> expected = Arrays.asList(LEXICOGRAPHICAL_ASC, LEXICOGRAPHICAL_DESC, UNORDERED, USER_DEFINED);
      Assert.assertTrue(Collections.isEqual(expected, actual));
   }

   @Test
   public void testGetRelationOrder() {
      RelationSorterProvider provider = new RelationSorterProvider();

      testSorter(provider, RelationSorter.LEXICOGRAPHICAL_ASC, LexicographicalRelationSorter.class);
      testSorter(provider, RelationSorter.LEXICOGRAPHICAL_DESC, LexicographicalRelationSorter.class);
      testSorter(provider, RelationSorter.UNORDERED, UnorderedRelationSorter.class);
      testSorter(provider, RelationSorter.USER_DEFINED, UserDefinedRelationSorter.class);

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("No sorted is defined for preexisting (nor should there be).");
      testSorter(provider, RelationSorter.PREEXISTING, UserDefinedRelationSorter.class);
   }

   private void testSorter(RelationSorterProvider provider, RelationSorter sorterId, Class<? extends IRelationSorter> clazz) {
      IRelationSorter actual = provider.getRelationOrder(sorterId);
      Assert.assertEquals(sorterId, actual.getSorterId());
      Assert.assertEquals(clazz, actual.getClass());
   }
}