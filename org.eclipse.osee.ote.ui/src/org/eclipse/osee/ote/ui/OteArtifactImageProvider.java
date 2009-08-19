/*
 * Created on Jun 8, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.ui;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageProvider;
import org.eclipse.osee.framework.ui.skynet.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OteArtifactImageProvider extends ArtifactImageProvider {

   @Override
   public void init() throws OseeCoreException {
      ImageManager.registerBaseImage("Test Procedure", OteImage.TEST_PROCEDURE, this);
      ImageManager.registerBaseImage("Test Configuration", OteImage.TEST_CONFIG, this);
      ImageManager.registerBaseImage("Test Run", OteImage.TEST_RUN, this);
      ImageManager.registerBaseImage("Test Case", OteImage.TEST_CASE, this);
      ImageManager.registerBaseImage("Test Support", OteImage.TEST_SUPPORT, this);
   }
}