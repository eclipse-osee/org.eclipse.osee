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

import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.demo.DemoUtilCore;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.test.AbstractBlamTest;

/**
 * @author Donald G. Dunne
 */
public class AttributeCheckBlamTest extends AbstractBlamTest {

   public AttributeCheckBlamTest() {
      super(new AttributeCheckBlam());
   }

   @Override
   protected void runDemoTest(BlamEditor editor, XResultData rd) throws Exception {
      setSelected(AttributeCheckBlam.ARTIFACTS, DemoUtilCore.getRobotInterfacesArt());
      setSelected(AttributeCheckBlam.CHANGE_INCONSISTENT_VALUES, true);
      setSelected(AttributeCheckBlam.MULTIPLE_VALUES, true);
      setSelected(AttributeCheckBlam.ATTRIBUTE_TYPE, CoreAttributeTypes.StaticId);

      editor.executeBlam(rd);

      // Verify VariableMap
      VariableMap varMap = editor.getOverviewPage().getVariableMap();
      rd.assertNotNull(varMap.getValue(AttributeCheckBlam.ARTIFACTS));
      rd.assertNotNull(varMap.getValue(AttributeCheckBlam.CHANGE_INCONSISTENT_VALUES));
      rd.assertNotNull(varMap.getValue(AttributeCheckBlam.MULTIPLE_VALUES));
      rd.assertNotNull(varMap.getValue(AttributeCheckBlam.ATTRIBUTE_TYPE));

      rd.warning("Future - Needs Operation Test");
   }

}
