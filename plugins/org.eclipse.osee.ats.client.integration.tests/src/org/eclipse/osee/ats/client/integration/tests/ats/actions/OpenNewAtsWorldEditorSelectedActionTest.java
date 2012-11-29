/*
 * Created on Oct 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.client.integration.tests.ats.actions;

import java.util.Arrays;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.actions.OpenNewAtsWorldEditorSelectedAction;
import org.eclipse.osee.ats.actions.OpenNewAtsWorldEditorSelectedAction.IOpenNewAtsWorldEditorSelectedHandler;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class OpenNewAtsWorldEditorSelectedActionTest extends AbstractAtsActionRunTest {

   @Override
   public OpenNewAtsWorldEditorSelectedAction createAction() {
      return new OpenNewAtsWorldEditorSelectedAction(new IOpenNewAtsWorldEditorSelectedHandler() {

         @Override
         public List<Artifact> getSelectedArtifacts() throws OseeCoreException {
            return Arrays.asList((Artifact) AtsTestUtil.getTeamWf());
         }

         @Override
         public CustomizeData getCustomizeDataCopy() {
            return null;
         }
      });
   }
}
