/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.integration.tests.ats.actions;

import org.eclipse.osee.ats.ide.actions.ReloadAction;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;

/**
 * @author Donald G. Dunne
 */
public class ReloadActionTest extends AbstractAtsActionRunTest {

   @Override
   public ReloadAction createAction() {
      return new ReloadAction(AtsTestUtil.getTeamWf(), null);
   }

}
