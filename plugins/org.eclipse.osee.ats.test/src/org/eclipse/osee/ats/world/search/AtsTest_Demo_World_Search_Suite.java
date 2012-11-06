package org.eclipse.osee.ats.world.search;

import static org.junit.Assert.assertTrue;
import org.eclipse.osee.ats.util.DemoTestUtil;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({LegacyPcrIdQuickSearchTest.class, TeamDefinitionQuickSearchTest.class})
/**
 * @author Donald G. Dunne
 */
public class AtsTest_Demo_World_Search_Suite {
   @BeforeClass
   public static void setUp() throws Exception {
      OseeProperties.setIsInTest(true);
      assertTrue("Demo Application Server must be running.",
         ClientSessionManager.getAuthenticationProtocols().contains("demo"));
      assertTrue("Client must authenticate using demo protocol",
         ClientSessionManager.getSession().getAuthenticationProtocol().equals("demo"));
      System.out.println("\n\nBegin " + AtsTest_Demo_World_Search_Suite.class.getSimpleName());
      DemoTestUtil.setUpTest();
   }

   @AfterClass
   public static void tearDown() throws Exception {
      System.out.println("End " + AtsTest_Demo_World_Search_Suite.class.getSimpleName());
   }
}
