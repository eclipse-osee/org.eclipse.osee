/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.actions;

import org.eclipse.osee.ats.actions.OpenInAtsWorldAction;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class OpenInAtsWorldActionTest1 extends AbstractAtsActionRunTest {

   @Override
   public OpenInAtsWorldAction createAction()  {
      return new OpenInAtsWorldAction(AtsTestUtil.getTeamWf());
   }

   @Test(expected = OseeStateException.class)
   public void testNoParentAction()  {
      AtsTestUtil.getTeamWf().deleteRelations(AtsRelationTypes.ActionToWorkflow_Action);
      AtsTestUtil.getTeamWf().persist(getClass().getSimpleName());

      OpenInAtsWorldAction action = createAction();
      action.runWithException();
   }

}
