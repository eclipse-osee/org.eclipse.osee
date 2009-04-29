/*
 * Created on Apr 29, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.world;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class WorldXNavigateItemAction extends XNavigateItemAction {

   /**
    * @param parent
    * @param name
    */
   public WorldXNavigateItemAction(XNavigateItem parent, String name) {
      super(parent, name);
   }

   /**
    * @param parent
    * @param name
    * @param image
    */
   public WorldXNavigateItemAction(XNavigateItem parent, String name, Image image) {
      super(parent, name, image);
   }

   /**
    * @param parent
    * @param name
    * @param promptFirst
    * @param image
    */
   public WorldXNavigateItemAction(XNavigateItem parent, String name, boolean promptFirst, Image image) {
      super(parent, name, promptFirst, image);
   }

   /**
    * @param parent
    * @param action
    */
   public WorldXNavigateItemAction(XNavigateItem parent, Action action) {
      super(parent, action);
   }

   /**
    * @param parent
    * @param action
    * @param image
    */
   public WorldXNavigateItemAction(XNavigateItem parent, Action action, Image image) {
      super(parent, action, image);
   }

   /**
    * @param parent
    * @param action
    * @param image
    * @param promptFirst
    */
   public WorldXNavigateItemAction(XNavigateItem parent, Action action, Image image, boolean promptFirst) {
      super(parent, action, image, promptFirst);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem#getImage()
    */
   @Override
   public Image getImage() {
      if (getName().contains("Report")) {
         return AtsPlugin.getInstance().getImage("report.gif");
      } else if (getName().contains("Search")) {
         return AtsPlugin.getInstance().getImage("flashlight.gif");
      } else if (getName().contains("Task")) {
         return AtsPlugin.getInstance().getImage("task.gif");
      }
      return AtsPlugin.getInstance().getImage("globe.gif");
   }

}
