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

package org.eclipse.osee.ats.ide.operation;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.test.AbstractBlamTest;

/**
 * @author Donald G. Dunne
 */
public class MoveTeamWorkflowsBlamTest extends AbstractBlamTest {

   public MoveTeamWorkflowsBlamTest() {
      super(new MoveTeamWorkflowsBlam());
   }

   @Override
   protected void runDemoTest(BlamEditor editor, XResultData rd) throws Exception {
      setSelected(MoveTeamWorkflowsBlam.SOURCE_TEAM_WORKFLOWS, Arrays.asList(
         DemoUtil.getSawCodeCommittedWf().getStoreObject(), DemoUtil.getSawReqCommittedWf().getStoreObject()));
      setSelected(MoveTeamWorkflowsBlam.DEST_TEAM_WORKFLOW, DemoUtil.getSawReqUnCommittedWf().getStoreObject());

      editor.executeBlam(rd);

      // Verify VariableMap
      VariableMap varMap = editor.getOverviewPage().getVariableMap();
      Collection<Artifact> sourceWfs = varMap.getArtifacts(MoveTeamWorkflowsBlam.SOURCE_TEAM_WORKFLOWS);
      rd.assertEquals(2, sourceWfs.size());
      Artifact destWf = varMap.getArtifact(MoveTeamWorkflowsBlam.DEST_TEAM_WORKFLOW);
      rd.assertNotNull(destWf);

      // Operation and vars tested in another test

   }

}
