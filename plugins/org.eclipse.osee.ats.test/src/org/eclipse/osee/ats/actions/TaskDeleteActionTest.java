/*
 * Created on Oct 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions;

import org.eclipse.osee.ats.actions.TaskDeleteAction.ITaskDeleteActionHandler;

/**
 * @author Donald G. Dunne
 */
public class TaskDeleteActionTest extends AbstractAtsActionRunTest {

   @Override
   public TaskDeleteAction createAction() {
      return new TaskDeleteAction(new ITaskDeleteActionHandler() {

         @Override
         public void taskDeleteActionHandler() {
            System.out.println(" ");
         }
      });
   }

}
