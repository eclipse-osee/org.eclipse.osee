/*
 * Created on Apr 22, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.change;

import static org.eclipse.osee.framework.skynet.core.change.ChangeType.CONFLICTING;
import static org.eclipse.osee.framework.skynet.core.change.ChangeType.INCOMING;
import static org.eclipse.osee.framework.skynet.core.change.ChangeType.OUTGOING;
import static org.eclipse.osee.framework.skynet.core.change.ModificationType.CHANGE;
import static org.eclipse.osee.framework.skynet.core.change.ModificationType.DELETE;
import static org.eclipse.osee.framework.skynet.core.change.ModificationType.NEW;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class RelationChangeIcons {
   private static final String BASE_IMAGE_STRING = "relate";
   private static final SkynetActivator plugin = SkynetActivator.getInstance();
   private static boolean imagesInitialized;

   public static Image getImage(ChangeType changeType, ModificationType modType) {
      checkImageRegistry();
      return plugin.getImage(BASE_IMAGE_STRING + changeType + modType);
   }

   private static void checkImageRegistry() {
      if (!imagesInitialized) {
         imagesInitialized = true;

         ImageDescriptor outNew = plugin.getImageDescriptor("out_new.gif");
         ImageDescriptor outChange = plugin.getImageDescriptor("out_change.gif");
         ImageDescriptor outDeleted = plugin.getImageDescriptor("out_delete.gif");
         ImageDescriptor incNew = plugin.getImageDescriptor("inc_new.gif");
         ImageDescriptor incChange = plugin.getImageDescriptor("inc_change.gif");
         ImageDescriptor incDeleted = plugin.getImageDescriptor("inc_delete.gif");
         ImageDescriptor conChange = plugin.getImageDescriptor("con_change.gif");
         ImageDescriptor conDeleted = plugin.getImageDescriptor("con_delete.gif");

         Image baseImage = plugin.getImage(BASE_IMAGE_STRING + ".gif");

         plugin.addImageToRegistry(BASE_IMAGE_STRING + OUTGOING + DELETE, new OverlayImage(baseImage, outDeleted));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + OUTGOING + CHANGE, new OverlayImage(baseImage, outChange));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + OUTGOING + NEW, new OverlayImage(baseImage, outNew));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + INCOMING + DELETE, new OverlayImage(baseImage, incDeleted));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + INCOMING + CHANGE, new OverlayImage(baseImage, incChange));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + INCOMING + NEW, new OverlayImage(baseImage, incNew));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + CONFLICTING + DELETE, new OverlayImage(baseImage, conDeleted));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + CONFLICTING + CHANGE, new OverlayImage(baseImage, conChange));
      }
   }

}
