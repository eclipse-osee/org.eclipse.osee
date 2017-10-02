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

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.db.mock.OrcsIntegrationByClassRule;
import org.eclipse.osee.orcs.db.mock.OseeClassDatabase;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * @author Jeff C. Phillips
 */
public class OrcsAttributeSearchTest {

   @Rule
   public TestRule db = OrcsIntegrationByClassRule.integrationRule(this);

   @OsgiService
   private OrcsApi orcsApi;

   private QueryFactory queryFactory;

   @Before
   public void setup() {
      queryFactory = orcsApi.getQueryFactory();
   }

   @AfterClass
   public static void cleanup() throws Exception {
      OseeClassDatabase.cleanup();
   }

   @Test
   public void testNameAttributeEqualSearch()  {
      QueryBuilder builder = queryFactory.fromBranch(COMMON).and(CoreAttributeTypes.Name, "User Groups");

      ResultSet<ArtifactReadable> resultSet = builder.getResults();

      assertEquals(1, resultSet.size());
      assertEquals(1, builder.getCount());

      ArtifactReadable art7 = resultSet.getExactlyOne();

      //Test loading name attributes
      assertEquals(art7.getSoleAttributeAsString(CoreAttributeTypes.Name), "User Groups");
   }

   @Test
   public void testWTCAttributeEqualSearch()  {
      QueryBuilder builder = queryFactory.fromBranch(SAW_Bld_1).and(CoreAttributeTypes.WordTemplateContent, "commands",
         QueryOption.TOKEN_DELIMITER__ANY, QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.CASE__IGNORE,
         QueryOption.TOKEN_COUNT__IGNORE);

      assertEquals(3, builder.getCount());

      ResultSet<ArtifactReadable> resultSet = builder.getResults();

      assertFalse(resultSet.isEmpty());
      assertEquals(3, resultSet.size());
   }

   @Test
   public void testBooleanAttributeSearch()  {
      QueryBuilder builder = queryFactory.fromBranch(COMMON).and(CoreAttributeTypes.DefaultGroup, "true");
      ResultSet<ArtifactReadable> resultSet = builder.getResults();

      assertEquals(1, resultSet.size());
      assertEquals(1, builder.getCount());

      ArtifactReadable art8 = resultSet.getExactlyOne();
      assertEquals(art8.getSoleAttributeAsString(CoreAttributeTypes.Name), "Everyone");
   }

   @Test
   public void testNullAttributeSearch() {
      QueryBuilder builder = queryFactory.fromBranch(COMMON).and(CoreAttributeTypes.Email, (String) null);
      ResultSet<ArtifactReadable> resultSet = builder.getResults();
      assertEquals(8, resultSet.size());

      builder = queryFactory.fromBranch(COMMON).and(CoreAttributeTypes.Email, "");
      resultSet = builder.getResults();
      assertEquals(8, resultSet.size());

      builder = queryFactory.fromBranch(COMMON).and(CoreAttributeTypes.Email, " ");
      resultSet = builder.getResults();
      assertEquals(0, resultSet.size());
   }

}
