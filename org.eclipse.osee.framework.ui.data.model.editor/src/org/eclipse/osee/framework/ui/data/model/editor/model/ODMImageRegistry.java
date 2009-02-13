/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.data.model.editor.model;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public class ODMImageRegistry {

   private Map<String, Image> imageMap;

   public ODMImageRegistry() {
      this.imageMap = new HashMap<String, Image>();
   }

   public Image getImage(String imageName) {
      return imageMap.get(imageName);
   }

   public void addImage(String id, ImageDescriptor imageDescriptor) {
      imageMap.put(id, imageDescriptor.createImage());
   }

   public void clear() {
      imageMap.clear();
   }
}
