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

   public static final String EXPORT_IMAGE = "extractsupertype_wiz.png";
   public static final String IMPORT_IMAGE = "importsupertype_wiz.png";

   public static final String DATASTORE_IMAGE = "datastore.gif";
   public static final String NAMESPACE_IMAGE = "package_mode.gif";
   public static final String FILE_SOURCE_IMAGE = "file.gif";
   public static final String INHERITANCE = "hierarchy_co.gif";

   public static final String INHERITED_ATTRIBUTE = "access_restriction_attrib.gif";
   public static final String LOCAL_ATTRIBUTE = "local_attribute.gif";
   public static final String INHERITED_RELATION = "access_restriction_relat.gif";
   public static final String LOCAL_RELATION = "arrows.gif";

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
