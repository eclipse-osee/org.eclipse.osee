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
package org.eclipse.osee.framework.ui.skynet.test.blam.operation;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.HttpProcessor;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.test.util.FrameworkTestUtil;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.blam.operation.EmailGroupsBlam;
import org.eclipse.osee.framework.ui.skynet.blam.operation.EmailGroupsData;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * @author Donald G. Dunne
 */
public class EmailGroupsBlamTest extends EmailGroupsBlam {

   private static Artifact newGroup;

   @BeforeClass
   public static void setUp() throws Exception {
      cleanup();

      newGroup =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.UserGroup, BranchManager.getCommonBranch(),
            EmailGroupsBlamTest.class.getSimpleName());
      newGroup.addRelation(CoreRelationTypes.Users_User, UserManager.getUser());
      newGroup.persist(EmailGroupsBlamTest.class.getSimpleName());
   }

   @AfterClass
   public static void tearDown() throws Exception {
      cleanup();
   }

   private static void cleanup() throws OseeCoreException, Exception {
      FrameworkTestUtil.cleanupSimpleTest(BranchManager.getCommonBranch(), EmailGroupsBlamTest.class.getSimpleName());
   }

   @org.junit.Test
   public void testXWidgetsResolved() throws Exception {
      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
      for (DynamicXWidgetLayoutData xWidgetLayoutData : getLayoutDatas()) {
         XWidget xWidget = xWidgetLayoutData.getXWidget();
         Assert.assertNotNull(xWidget);
         /**
          * Test that widget gets resolved. If widget is unresolved, the resolver will resolve it as an XLabel with an
          * error string so the widget creation doesn't exception and fail. Check for this condition.
          */
         Assert.assertFalse(xWidget.getLabel(), xWidget.getLabel().contains("Unhandled XWidget"));
      }
      TestUtil.severeLoggingEnd(monitorLog, Arrays.asList(""));
   }

   @org.junit.Test
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

   @org.junit.Test
   public void testEmailGroupsUnsubscribe() throws OseeCoreException, MalformedURLException {
      Assert.assertEquals("Should be subscribed to the user group", Arrays.asList(UserManager.getUser()),
         newGroup.getRelatedArtifacts(CoreRelationTypes.Users_User));
      //      Program.launch("http://localhost:8089/osee/unsubscribe/group/" + newGroup.getArtId() + "/user/" + UserManager.getUser().getArtId());
      URL url = new URL("http://localhost:8089/osee/unsubscribe");
      String xml =
         "<request><groupId>" + newGroup.getArtId() + "</groupId><userId>" + UserManager.getUser().getArtId() + "</userId></request>";
      HttpProcessor.delete(url, xml, "text/xml", "UTF-8", new ByteArrayOutputStream(5));

      // TODO how test UnsubscribeServlet.doDelete without user interaction
      newGroup.reloadAttributesAndRelations();
      Assert.assertEquals("Should have been removed from this user group", new ArrayList<Artifact>(),
         newGroup.getRelatedArtifacts(CoreRelationTypes.Users_User));
   }

   private String getNonHtmlResult() throws OseeCoreException {
      return "<pre>Hello World\nNow is the time</pre></br>Click <a href=\"http://localhost:8089/osee/unsubscribe/group/" + newGroup.getArtId() + "/user/" + UserManager.getUser().getArtId() + "\">unsubscribe</a> to stop receiving all emails for the topic \"EmailGroupsBlamTest\"";
   }

   private String getHtmlResult() throws OseeCoreException {
      return "<b>Hello World</b></br>Click <a href=\"http://localhost:8089/osee/unsubscribe/group/" + newGroup.getArtId() + "/user/" + UserManager.getUser().getArtId() + "\">unsubscribe</a> to stop receiving all emails for the topic \"EmailGroupsBlamTest\"";
   }
}
