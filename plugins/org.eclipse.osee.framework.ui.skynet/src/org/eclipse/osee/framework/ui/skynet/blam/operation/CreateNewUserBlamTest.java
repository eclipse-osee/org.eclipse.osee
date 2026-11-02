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
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.test.AbstractBlamTest;

/**
 * @author Donald G. Dunne
 */
public class CreateNewUserBlamTest extends AbstractBlamTest {

   public CreateNewUserBlamTest() {
      super(new CreateNewUserBlam());
   }

   @Override
   protected void runDemoTest(BlamEditor editor, XResultData rd) throws Exception {
      setSelected(CreateNewUserBlam.NAME_LAST_FIRST, "my test value");
      setSelected(CreateNewUserBlam.USERID_UNIQUE, "my test value");
      setSelected(CreateNewUserBlam.EMAIL, "my test value");
      setSelected(CreateNewUserBlam.ACTIVE, true);

      editor.executeBlam(rd);

      // Verify VariableMap
      VariableMap varMap = editor.getOverviewPage().getVariableMap();
      rd.assertNotNull(varMap.getValue(CreateNewUserBlam.NAME_LAST_FIRST));
      rd.assertNotNull(varMap.getValue(CreateNewUserBlam.USERID_UNIQUE));
      rd.assertNotNull(varMap.getValue(CreateNewUserBlam.EMAIL));
      rd.assertNotNull(varMap.getValue(CreateNewUserBlam.ACTIVE));

      Artifact newUser =
         ArtifactQuery.getArtifactFromAttribute(CoreAttributeTypes.UserId, "my test value", CoreBranches.COMMON);
      rd.assertNotNull(newUser);
   }

}
