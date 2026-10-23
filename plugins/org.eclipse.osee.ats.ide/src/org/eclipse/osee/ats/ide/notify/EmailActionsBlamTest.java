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

package org.eclipse.osee.ats.ide.notify;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.ide.notify.EmailActionsData.EmailRecipient;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.skynet.blam.test.AbstractBlamTest;

/**
 * @author Donald G. Dunne
 */
public class EmailActionsBlamTest extends AbstractBlamTest {

   public EmailActionsBlamTest() {
      super(new EmailActionsBlam());
   }

   @Override
   protected void runDemoTest(BlamEditor editor, XResultData rd) throws Exception {
      setSelected(EmailActionsBlam.I_CERTIFY_NO_RESTRICTED_OR_SENSITIVE_DATA, true);

      IAtsTeamWorkflow sawCodeWf = DemoUtil.getSawCodeCommittedWf();
      IAtsTeamWorkflow sawReqWf = DemoUtil.getSawReqCommittedWf();
      List<Artifact> wfs = new ArrayList<>();
      wfs.add((Artifact) sawCodeWf.getStoreObject());
      wfs.add((Artifact) sawReqWf.getStoreObject());
      setSelected(EmailActionsBlam.ATS_WORKFLOWS, wfs);

      setSelected(EmailActionsBlam.SUBJECT, "my emailactionsblam.subject");

      setSelected(EmailActionsBlam.RECIPIENT, EmailRecipient.Assignees.name());

      setSelected(EmailActionsBlam.BODY, "my emailactionsblam.body");
      setSelected(EmailActionsBlam.INCLUDE_CANCEL_HYPERLINK, true);

      editor.executeBlam(rd);
      if (rd.isErrors()) {
         return;
      }

      // TODO: Verify the operation completed successfully
      // Example: check log output, verify artifacts created, etc.
   }

}
