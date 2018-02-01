/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.integration.tests.integration.dsl.ui.integration;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.IAccessContextId;
import org.eclipse.osee.framework.core.dsl.OseeDslResourceUtil;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.dsl.ui.integration.operations.OseeDslRoleContextProvider;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test Case for {@link OseeDslRoleContextProvider}
 *
 * @author John R. Misinco
 */
public class OseeDslRoleContextProviderTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Test
   public void testGetContextId() throws Exception {
      String contextGuid = GUID.create();
      Artifact user = ArtifactQuery.getArtifactFromToken(SystemUser.Anonymous);
      String testSheet = getTestSheet1(contextGuid, user.getGuid());
      OseeDsl model = OseeDslResourceUtil.loadModel("osee:/text.osee", testSheet).getModel();
      MockDslProvider dslProvider = new MockDslProvider(model);
      OseeDslRoleContextProvider contextProvider = new OseeDslRoleContextProvider(dslProvider);
      Collection<? extends IAccessContextId> contextIds = contextProvider.getContextId(user);

      Assert.assertEquals(1, contextIds.size());
      Assert.assertEquals(contextGuid, contextIds.iterator().next().getGuid());
   }

   @Test
   public void testGetContextIdExtended() throws Exception {
      String contextGuid1 = GUID.create();
      String contextGuid2 = GUID.create();
      String role2Guid = GUID.create();
      Artifact user = ArtifactQuery.getArtifactFromToken(SystemUser.Anonymous);
      String testSheet = getTestSheet2(contextGuid1, user.getGuid(), contextGuid2, role2Guid);
      OseeDsl model = OseeDslResourceUtil.loadModel("osee:/text.osee", testSheet).getModel();
      MockDslProvider dslProvider = new MockDslProvider(model);
      OseeDslRoleContextProvider contextProvider = new OseeDslRoleContextProvider(dslProvider);
      Collection<? extends IAccessContextId> contextIds = contextProvider.getContextId(user);

      Assert.assertEquals(1, contextIds.size());
      Assert.assertEquals(contextGuid1, contextIds.iterator().next().getGuid());

      Artifact role2User = ArtifactQuery.getOrCreate(role2Guid, CoreArtifactTypes.Artifact, CoreBranches.COMMON);
      role2User.persist("Test User");
      contextIds = contextProvider.getContextId(role2User);

      Assert.assertEquals(2, contextIds.size());
      Iterator<? extends IAccessContextId> iterator = contextIds.iterator();
      List<String> contextList = new LinkedList<>();
      contextList.add(contextGuid1);
      contextList.add(contextGuid2);
      Assert.assertTrue(contextList.remove(iterator.next().getGuid()));
      Assert.assertTrue(contextList.remove(iterator.next().getGuid()));

      role2User.deleteAndPersist();
   }

   private String getTestSheet1(String contextGuid, String role1Guid) {
      StringBuilder sb = new StringBuilder();
      sb.append("role \"role1\" {\n");
      sb.append("   guid \"");
      sb.append(role1Guid);
      sb.append("\";\n");
      sb.append("   accessContext \"role1.context\";\n");
      sb.append("}\n\n");

      sb.append("accessContext \"role1.context\" {\n");
      sb.append("   guid \"");
      sb.append(contextGuid);
      sb.append("\";\n");
      sb.append("   DENY edit relationType ALL BOTH;\n");
      sb.append("}\n");
      return sb.toString();
   }

   private String getTestSheet2(String context1, String role1Guid, String context2, String role2Guid) {
      StringBuilder sb = new StringBuilder(getTestSheet1(context1, role1Guid));
      sb.append("\nrole \"role2\" extends \"role1\" {\n");
      sb.append("   guid \"");
      sb.append(role2Guid);
      sb.append("\";\n");
      sb.append("   accessContext \"role2.context\";\n");
      sb.append("}\n\n");

      sb.append("accessContext \"role2.context\" {\n");
      sb.append("   guid \"");
      sb.append(context2);
      sb.append("\";\n");
      sb.append("   DENY edit relationType ALL BOTH;\n");
      sb.append("}\n");
      return sb.toString();
   }
}
