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
package org.eclipse.osee.ats.client.integration.tests.ats.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.client.demo.DemoUsers;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Case for {@link AbstractAtsQueryImpl}
 *
 * @author Donald G. Dunne
 */
public class AbstractAtsQueryImplTest {

   private IAtsQuery query;

   @Before
   public void setup() {
      query = AtsClientService.get().getQueryService().createQuery();
   }

   @Test
   public void testIsGoal() {
      assertEquals(1, query.isGoal().getResults().size());
   }

   @Test
   public void testAndAssignee() {
      assertEquals(1, query.isGoal().andAssignee(AtsCoreUsers.UNASSIGNED_USER).getResults().size());

      assertEquals(0, query.isGoal().andAssignee(
         AtsClientService.get().getUserService().getUserById(DemoUsers.Joe_Smith.getUserId())).getResults().size());
   }

   @Test
   public void testGetResultArtifacts() {
      ResultSet<ArtifactId> resultArtifacts = query.isGoal().getResultArtifacts();
      assertEquals(1, resultArtifacts.size());
      assertTrue(resultArtifacts.iterator().next() instanceof Artifact);
   }

}
