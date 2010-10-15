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
package org.eclipse.osee.framework.search.engine.test.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.search.engine.utility.SearchTagQueryBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link SearchTagQueryBuilder}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class SearchTagQueryBuilderTest {

   private final int numberOfItems;
   private final boolean useAttributeTypeJoin;
   private final int branchId;
   private final boolean includeDeleted;

   public SearchTagQueryBuilderTest(int numberOfItems, boolean useAttributeTypeJoin, int branchId, boolean includeDeleted) {
      super();
      this.numberOfItems = numberOfItems;
      this.useAttributeTypeJoin = useAttributeTypeJoin;
      this.branchId = branchId;
      this.includeDeleted = includeDeleted;
   }

   @Test
   public void testCase() {
      SearchTagQueryBuilder builder = new SearchTagQueryBuilder();
      String actualQuery = builder.getQuery(numberOfItems, useAttributeTypeJoin, branchId, includeDeleted);
      String expectedQuery = createExpected(numberOfItems, useAttributeTypeJoin, branchId, includeDeleted);
      Assert.assertEquals(expectedQuery, actualQuery);
   }

   @Parameters
   public static Collection<Object[]> data() {
      List<Object[]> data = new ArrayList<Object[]>();

      data.add(new Object[] {1, true, 2, false});
      data.add(new Object[] {1, false, -1, false});
      data.add(new Object[] {1, true, -1, false});
      data.add(new Object[] {1, false, -1, true});

      data.add(new Object[] {3, false, 2, false});
      data.add(new Object[] {3, true, 2, false});
      data.add(new Object[] {3, false, -1, false});
      data.add(new Object[] {3, true, -1, false});
      data.add(new Object[] {3, false, -1, true});

      data.add(new Object[] {4, true, 2, false});
      data.add(new Object[] {4, false, -1, false});
      data.add(new Object[] {4, true, 6, false});
      data.add(new Object[] {4, false, -1, true});
      return data;
   }

   private static String createExpected(int count, boolean hasJoin, int branchId, boolean includeDeleted) {
      StringBuilder builder = new StringBuilder();
      builder.append("SELECT  /*+ ordered FIRST_ROWS */ attr1.art_id, attr1.gamma_id, attr1.value, attr1.uri, attr1.attr_type_id, txs1.branch_id FROM \n");

      for (int index = 0; index < count; index++) {
         builder.append(String.format("osee_search_tags ost%s, \n", index));
      }
      if (hasJoin) {
         builder.append(" osee_join_id idj,");
      }
      builder.append(" osee_attribute attr1, osee_txs txs1 WHERE \n");

      for (int index = 0; index < count; index++) {
         builder.append(String.format("ost%s.coded_tag_id = ? AND\n", index));
      }

      for (int index = 0; index < count - 1; index++) {
         builder.append(String.format("ost%s.gamma_id = ost%s.gamma_id AND \n", index, index + 1));
      }
      builder.append(String.format("ost%s.gamma_id = attr1.gamma_id AND\n", count - 1));
      builder.append(" attr1.gamma_id = txs1.gamma_id\n");

      if (hasJoin) {
         builder.append(" AND attr1.attr_type_id = idj.id AND idj.query_id = ?\n");
      }
      if (branchId > -1) {
         builder.append(" AND txs1.branch_id = ?");
      }
      if (includeDeleted) {
         builder.append(" AND txs1.tx_current IN (1,3)");
      } else {
         builder.append(" AND txs1.tx_current = 1");
      }
      return builder.toString();
   }
}
