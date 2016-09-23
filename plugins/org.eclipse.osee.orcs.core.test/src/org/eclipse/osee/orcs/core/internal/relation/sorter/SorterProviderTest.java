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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import org.eclipse.osee.framework.core.data.RelationSorter;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.data.RelationTypes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link SorterProvider}
 *
 * @author Roberto E. Escobar
 */
public class SorterProviderTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private RelationTypes relationTypeCache;
   // @formatter:on

   private SorterProvider provider;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);

      provider = new SorterProvider(relationTypeCache);
   }

   @Test
   public void testGetDefaultSorterId() throws OseeCoreException {
      when(relationTypeCache.getDefaultOrderTypeGuid(CoreRelationTypes.Default_Hierarchical__Child)).thenReturn(
         RelationOrderBaseTypes.USER_DEFINED);

      RelationSorter actual1 = provider.getDefaultSorterId(CoreRelationTypes.Default_Hierarchical__Child);
      assertEquals(RelationOrderBaseTypes.USER_DEFINED, actual1);

      when(relationTypeCache.getDefaultOrderTypeGuid(CoreRelationTypes.Users_User)).thenReturn(
         RelationOrderBaseTypes.LEXICOGRAPHICAL_DESC);

      RelationSorter actual2 = provider.getDefaultSorterId(CoreRelationTypes.Users_User);
      assertEquals(RelationOrderBaseTypes.LEXICOGRAPHICAL_DESC, actual2);
   }

   @Test
   public void testGetDefaultSorterIdNull() throws OseeCoreException {
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