/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.integration.tests.integration.ui.skynet;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.SubscriptionGroup;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.skynet.blam.operation.EmailGroupsBlam;
import org.eclipse.osee.framework.ui.skynet.blam.operation.EmailGroupsData;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
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
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo method = new TestInfo();

   private Artifact newGroup;

   @Before
   public void setUp() throws Exception {
      newGroup = ArtifactTypeManager.addArtifact(SubscriptionGroup, COMMON, method.getQualifiedTestName());
      newGroup.addRelation(CoreRelationTypes.Users_User, UserManager.getUser());
      newGroup.persist(method.getQualifiedTestName());
   }

   @After
   public void tearDown() throws Exception {
      if (newGroup != null) {
         newGroup.purgeFromBranch();
      }
   }

   @Test
   public void testXWidgetsResolved() throws Exception {
      EmailGroupsBlam blam = new EmailGroupsBlam();
      for (XWidgetRendererItem xWidgetLayoutData : blam.getLayoutDatas()) {
         XWidget xWidget = xWidgetLayoutData.getXWidget();
         Assert.assertNotNull(xWidget);
         /**
          * Test that widget gets resolved. If widget is unresolved, the resolver will resolve it as an XLabel with an
          * error string so the widget creation doesn't exception and fail. Check for this condition.
          */
         Assert.assertFalse(xWidget.getLabel(), xWidget.getLabel().contains("Unhandled XWidget"));
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

      User user = UserManager.getUser();

      String expectedBody = "Hello World\nNow is the time";
      String htmlOut = data.getHtmlResult(user);
      checkHtmlData(user, htmlOut, expectedBody);

      expectedBody = "<b>Hello World</b>";
      data.setBody(expectedBody);
      data.setBodyIsHtml(true);
      htmlOut = data.getHtmlResult(user);

      String firstPart = "<b>Hello World</b></br>Click <a href=\"";
      checkHtmlData(user, htmlOut, firstPart);

      String urlPart = "/unsubscribe/ui/";
      checkHtmlData(user, htmlOut, urlPart);

      String endPart =
         String.format("unsubscribe</a> to stop receiving all emails for the topic <b>\"%s\"</b>", newGroup.getName());
      checkHtmlData(user, htmlOut, endPart);
   }

   private void checkHtmlData(User user, String htmlBody, String innerData) {
      String message = String.format("HtmlBody - [%s] did not contain [%s]", htmlBody, innerData);
      boolean result = htmlBody.contains(innerData);
      Assert.assertTrue(message, result);
   }
}
