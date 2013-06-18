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
package org.eclipse.osee.orcs.api;

import static org.eclipse.osee.orcs.OrcsIntegrationRule.integrationRule;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.Operator;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.TestRule;

/**
 * @author Jeff C. Phillips
 */
public class OrcsAttributeLoadingTest {

   @Rule
   public TestRule osgi = integrationRule(this, "osee.demo.hsql");

   @OsgiService
   private OrcsApi orcsApi;

   @org.junit.Test
   public void testAttributeLoading() throws Exception {
      ApplicationContext context = null; // TODO use real application context

      QueryFactory queryFactory = orcsApi.getQueryFactory(context);
      QueryBuilder builder = queryFactory.fromBranch(CoreBranches.COMMON).andLocalIds(Arrays.asList(6, 7, 8));
      ResultSet<ArtifactReadable> resultSet = builder.getResults();
      List<ArtifactReadable> moreArts = resultSet.getList();

      Assert.assertEquals(3, moreArts.size());
      Assert.assertEquals(3, builder.getCount());

      Map<Integer, ArtifactReadable> lookup = creatLookup(moreArts);
      ArtifactReadable art6 = lookup.get(6);
      ArtifactReadable art7 = lookup.get(7);
      ArtifactReadable art8 = lookup.get(8);

      //Test loading name attributes
      Assert.assertEquals(art6.getSoleAttributeAsString(CoreAttributeTypes.Name),
         "org.eclipse.osee.coverage.OseeTypes_Coverage");
      Assert.assertEquals(art7.getSoleAttributeAsString(CoreAttributeTypes.Name), "User Groups");
      Assert.assertEquals(art8.getSoleAttributeAsString(CoreAttributeTypes.Name), "Everyone");

      //Test boolean attributes
      Assert.assertEquals(art8.getSoleAttributeAsString(CoreAttributeTypes.DefaultGroup), "true");

      //Load WTC attributes
      loadWordTemplateContentAttributes(queryFactory, orcsApi.getBranchCache());
   }

   private void loadWordTemplateContentAttributes(QueryFactory queryFactory, BranchCache branchCache) throws OseeCoreException {
      QueryBuilder builder =
         queryFactory.fromBranch(branchCache.getByName("SAW_Bld_1").iterator().next()).and(CoreAttributeTypes.Name,
            Operator.EQUAL, "Haptic Constraints");

      ResultSet<ArtifactReadable> resultSet = builder.getResults();
      List<ArtifactReadable> moreArts = resultSet.getList();

      Assert.assertFalse(moreArts.isEmpty());
      Assert.assertTrue(builder.getCount() > 0);

      ArtifactReadable artifact = moreArts.iterator().next();
      Assert.assertTrue(artifact.getSoleAttributeAsString(CoreAttributeTypes.WordTemplateContent).length() > 2);
   }

   Map<Integer, ArtifactReadable> creatLookup(List<ArtifactReadable> arts) {
      Map<Integer, ArtifactReadable> lookup = new HashMap<Integer, ArtifactReadable>();
      for (ArtifactReadable artifact : arts) {
         lookup.put(artifact.getLocalId(), artifact);
      }
      return lookup;
   }
}
