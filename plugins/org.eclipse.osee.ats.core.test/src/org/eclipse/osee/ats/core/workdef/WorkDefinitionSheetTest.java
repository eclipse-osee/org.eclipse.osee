/*
 * Created on Mar 21, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workdef;

import java.io.File;
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class WorkDefinitionSheetTest {

   @Test
   public void testWorkDefinitionSheet() {
      WorkDefinitionSheet sheet = new WorkDefinitionSheet("name", new File("\\file\\path"));
      Assert.assertNotNull(sheet.getFile());
      Assert.assertEquals("name", sheet.getName());
      Assert.assertEquals("name   - file[\\file\\path]", sheet.toString());
   }
}
