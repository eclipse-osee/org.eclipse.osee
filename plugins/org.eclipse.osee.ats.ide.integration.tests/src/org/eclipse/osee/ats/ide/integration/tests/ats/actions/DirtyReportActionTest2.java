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

import org.eclipse.osee.ats.ide.actions.DirtyReportAction;
import org.eclipse.osee.ats.ide.actions.IDirtyReportable;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class DirtyReportActionTest2 extends AbstractAtsActionTest {

   @Test(expected = OseeStateException.class)
   public void test() throws Exception {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      DirtyReportAction action = createAction();
      action.runWithException();
   }

   @Override
   public DirtyReportAction createAction() {
      return new DirtyReportAction(new IDirtyReportable() {
         @Override
         public Result isDirtyResult() {
            return Result.TrueResult;
         }
      });
   }

}
