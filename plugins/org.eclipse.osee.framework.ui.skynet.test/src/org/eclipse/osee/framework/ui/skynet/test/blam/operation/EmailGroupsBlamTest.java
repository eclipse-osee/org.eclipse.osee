/*
 * Created on Aug 19, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.test.blam.operation;

import java.util.ArrayList;
import java.util.Arrays;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.test.util.FrameworkTestUtil;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.blam.operation.EmailGroupsBlam;
import org.eclipse.osee.framework.ui.skynet.blam.operation.EmailGroupsData;
import org.eclipse.swt.program.Program;
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
   public void testEmailGroupsUnsubscribe() throws OseeCoreException {
      Assert.assertEquals("Should be subscribed to the user group", Arrays.asList(UserManager.getUser()),
         newGroup.getRelatedArtifacts(CoreRelationTypes.Users_User));
      Program.launch("http://localhost:8089/osee/unsubscribe/group/" + newGroup.getArtId() + "/user/" + UserManager.getUser().getArtId());

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
