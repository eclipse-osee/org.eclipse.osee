/*
 * Created on Nov 10, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.column;

import java.util.Date;
import junit.framework.Assert;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.column.AssigneeColumn;
import org.eclipse.osee.ats.column.CreatedDateColumn;
import org.eclipse.osee.ats.test.util.DemoTestUtil;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.support.test.util.DemoWorkType;

/**
 * @tests CreatedDateColumn
 * @author Donald G Dunne
 */
public class CreatedDateColumnTest {

   @org.junit.Test
   public void testGetColumnText() throws Exception {
      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      Assert.assertNotNull(CreatedDateColumn.getInstance().getColumnText(codeArt, AssigneeColumn.getInstance(), 0));
      Date date = CreatedDateColumn.getDate(codeArt);
      Assert.assertNotNull(date);
      Assert.assertEquals(DateUtil.getMMDDYYHHMM(date), CreatedDateColumn.getDateStr(codeArt));

      ActionArtifact actionArt = codeArt.getParentActionArtifact();
      Assert.assertEquals(DateUtil.getMMDDYYHHMM(date), CreatedDateColumn.getDateStr(actionArt));
   }

}
