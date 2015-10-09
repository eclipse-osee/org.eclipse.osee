/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.writer;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON_ID;
import static org.eclipse.osee.orcs.OrcsIntegrationRule.integrationRule;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * Test case for {@link OrcsValidationHelperAdapter}
 *
 * @author Donald G. Dunne
 */
public class OrcsValidationHelperAdapterTest {

   @Rule
   public TestRule osgi = integrationRule(this);

   @OsgiService
   private OrcsApi orcsApi;

   private OrcsValidationHelperAdapter helper;

   @Before
   public void setUp() throws Exception {
      helper = new OrcsValidationHelperAdapter(orcsApi);
   }

   @Test
   public void testIsBranchExists() {
      Assert.assertTrue(helper.isBranchExists(COMMON_ID));

      Assert.assertFalse(helper.isBranchExists(34598L));
   }

   @Test
   public void testIsUserExists() {
      Assert.assertTrue(helper.isUserExists(SystemUser.OseeSystem.getUserId()));

      Assert.assertFalse(helper.isUserExists("notUserId"));
   }

   @Test
   public void testIsArtifactExists() {
      ArtifactReadable artifact = orcsApi.getQueryFactory().fromBranch(COMMON_ID).andIsOfType(
         CoreArtifactTypes.User).getResults().iterator().next();
      Assert.assertTrue(helper.isArtifactExists(COMMON_ID, artifact.getUuid()));

      Assert.assertFalse(helper.isArtifactExists(COMMON_ID, 999999L));
   }

   @Test
   public void testIsArtifactTypeExist() {
      Assert.assertTrue(helper.isArtifactTypeExist(CoreArtifactTypes.User.getGuid()));

      Assert.assertFalse(helper.isArtifactTypeExist(999999L));
   }

   @Test
   public void testIsRelationTypeExist() {
      Assert.assertTrue(helper.isRelationTypeExist(CoreRelationTypes.Default_Hierarchical__Child.getGuid()));

      Assert.assertFalse(helper.isRelationTypeExist(999999L));
   }

   @Test
   public void testIsAttributeTypeExists() {
      Assert.assertTrue(helper.isAttributeTypeExists(CoreAttributeTypes.StaticId.getGuid()));

      Assert.assertFalse(helper.isAttributeTypeExists(999999L));
   }

}
