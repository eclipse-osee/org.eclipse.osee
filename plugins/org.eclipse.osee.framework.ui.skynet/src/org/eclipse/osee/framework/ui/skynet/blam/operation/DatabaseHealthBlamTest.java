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

package org.eclipse.osee.framework.ui.skynet.blam.operation;

import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.test.AbstractBlamTest;

/**
 * @author Donald G. Dunne
 */
public class DatabaseHealthBlamTest extends AbstractBlamTest {

   public DatabaseHealthBlamTest() {
      super(new DatabaseHealthBlam());
   }

   @Override
   protected void runDemoTest(BlamEditor editor, XResultData rd) throws Exception {
      setSelected(DatabaseHealthBlam.CLEAN_ALL_PROMPT, true);
      setSelected(DatabaseHealthBlam.SHOW_ALL_PROMPT, true);

      editor.executeBlam(rd);

      // Verify VariableMap
      VariableMap varMap = editor.getOverviewPage().getVariableMap();
      rd.assertNotNull(varMap.getValue(DatabaseHealthBlam.CLEAN_ALL_PROMPT));
      rd.assertNotNull(varMap.getValue(DatabaseHealthBlam.SHOW_ALL_PROMPT));

      rd.warning("Future - Needs Operation Test");
   }

}
