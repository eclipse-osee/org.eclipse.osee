/*
 * Created on Jun 15, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.cases;

import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.framework.ui.skynet.test.cases.ImageManagerTest;

/**
 * @author Donald G. Dunne
 */
public class AtsImageTest extends ImageManagerTest {

   /**
    * @param imageClassName
    * @param oseeImages
    */
   public AtsImageTest() {
      super("AtsImage", AtsImage.values());
   }

}
