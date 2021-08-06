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
package org.eclipse.osee.client.integration.tests.integration.orcs.rest;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Component;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeType;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Branden W. Phillips
 */
public class TransactionEndpointTest {

   private static TransactionEndpoint transactionEndpoint;
   private static JaxRsApi jaxRsApi;

   @BeforeClass
   public static void testSetup() {
      OseeClient oseeClient = OsgiUtil.getService(DemoChoice.class, OseeClient.class);
      transactionEndpoint = oseeClient.getTransactionEndpoint();
      jaxRsApi = oseeClient.jaxRsApi();
   }

   /**
    * Simple test for the getArtifactHistory rest call. Checking for the following cases <br/>
    * 1. Artifact changes are being found <br/>
    * 2. Attribute changes are being found <br/>
    * 3. Relation changes are being found <br/>
    * 4. Changes are coming from multiple branches since the query is following recursive branches. We know that the
    * Demo PL branch has parents for this artifact. With this, we want to be sure that multiple branches have been found
    * on the list of changes by checking that the set size is greater than 1 at the end of processing.
    */
   @Test
   public void testGetArtifactHistory() {
      ArtifactId sawProductDecomp = ArtifactQuery.getArtifactFromTypeAndName(Component,
         CoreArtifactTokens.SAW_PRODUCT_DECOMP, DemoBranches.SAW_PL);

      List<ChangeItem> changeItems = transactionEndpoint.getArtifactHistory(sawProductDecomp, DemoBranches.SAW_PL);

      assertFalse(changeItems.isEmpty());

      boolean hasArtifactChanges = false, hasAttributeChanges = false, hasRelationChanges = false;
      Set<BranchId> branches = new HashSet<>();

      for (ChangeItem change : changeItems) {
         ChangeType changeType = change.getChangeType();
         if (changeType.isArtifactChange()) {
            hasArtifactChanges = true;
         } else if (changeType.isAttributeChange()) {
            hasAttributeChanges = true;
         } else if (changeType.isRelationChange()) {
            hasRelationChanges = true;
         }
         BranchId branch = change.getCurrentVersion().getTransactionToken().getBranch();
         if (!branches.contains(branch)) {
            branches.add(branch);
         }
      }

      assertTrue(hasArtifactChanges);
      assertTrue(hasAttributeChanges);
      assertTrue(hasRelationChanges);
      assertTrue(branches.size() > 1);
   }

   @Test
   public void testCreateTransaction() {
      // test transactionEndpoint.create(tx);
      String json = OseeInf.getResourceContents("create_tx.json", getClass());
      Response response = jaxRsApi.newTarget("orcs/txs").request(MediaType.APPLICATION_JSON).post(Entity.json(json));
      assertEquals(Family.SUCCESSFUL, response.getStatusInfo().getFamily());
   }
}