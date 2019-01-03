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
public class DirtyReportActionTest1 extends AbstractAtsActionTest {

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
            return new Result("Hello World");
         }
      });
   }

}
