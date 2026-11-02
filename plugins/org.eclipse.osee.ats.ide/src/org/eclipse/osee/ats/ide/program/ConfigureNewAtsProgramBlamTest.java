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

package org.eclipse.osee.ats.ide.program;

import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.test.AbstractBlamTest;

/**
 * @author Donald G. Dunne
 */
public class ConfigureNewAtsProgramBlamTest extends AbstractBlamTest {

   public ConfigureNewAtsProgramBlamTest() {
      super(new ConfigureNewAtsProgramBlam());
   }

   @Override
   protected void runDemoTest(BlamEditor editor, XResultData rd) throws Exception {
      setSelected(ConfigureNewAtsProgramBlam.ONLY_COPY_FROM_COPYFROMTAG_AIS_AND_TEAMDEF, true);
      setSelected(ConfigureNewAtsProgramBlam.RETAIN_TEAM_LEADSMEMBERS, true);
      setSelected(ConfigureNewAtsProgramBlam.PERSIST_CHANGES, false);

      editor.executeBlam(rd);

      // Verify VariableMap captured the set values
      VariableMap varMap = editor.getOverviewPage().getVariableMap();
      rd.assertTrue(varMap.getBoolean(ConfigureNewAtsProgramBlam.ONLY_COPY_FROM_COPYFROMTAG_AIS_AND_TEAMDEF));
      rd.assertTrue(varMap.getBoolean(ConfigureNewAtsProgramBlam.RETAIN_TEAM_LEADSMEMBERS));
      rd.assertFalse(varMap.getBoolean(ConfigureNewAtsProgramBlam.PERSIST_CHANGES));
   }

}
