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

package org.eclipse.osee.define.ide.blam.operation;

import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.widget.XWidgetLabels;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.test.AbstractBlamTest;

/**
 * @author Donald G. Dunne
 */
public class FixDuplicateEnumeratedAttributesBlamTest extends AbstractBlamTest {

   public FixDuplicateEnumeratedAttributesBlamTest() {
      super(new FixDuplicateEnumeratedAttributesBlam());
   }

   @Override
   protected void runDemoTest(BlamEditor editor, XResultData rd) throws Exception {
      setSelected(XWidgetLabels.BRANCH, DemoBranches.SAW_Bld_2);
      setSelected(FixDuplicateEnumeratedAttributesBlam.COMMIT_CHANGES_LABEL, true);

      editor.executeBlam(rd);

      // Verify VariableMap
      VariableMap varMap = editor.getOverviewPage().getVariableMap();
      rd.assertNotNull(varMap.getValue(XWidgetLabels.BRANCH));
      rd.assertNotNull(varMap.getValue(FixDuplicateEnumeratedAttributesBlam.COMMIT_CHANGES_LABEL));

      rd.warning("Future - Needs Operation Test");
   }

}
