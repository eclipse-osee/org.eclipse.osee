/*
 * Created on Jun 8, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.demo;

import org.eclipse.osee.ats.config.demo.util.DemoImage;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageProvider;
import org.eclipse.osee.framework.ui.skynet.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class DemoArtifactImageProvider extends ArtifactImageProvider {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.ArtifactImageProvider#init()
    */
   @Override
   public void init() throws OseeCoreException {
      ImageManager.registerBaseImage("Demo Code Team Workflow", DemoImage.DEMO_WORKFLOW);
      ImageManager.registerBaseImage("Demo Req Team Workflow", DemoImage.DEMO_WORKFLOW);
      ImageManager.registerBaseImage("Demo Test Team Workflow", DemoImage.DEMO_WORKFLOW);
   }
}