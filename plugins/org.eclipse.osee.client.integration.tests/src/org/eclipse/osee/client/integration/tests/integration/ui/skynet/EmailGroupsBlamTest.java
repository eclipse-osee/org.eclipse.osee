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
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilderClient;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.util.HttpProcessor;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
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
      newGroup =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.UserGroup, BranchManager.getCommonBranch(),
            method.getQualifiedTestName());
      newGroup.addRelation(CoreRelationTypes.Users_User, UserManager.getUser());
      newGroup.persist(method.getQualifiedTestName());
   }

   @After
   public void tearDown() throws Exception {
      if (newGroup != null) {
         newGroup.purgeFromBranch();
      }
   }

   @org.junit.Test
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
   public void testEmailGroupsData() throws OseeCoreException {
      Assert.assertNotNull(newGroup);

      EmailGroupsData data = new EmailGroupsData();
      Result result = data.isValid();
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

      Assert.assertEquals(getNonHtmlResult(), data.getHtmlResult(UserManager.getUser()));

      data.setBody("<b>Hello World</b>");
      data.setBodyIsHtml(true);
      Assert.assertEquals(getHtmlResult(), data.getHtmlResult(UserManager.getUser()));
   }

   @Test
   public void testEmailGroupsUnsubscribe() throws OseeCoreException, MalformedURLException {
      Assert.assertEquals("Should be subscribed to the user group", Arrays.asList(UserManager.getUser()),
         newGroup.getRelatedArtifacts(CoreRelationTypes.Users_User));

      HttpUrlBuilderClient urlBuilder = HttpUrlBuilderClient.getInstance();
      String url = urlBuilder.getOsgiServletServiceUrl(OseeServerContext.OSEE_EMAIL_UNSUBSCRIBE, null);

      StringBuilder builder = new StringBuilder();
      builder.append("<request><groupId>");
      builder.append(newGroup.getArtId());
      builder.append("</groupId><userId>");
      builder.append(UserManager.getUser().getArtId());
      builder.append("</userId></request>");
      String xml = builder.toString();
      HttpProcessor.delete(new URL(url), xml, "text/xml", "UTF-8", new ByteArrayOutputStream(5));

      // TODO how test UnsubscribeServlet.doDelete without user interaction
      newGroup.reloadAttributesAndRelations();
      Assert.assertEquals("Should have been removed from this user group", new ArrayList<Artifact>(),
         newGroup.getRelatedArtifacts(CoreRelationTypes.Users_User));
   }

   private String getNonHtmlResult() throws OseeCoreException {
      StringBuilder builder = new StringBuilder();
      builder.append("<pre>Hello World\nNow is the time</pre>");
      addUnsubscribeMessage(builder);
      return builder.toString();
   }

   private String getHtmlResult() throws OseeCoreException {
      StringBuilder builder = new StringBuilder();
      builder.append("<b>Hello World</b>");
      addUnsubscribeMessage(builder);
      return builder.toString();
   }

   private void addUnsubscribeMessage(StringBuilder builder) throws OseeCoreException {
      builder.append("</br>Click <a href=\"");
      HttpUrlBuilderClient urlBuilder = HttpUrlBuilderClient.getInstance();
      String url = urlBuilder.getOsgiServletServiceUrl(OseeServerContext.OSEE_EMAIL_UNSUBSCRIBE, null);
      builder.append(url);
      builder.append("/group/");
      builder.append(newGroup.getArtId());
      builder.append("/user/");
      builder.append(UserManager.getUser().getArtId());
      builder.append("\">unsubscribe</a> to stop receiving all emails for the topic \"");
      builder.append(method.getQualifiedTestName());
      builder.append("\"");
   }
}
