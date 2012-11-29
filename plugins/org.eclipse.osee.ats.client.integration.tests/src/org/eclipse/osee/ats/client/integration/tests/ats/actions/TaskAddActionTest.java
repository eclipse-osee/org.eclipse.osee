/*
 * Created on Oct 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.client.integration.tests.ats.actions;

import org.eclipse.osee.ats.actions.TaskAddAction;
import org.eclipse.osee.ats.actions.TaskAddAction.ITaskAddActionHandler;

/**
 * @author Donald G. Dunne
 */
public class TaskAddActionTest extends AbstractAtsActionRunTest {

   @Override
   public TaskAddAction createAction() {
      return new TaskAddAction(new ITaskAddActionHandler() {

         @Override
         public void taskAddActionHandler() {
            System.out.println(" ");
         }
      });
   }

}
