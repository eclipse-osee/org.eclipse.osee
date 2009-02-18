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
package org.eclipse.osee.framework.ui.data.model.editor.utility;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.data.model.editor.ODMEditorActivator;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public class ODMImages {

   public static final String INHERITANCE = "hierarchy_co.gif";
   public static final String ATTRIBUTE_ENTRY = "field_public_obj.gif";
   public static final String RELATION_ENTRY = "link.gif";
   public static final String EXPAND_ALL = "expandall.gif";
   public static final String TWO_WAY_REFERENCE = "two_way_reference.gif";
   public static final String ONE_WAY_REFERENCE = "one_way_reference.gif";

   public static final String SNAP_TO_GRID = "geometry.gif";
   public static final String SNAP_TO_GRID_DISABLED = "geometry_disabled.gif";

   private ODMImages() {
   }

   public static Image getImage(String imageName) {
      return ODMEditorActivator.getInstance().getImage(imageName);
   }

   public static ImageDescriptor getImageDescriptor(String imageName) {
      return ODMEditorActivator.getInstance().getImageDescriptor(imageName);
   }

}
