/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.demo.workflow.cr;

import org.eclipse.osee.framework.ui.skynet.blam.test.AbstractBlamTest;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;

/**
 * @author Donald G. Dunne
 */
public class CreateNewDemoChangeRequestBlamTest extends AbstractBlamTest {

   public CreateNewDemoChangeRequestBlamTest() {
      super(new CreateNewDemoChangeRequestBlam());
   }

   // Operation and vars tested in another test

   @Override
   protected void runDemoTest(BlamEditor editor, XResultData rd) throws Exception {

      // Widget variables tested in other BlamTests

      rd.warning("Future - Needs Operation Test");
   }

}
