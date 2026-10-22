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

import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.test.AbstractBlamTest;

/**
 * @author Donald G. Dunne
 */
public class EmailGroupsBlamTest extends AbstractBlamTest {

   public EmailGroupsBlamTest() {
      super(new EmailGroupsBlam());
   }

   @Override
   protected void runDemoTest(BlamEditor editor, XResultData rd) throws Exception {
      Artifact userGroup = ArtifactQuery.getArtifactFromToken(CoreUserGroups.AccountAdmin);
      setSelected(EmailGroupsBlam.GROUPS, userGroup); // TBD: andWidget
      setSelected(EmailGroupsBlam.REPLYTO_ADDRESS, "my test value");
      setSelected(EmailGroupsBlam.SUBJECT, "my test value");
      setSelected(EmailGroupsBlam.BODY_IS_HTML, true);
      setSelected(EmailGroupsBlam.BODY, "my test value");

      editor.executeBlam(rd);

      // Verify VariableMap
      VariableMap varMap = editor.getOverviewPage().getVariableMap();
      rd.assertNotNull(varMap.getValue(EmailGroupsBlam.GROUPS));
      rd.assertNotNull(varMap.getValue(EmailGroupsBlam.REPLYTO_ADDRESS));
      rd.assertNotNull(varMap.getValue(EmailGroupsBlam.SUBJECT));
      rd.assertNotNull(varMap.getValue(EmailGroupsBlam.BODY_IS_HTML));
      rd.assertNotNull(varMap.getValue(EmailGroupsBlam.BODY));

      rd.warning("Future - Needs Operation Test");
   }

}
