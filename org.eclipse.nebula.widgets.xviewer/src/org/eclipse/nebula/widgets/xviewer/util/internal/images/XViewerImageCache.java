/*
 * Created on Mar 24, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.nebula.widgets.xviewer.util.internal.images;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class XViewerImageCache {

   static Map<String, Image> imageCache = new HashMap<String, Image>();

   /**
    * Return image
    * 
    * @param eg: clear.gif
    * @return the clearImage
    */
   public static Image getImage(String imageName) {
      if (!imageCache.containsKey(imageName)) {
         imageCache.put(imageName, getImageDescriptor(imageName).createImage());
      }
      return imageCache.get(imageName);
   }

   public static ImageDescriptor getImageDescriptor(String imageName) {
      URL url = XViewerImageCache.class.getResource(imageName);
      return ImageDescriptor.createFromURL(url);
   }

}
