/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ui.skynet;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.SubscriptionGroup;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.account.rest.client.AccountClient;
import org.eclipse.osee.account.rest.client.AccountClient.UnsubscribeInfo;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.skynet.blam.operation.EmailGroupsData;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class EmailGroupsBlamTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo method = new TestInfo();

   private Artifact newGroup;

   @Before
   public void setUp() throws Exception {
      newGroup = ArtifactTypeManager.addArtifact(SubscriptionGroup, COMMON, method.getQualifiedTestName());
      newGroup.addRelation(CoreRelationTypes.Users_User, OseeApiService.userArt());
      newGroup.persist(method.getQualifiedTestName());
   }

   @After
   public void tearDown() throws Exception {
      if (newGroup != null) {
         newGroup.purgeFromBranch();
      }
   }

   @Test
   public void testEmailGroupsData() {
      Assert.assertNotNull(newGroup);

      EmailGroupsData data = new EmailGroupsData();
      Result result = data.isValid();
      Assert.assertTrue("Should error with no from address", result.getText().contains("from address"));
      data.setFromAddress("d@d.com");

      result = data.isValid();
      Assert.assertTrue("Should error with no reply to address", result.getText().contains("reply to address"));
      data.setReplyToAddress("k@k.com");

      result = data.isValid();
      Assert.assertTrue("Should error with no subject", result.getText().contains("subject"));

      data.setSubject("This is the subject");
      result = data.isValid();
      Assert.assertTrue("Should error with no body", result.getText().contains("body"));

      data.setBody("Hello World\nNow is the time");
      result = data.isValid();
      Assert.assertTrue("Should error with no groups", result.getText().contains("groups"));

      data.getGroups().add(newGroup);
      result = data.isValid();
      Assert.assertTrue(result.isTrue());

      UserToken user = OseeApiService.user();

      AccountClient client = ServiceUtil.getAccountClient();
      Collection<String> groupNames = new ArrayList<>();
      for (Artifact group : data.getGroups()) {
         groupNames.add(group.getName());
      }
      ResultSet<UnsubscribeInfo> infos = client.getUnsubscribeUris(user.getId(), groupNames);
      List<UnsubscribeInfo> unsubscribeInfos = infos.getList();

      String expectedBody = "Hello World\nNow is the time";
      String htmlOut = data.getHtmlResult(user.getName(), unsubscribeInfos);
      checkHtmlData(htmlOut, expectedBody);

      expectedBody = "<b>Hello World</b>";
      data.setBody(expectedBody);
      data.setBodyIsHtml(true);
      htmlOut = data.getHtmlResult(user.getName(), unsubscribeInfos);

      String firstPart = "<b>Hello World</b><br/><br/><p>Click <a href=\"";
      checkHtmlData(htmlOut, firstPart);

      String urlPart = "/unsubscribe/ui/";
      checkHtmlData(htmlOut, urlPart);

      String endPart =
         String.format("unsubscribe</a> to stop receiving all emails for the topic <b>\"%s\"</b>", newGroup.getName());
      checkHtmlData(htmlOut, endPart);
   }

   private void checkHtmlData(String htmlBody, String innerData) {
      String message = String.format("HtmlBody - [%s] did not contain [%s]", htmlBody, innerData);
      boolean result = htmlBody.contains(innerData);
      Assert.assertTrue(message, result);
   }
}
