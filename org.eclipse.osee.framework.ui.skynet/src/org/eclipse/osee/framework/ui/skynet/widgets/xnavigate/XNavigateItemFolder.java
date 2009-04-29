/*
 * Created on Apr 29, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.xnavigate;

import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class XNavigateItemFolder extends XNavigateItem {

   /**
    * @param parent
    * @param name
    */
   public XNavigateItemFolder(XNavigateItem parent, String name) {
      super(parent, name);
   }

   /**
    * @param parent
    * @param name
    * @param image
    */
   public XNavigateItemFolder(XNavigateItem parent, String name, Image image) {
      super(parent, name, image);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem#getImage()
    */
   @Override
   public Image getImage() {
      if (getName().contains("Admin")) {
         return SkynetGuiPlugin.getInstance().getImage("admin.gif");
      }
      return SkynetGuiPlugin.getInstance().getImage("folder.gif");
   }

}
