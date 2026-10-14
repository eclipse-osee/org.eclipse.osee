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

package org.eclipse.osee.ats.ide.util.Import;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.widget.XWidgetLabels;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.test.AbstractBlamTest;

/**
 * @author Donald G. Dunne
 */
public class ImportTasksFromSimpleListBlamTest extends AbstractBlamTest {

   public ImportTasksFromSimpleListBlamTest() {
      super(new ImportTasksFromSimpleListBlam());
   }

   @Override
   protected void runDemoTest(BlamEditor editor, XResultData rd) throws Exception {
      setSelected("Create Tasks In State", "my test value");
      setSelected(XWidgetLabels.ASSIGNEES, Arrays.asList(DemoUsers.Joe_Smith, DemoUsers.Kay_Jason));
      setSelected(ImportTasksFromSimpleListBlam.TEAM_WORKFLOW, DemoUtil.getSawCodeCommittedWf().getStoreObject());

      editor.executeBlam(rd);

      // Verify VariableMap
      VariableMap varMap = editor.getOverviewPage().getVariableMap();
      rd.assertNotNull(varMap.getValue("Create Tasks In State"));
      List<UserToken> userTokens = varMap.getUserTokens(XWidgetLabels.ASSIGNEES);
      rd.assertEquals(2, userTokens.size());
      rd.assertTrue(userTokens.contains(DemoUsers.Joe_Smith));
      rd.assertTrue(userTokens.contains(DemoUsers.Kay_Jason));

      // Operation and vars tested in another test
   }

}
