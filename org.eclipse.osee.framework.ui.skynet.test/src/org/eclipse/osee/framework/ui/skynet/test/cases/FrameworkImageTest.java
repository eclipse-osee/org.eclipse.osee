/*
 * Created on Jun 15, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.test.cases;

import org.eclipse.osee.framework.ui.skynet.FrameworkImage;

/**
 * @author Donald G. Dunne
 */
public class FrameworkImageTest extends ImageManagerTest {

   /**
    * @param imageClassName
    * @param oseeImages
    */
   public FrameworkImageTest() {
      super("FrameworkImage", FrameworkImage.values());
   }

}
