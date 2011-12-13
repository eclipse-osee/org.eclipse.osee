/*
 * Created on Oct 21, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions;

import junit.framework.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.core.AtsTestUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.rule.OseeHousekeepingRule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Abstract for Action tests that tests getImageDescriptor and calls cleanup before/after class
 * 
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsActionTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Throwable {
      String name = AtsTestUtil.getName();
      AtsTestUtil.cleanup();
      OseeHousekeepingRule.verify(name, name);
   }

   @Test
   public void getImageDescriptor() throws Exception {
      Action action = createAction();
      Assert.assertNotNull("Image should be specified", action.getImageDescriptor());
   }

   public abstract Action createAction() throws OseeCoreException;
}
