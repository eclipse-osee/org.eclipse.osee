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
package org.eclipse.osee.framework.skynet.core.test.relation.order;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.relation.order.IRelationSorterId;
import org.eclipse.osee.framework.skynet.core.relation.order.IRelationSorter;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderBaseTypes;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationSorterProvider;
import org.eclipse.osee.framework.skynet.core.relation.sorters.LexicographicalRelationSorter;
import org.eclipse.osee.framework.skynet.core.relation.sorters.UnorderedRelationSorter;
import org.eclipse.osee.framework.skynet.core.relation.sorters.UserDefinedRelationSorter;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class RelationSorterProviderTest {

   @Test
   public void testGetAllRelationOrderIds() {
      RelationSorterProvider provider = new RelationSorterProvider();
      List<IRelationSorterId> actual = provider.getAllRelationOrderIds();

      List<IRelationSorterId> expected =
            Collections.castAll(IRelationSorterId.class, Arrays.asList(RelationOrderBaseTypes.values()));
      Assert.assertTrue(Collections.isEqual(expected, actual));
   }

   @Test
   public void testGetRelationOrder() throws OseeCoreException {
      RelationSorterProvider provider = new RelationSorterProvider();
      for (RelationOrderBaseTypes baseType : RelationOrderBaseTypes.values()) {
         IRelationSorter actual = provider.getRelationOrder(baseType.getGuid());
         Assert.assertEquals(baseType, actual.getSorterId());
         boolean matches = false;
         switch (baseType) {
            case LEXICOGRAPHICAL_ASC:
               matches = actual instanceof LexicographicalRelationSorter;
               break;
            case LEXICOGRAPHICAL_DESC:
               matches = actual instanceof LexicographicalRelationSorter;
               break;
            case UNORDERED:
               matches = actual instanceof UnorderedRelationSorter;
               break;
            case USER_DEFINED:
               matches = actual instanceof UserDefinedRelationSorter;
               break;
            default:
               Assert.assertNull("This line should not be reached");
               break;
         }
         Assert.assertTrue(matches);
      }
   }

   @Test
   public void testArgumentExceptions() throws OseeCoreException {
      RelationSorterProvider provider = new RelationSorterProvider();
      try {
         provider.getRelationOrder("ABC");
         Assert.assertNull("This line should not be reached");
      } catch (Exception ex) {
         Assert.assertTrue(ex instanceof OseeArgumentException);
      }
   }

   @Test
   public void testNotFoundExceptions() throws OseeCoreException {
      RelationSorterProvider provider = new RelationSorterProvider();
      String randomGuid = GUID.create();
      try {
         provider.getRelationOrder(randomGuid);
         Assert.assertNull("This line should not be reached");
      } catch (Exception ex) {
         Assert.assertTrue(ex instanceof OseeCoreException);
      }
   }
}
