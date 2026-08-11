/*********************************************************************
 * Copyright (c) 2021 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.integration.tests.ats.query;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.resource.AbstractRestTest;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreBranchCategoryTokens;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test unit for {@link AtsProductLineEndpointImpl}
 *
 * @author Audrey Denk
 */
public class AtsProductLineEndpointImplTest extends AbstractRestTest {
   private final JaxRsApi jaxRsApi = AtsApiService.get().jaxRsApi();

   @Test
   public void testAtsPleBranchesRestCall() {

      String path = "ats/ple/branches";
      WebTarget target = jaxRsApi.newTargetQuery(path, "type", BranchType.BASELINE.getIdString(), "category",
         CoreBranchCategoryTokens.PLE.getIdString());
      testActionRestCall(target, 6);
   }

   private void testActionRestCall(WebTarget target, int size) {
      String json = getJson(target);
      JsonNode arrayNode = jaxRsApi.readTree(json);
      Assert.assertEquals(size, arrayNode.size());
   }

   @Test
   public void testBranchesContentsAccessControl() {
      StringBuilder sb = new StringBuilder();
      sb.append("orcs/branches/").append(DemoBranches.SAW_Bld_1.getIdString()).append("/permission/").append(
         PermissionEnum.FULLACCESS.name()).append("/").append(DemoUsers.Joe_Smith.getIdString());

      WebTarget targetInit = jaxRsApi.newTarget(sb.toString());
      targetInit.request().post(Entity.text(""));

      String path = "ats/ple/branches";
      WebTarget target = jaxRsApi.newTargetQuery(path, "type", BranchType.BASELINE.getIdString());
      testGetAllBranchesResult(target);
   }

   private void testGetAllBranchesResult(WebTarget target) {

      String json = getJson(target);
      JsonNode arrayNode = jaxRsApi.readTree(json);
      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // shortName won't deserialize
      List<Branch> branches = mapper.convertValue(arrayNode, new TypeReference<List<Branch>>() { //
      });
      for (Branch branch : branches) {
         if (DemoBranches.SAW_Bld_1.getName().equals(branch.getName())) {
            // check Joe Smith has access set as above
            Assert.assertEquals(branch.getCurrentUserPermission(), PermissionEnum.FULLACCESS);
         }
         if (DemoBranches.SAW_PL.getName().equals(branch.getName())) {
            // Everyone has READ Access
            Assert.assertEquals(branch.getCurrentUserPermission(), PermissionEnum.READ);
         }
      }
   }

   /**
    * Verifies that paginated branch queries with includeCategories produce no duplicate branches. The bug was that the
    * category LEFT JOIN produced duplicate rows, and row_number() was applied before DISTINCT, causing pagination to
    * operate on the duplicated set — returning fewer unique branches than expected per page.
    */
   @Test
   public void testPaginatedBranchesWithCategoriesNoDuplicates() {
      // Request page 1 with a small page size — this exercises the pagination + categories path
      int pageSize = 3;
      String path = "ats/ple/branches";
      WebTarget target = jaxRsApi.newTargetQuery(path, "type", BranchType.BASELINE.getIdString(), "pageNum", "1",
         "count", String.valueOf(pageSize));
      String json = getJson(target);
      JsonNode arrayNode = jaxRsApi.readTree(json);
      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      List<Branch> page1 = mapper.convertValue(arrayNode, new TypeReference<List<Branch>>() { //
      });

      // Verify no duplicates within the page
      Set<Long> branchIds = new HashSet<>();
      for (Branch branch : page1) {
         Assert.assertTrue("Duplicate branch in paginated result: " + branch.getName(),
            branchIds.add(branch.getId()));
      }

      // Verify page size is respected (should return at most pageSize branches)
      Assert.assertTrue("Page should have at most " + pageSize + " branches", page1.size() <= pageSize);

      // If there are more branches, get page 2 and confirm no overlap with page 1
      if (page1.size() == pageSize) {
         WebTarget target2 = jaxRsApi.newTargetQuery(path, "type", BranchType.BASELINE.getIdString(), "pageNum", "2",
            "count", String.valueOf(pageSize));
         String json2 = getJson(target2);
         JsonNode arrayNode2 = jaxRsApi.readTree(json2);
         List<Branch> page2 = mapper.convertValue(arrayNode2, new TypeReference<List<Branch>>() { //
         });

         for (Branch branch : page2) {
            Assert.assertFalse("Branch from page 2 already appeared in page 1: " + branch.getName(),
               branchIds.contains(branch.getId()));
         }
      }
   }
}
