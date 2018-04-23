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

import org.eclipse.osee.ats.actions.CopyActionDetailsAction;
import org.eclipse.osee.ats.client.integration.tests.ats.workflow.AtsTestUtil;

/**
 * @author Donald G. Dunne
 */
public class CopyActionDetailsActionTest extends AbstractAtsActionRunTest {

   @Override
   public CopyActionDetailsAction createAction() {
      return new CopyActionDetailsAction(AtsTestUtil.getTeamWf());
   }

}
