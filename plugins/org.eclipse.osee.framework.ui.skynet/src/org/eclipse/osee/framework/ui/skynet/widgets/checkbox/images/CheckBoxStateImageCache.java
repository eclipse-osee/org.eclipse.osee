/*********************************************************************
 * Copyright (c) 2018 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.widgets.checkbox.images;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class CheckBoxStateImageCache {

   static Map<String, Image> imageCache = new HashMap<>();
   static Map<String, ImageDescriptor> imageDescriptorCache = new HashMap<>();

   public static Image getImage(String imageName) {
      if (!imageCache.containsKey(imageName)) {
         imageCache.put(imageName, getImageDescriptor(imageName).createImage());
      }
      return imageCache.get(imageName);
   }

   public static ImageDescriptor getImageDescriptor(String imageName) {
      if (!imageDescriptorCache.containsKey(imageName)) {
         URL url = CheckBoxStateImageCache.class.getResource(imageName);
         imageDescriptorCache.put(imageName, ImageDescriptor.createFromURL(url));
      }
      return imageDescriptorCache.get(imageName);
   }

}
