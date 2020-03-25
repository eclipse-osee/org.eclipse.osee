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
package org.eclipse.osee.orcs.core.internal.relation.sorter;

import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_ASC;
import static org.eclipse.osee.framework.core.enums.RelationSorter.UNORDERED;
import static org.junit.Assert.assertEquals;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link SorterProvider}
 *
 * @author Roberto E. Escobar
 */
public class SorterProviderTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   private SorterProvider provider;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);

      provider = new SorterProvider();
   }

   @Test
   public void testGetDefaultSorterId() {
      RelationSorter actual1 = provider.getDefaultSorterId(CoreRelationTypes.DefaultHierarchical_Child);
      assertEquals(LEXICOGRAPHICAL_ASC, actual1);

      RelationSorter actual2 = provider.getDefaultSorterId(CoreRelationTypes.Users_User);
      assertEquals(UNORDERED, actual2);
   }

   @Test
   public void testGetDefaultSorterIdNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("type cannot be null");

      provider.getDefaultSorterId(null);
   }

   @Test
   public void testGetRelationOrder() {
      testSorter(provider, RelationSorter.LEXICOGRAPHICAL_ASC, LexicographicalSorter.class);
      testSorter(provider, RelationSorter.LEXICOGRAPHICAL_DESC, LexicographicalSorter.class);
      testSorter(provider, RelationSorter.UNORDERED, UnorderedSorter.class);
      testSorter(provider, RelationSorter.USER_DEFINED, UserDefinedSorter.class);

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("No sorted is defined for preexisting");
      testSorter(provider, RelationSorter.PREEXISTING, UserDefinedSorter.class);
   }

   private void testSorter(SorterProvider provider, RelationSorter sorterId, Class<? extends Sorter> clazz) {
      Sorter actual = provider.getSorter(sorterId);
      Assert.assertEquals(sorterId, actual.getId());
      Assert.assertEquals(clazz, actual.getClass());
   }
}