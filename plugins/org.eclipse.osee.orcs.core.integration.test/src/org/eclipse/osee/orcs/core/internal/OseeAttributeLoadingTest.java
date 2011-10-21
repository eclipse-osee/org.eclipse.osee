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
package org.eclipse.osee.orcs.core.internal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OseeApi;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.db.mock.OseeDatabase;
import org.eclipse.osee.orcs.db.mock.OsgiUtil;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.ResultSet;
import org.junit.Rule;

/**
 * @author Jeff C. Phillips
 */
public class OseeAttributeLoadingTest {

   @Rule
   public OseeDatabase db = new OseeDatabase("osee.demo.h2");

   @org.junit.Test
   public void testAttributeLoading() throws Exception {
      OseeApi oseeApi = OsgiUtil.getService(OseeApi.class);

      ApplicationContext context = null; // TODO use real application context

      QueryFactory queryFactory = oseeApi.getQueryFactory(context);
      QueryBuilder builder = queryFactory.fromBranch(CoreBranches.COMMON).andLocalIds(Arrays.asList(7, 8, 9));
      ResultSet<ReadableArtifact> resultSet = builder.build(LoadLevel.FULL);
      List<ReadableArtifact> moreArts = resultSet.getList();

      Assert.assertEquals(3, moreArts.size());
      Assert.assertEquals(3, builder.getCount());

      Map<Integer, ReadableArtifact> lookup = creatLookup(moreArts);
      ReadableArtifact art7 = lookup.get(7);
      ReadableArtifact art8 = lookup.get(8);
      ReadableArtifact art9 = lookup.get(9);

      //Test loading name attributes
      Assert.assertEquals(art7.getSoleAttributeAsString(CoreAttributeTypes.Name),
         "org.eclipse.osee.ats.config.demo.OseeTypes_Demo");
      Assert.assertEquals(art8.getSoleAttributeAsString(CoreAttributeTypes.Name), "User Groups");
      Assert.assertEquals(art9.getSoleAttributeAsString(CoreAttributeTypes.Name), "Everyone");

      //Test boolean attributes
      Assert.assertEquals(art9.getSoleAttributeAsString(CoreAttributeTypes.DefaultGroup), "yes");
   }

   Map<Integer, ReadableArtifact> creatLookup(List<ReadableArtifact> arts) {
      Map<Integer, ReadableArtifact> lookup = new HashMap<Integer, ReadableArtifact>();
      for (ReadableArtifact artifact : arts) {
         lookup.put(artifact.getId(), artifact);
      }
      return lookup;
   }
}
