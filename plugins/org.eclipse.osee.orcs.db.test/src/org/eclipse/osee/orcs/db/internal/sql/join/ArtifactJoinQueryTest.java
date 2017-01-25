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
package org.eclipse.osee.orcs.db.internal.sql.join;

import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link ArtifactJoinQuery}
 *
 * @author Roberto E. Escobar
 */
public class ArtifactJoinQueryTest {

   @Test
   public void testAdd() throws OseeCoreException {
      MockJoinAccessor joinAccessor = new MockJoinAccessor();
      Id4JoinQuery join = new Id4JoinQuery(joinAccessor, -1L, 999, 10);
      Assert.assertEquals(0, join.size());
      Assert.assertEquals(true, join.isEmpty());

      join.add(BranchId.valueOf(5678L), ArtifactId.valueOf(1234));
      Assert.assertEquals(1, join.size());
      Assert.assertEquals(false, join.isEmpty());

      join.add(BranchId.valueOf(5678L), ArtifactId.valueOf(1234));

      Assert.assertEquals(1, join.size());

      Assert.assertEquals(false, join.wasStored());
      join.store();
      Assert.assertEquals(true, join.wasStored());

      Assert.assertNull(joinAccessor.getConnection());
      Assert.assertEquals(999, joinAccessor.getQueryId());

      List<Object[]> data = joinAccessor.getDataList();
      Assert.assertEquals(1, data.size());

      Object[] entry = data.get(0);
      Assert.assertEquals(5, entry.length);
      Assert.assertEquals(999, entry[0]);
      Assert.assertEquals(ArtifactId.valueOf(1234), entry[2]);
      Assert.assertEquals(BranchId.valueOf(5678L), entry[1]);

   }

   @Test(expected = OseeCoreException.class)
   public void testStoreTwice() throws OseeCoreException {
      MockJoinAccessor joinAccessor = new MockJoinAccessor();
      Id4JoinQuery join = new Id4JoinQuery(joinAccessor, -1L, 1000, 10);

      Assert.assertEquals(false, join.wasStored());
      join.store();
      Assert.assertEquals(true, join.wasStored());

      Assert.assertNull(joinAccessor.getConnection());
      Assert.assertEquals(1000, joinAccessor.getQueryId());

      join.store();
   }

   @Test(expected = OseeCoreException.class)
   public void testMoreThanAllowed() throws OseeCoreException {
      MockJoinAccessor joinAccessor = new MockJoinAccessor();
      int maxSize = 5;
      Id4JoinQuery join = new Id4JoinQuery(joinAccessor, -1L, 1000, maxSize);

      for (int i = 0; i < maxSize + 1; i++) {
         join.add(ArtifactId.valueOf(i + 1), BranchId.valueOf(1123L));
      }

   }
}
