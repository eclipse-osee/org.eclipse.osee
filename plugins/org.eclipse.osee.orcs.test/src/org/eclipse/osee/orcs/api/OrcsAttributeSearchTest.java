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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsIntegrationRule;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.db.mock.OseeDatabase;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.search.CaseType;
import org.eclipse.osee.orcs.search.Operator;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.StringOperator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Jeff C. Phillips
 */
public class OrcsAttributeSearchTest {

   @Rule
   public OrcsIntegrationRule osgi = new OrcsIntegrationRule(this);

   @Rule
   public OseeDatabase db1 = new OseeDatabase("osee.demo.h2");

   @OsgiService
   private OrcsApi orcsApi;

   private QueryFactory queryFactory;

   @Before
   public void setup() {
      ApplicationContext context = null; // TODO use real application context
      queryFactory = orcsApi.getQueryFactory(context);
   }

   @Test
   public void testNameAttributeNotEqualSearch() throws OseeCoreException {
      QueryBuilder builder =
         queryFactory.fromBranch(CoreBranches.COMMON).and(CoreAttributeTypes.Name, Operator.NOT_EQUAL, "User Groups");

      ResultSet<ArtifactReadable> resultSet = builder.getResults();
      List<ArtifactReadable> moreArts = resultSet.getList();

      for (ArtifactReadable artifact : moreArts) {
         Assert.assertTrue(artifact.getLocalId() != 8);
      }
   }

   @Test
   public void testNameAttributeEqualSearch() throws OseeCoreException {
      QueryBuilder builder =
         queryFactory.fromBranch(CoreBranches.COMMON).and(CoreAttributeTypes.Name, Operator.EQUAL, "User Groups");

      ResultSet<ArtifactReadable> resultSet = builder.getResults();
      List<ArtifactReadable> moreArts = resultSet.getList();

      Assert.assertEquals(1, moreArts.size());
      Assert.assertEquals(1, builder.getCount());

      Map<Integer, ArtifactReadable> lookup = creatLookup(moreArts);
      ArtifactReadable art8 = lookup.get(8);

      //Test loading name attributes
      Assert.assertEquals(art8.getSoleAttributeAsString(CoreAttributeTypes.Name), "User Groups");
   }

   @Test
   public void testWTCAttributeEqualSearch() throws OseeCoreException {
      BranchCache branchCache = orcsApi.getBranchCache();
      Branch branch = branchCache.getBySoleName("SAW_Bld_1");
      QueryBuilder builder =
         queryFactory.fromBranch(branch).and(CoreAttributeTypes.WordTemplateContent,
            StringOperator.TOKENIZED_ANY_ORDER, CaseType.IGNORE_CASE, "commands");

      ResultSet<ArtifactReadable> resultSet = builder.getResults();
      List<ArtifactReadable> moreArts = resultSet.getList();

      Assert.assertFalse(moreArts.isEmpty());
      Assert.assertEquals(3, moreArts.size());
      Assert.assertEquals(3, builder.getCount());
   }

   @Test
   public void testBooleanAttributeSearch() throws OseeCoreException {
      QueryBuilder builder =
         queryFactory.fromBranch(CoreBranches.COMMON).and(CoreAttributeTypes.DefaultGroup, Operator.EQUAL, "true");
      ResultSet<ArtifactReadable> resultSet = builder.getResults();
      List<ArtifactReadable> moreArts = resultSet.getList();

      Assert.assertEquals(1, moreArts.size());
      Assert.assertEquals(1, builder.getCount());

      Map<Integer, ArtifactReadable> lookup = creatLookup(moreArts);
      ArtifactReadable art9 = lookup.get(9);
      Assert.assertEquals(art9.getSoleAttributeAsString(CoreAttributeTypes.Name), "Everyone");
   }

   private Map<Integer, ArtifactReadable> creatLookup(List<ArtifactReadable> arts) {
      Map<Integer, ArtifactReadable> lookup = new HashMap<Integer, ArtifactReadable>();
      for (ArtifactReadable artifact : arts) {
         lookup.put(artifact.getLocalId(), artifact);
      }
      return lookup;
   }
}
