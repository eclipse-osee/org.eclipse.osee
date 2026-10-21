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

import static org.eclipse.osee.framework.ui.skynet.blam.operation.AdvancedKeywordSearchBlam.FILTER_RESULTS_BY_ARTIFACT_TYPE;
import static org.eclipse.osee.framework.ui.skynet.blam.operation.AdvancedKeywordSearchBlam.INCLUDE_ATTRIBUTE_VALUES_IN_RESULTS;
import static org.eclipse.osee.framework.ui.skynet.blam.operation.AdvancedKeywordSearchBlam.KEYWORD_GROUPS_ONE_SET_OF_KEYWORDS_PER_LINE;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.widget.XWidgetLabels;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.test.AbstractBlamTest;

/**
 * @author Donald G. Dunne
 */
public class AdvancedKeywordSearchBlamTest extends AbstractBlamTest {

   public AdvancedKeywordSearchBlamTest() {
      super(new AdvancedKeywordSearchBlam());
   }

   @Override
   protected void runDemoTest(BlamEditor editor, XResultData rd) throws Exception {
      setSelected(XWidgetLabels.BRANCH, DemoBranches.SAW_Bld_2);
      setSelected(KEYWORD_GROUPS_ONE_SET_OF_KEYWORDS_PER_LINE, "my test value");
      setSelected(FILTER_RESULTS_BY_ARTIFACT_TYPE, CoreArtifactTypes.Requirement);
      setSelected(INCLUDE_ATTRIBUTE_VALUES_IN_RESULTS, CoreAttributeTypes.StaticId);

      editor.executeBlam(rd);

      // Verify VariableMap
      VariableMap varMap = editor.getOverviewPage().getVariableMap();
      rd.assertNotNull(varMap.getValue(XWidgetLabels.BRANCH));
      rd.assertNotNull(varMap.getValue(KEYWORD_GROUPS_ONE_SET_OF_KEYWORDS_PER_LINE));
      rd.assertNotNull(varMap.getValue(FILTER_RESULTS_BY_ARTIFACT_TYPE));
      rd.assertNotNull(varMap.getValue(INCLUDE_ATTRIBUTE_VALUES_IN_RESULTS));

      rd.warning("Future - Needs Operation Test");
   }

}
