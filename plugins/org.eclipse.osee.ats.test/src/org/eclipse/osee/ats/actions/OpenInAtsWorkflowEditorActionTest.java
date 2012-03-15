/*
 * Created on Oct 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions;

import org.eclipse.osee.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.junit.After;

/**
 * @author Donald G. Dunne
 */
public class OpenInAtsWorkflowEditorActionTest extends AbstractAtsActionRunTest {

   @Override
   public OpenInAtsWorkflowEditorAction createAction() {
      return new OpenInAtsWorkflowEditorAction(AtsTestUtil.getSelectedAtsArtifactsForTeamWf());
   }

   @After
   public void closeEditors() {
      SMAEditor.closeAll();
   }
}
