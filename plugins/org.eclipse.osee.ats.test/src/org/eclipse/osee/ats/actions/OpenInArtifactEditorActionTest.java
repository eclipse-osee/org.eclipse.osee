/*
 * Created on Oct 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions;

import org.eclipse.osee.ats.core.AtsTestUtil;

/**
 * @author Donald G. Dunne
 */
public class OpenInArtifactEditorActionTest extends AbstractAtsActionRunTest {

   @Override
   public OpenInArtifactEditorAction createAction() {
      return new OpenInArtifactEditorAction(AtsTestUtil.getSelectedAtsArtifactsForTeamWf());
   }

}
