/*
 * Created on Oct 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions;

import java.util.Collections;
import org.eclipse.osee.ats.core.AtsTestUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class DuplicateWorkflowActionTest extends AbstractAtsActionRunTest {

   @Override
   public DuplicateWorkflowAction createAction() throws OseeCoreException {
      return new DuplicateWorkflowAction(Collections.singleton(AtsTestUtil.getTeamWf()));
   }

}
