/*
 * Created on Oct 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.client.integration.tests.ats.actions;

import org.eclipse.osee.ats.actions.ShowWorkDefinitionAction;

/**
 * @author Donald G. Dunne
 */
public class ShowWorkDefinitionActionTest extends AbstractAtsActionRunTest {

   @Override
   public ShowWorkDefinitionAction createAction() {
      return new ShowWorkDefinitionAction();
   }

}
