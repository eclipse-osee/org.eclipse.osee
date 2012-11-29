/*
 * Created on Oct 21, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.client.integration.tests.ats.actions;

import junit.framework.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
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
      AtsTestUtil.cleanup();
      SMAEditor.closeAll();
   }

   @Test
   public void getImageDescriptor() throws Exception {
      Action action = createAction();
      Assert.assertNotNull("Image should be specified", action.getImageDescriptor());
   }

   public abstract Action createAction() throws OseeCoreException;
}
