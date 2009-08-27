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
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.OseeImage;

/**
 * @author Roberto E. Escobar
 * @author Ryan Schmitt
 */
public enum ODMImage implements OseeImage {

   EXPORT_IMAGE("extractsupertype_wiz.png"),
   IMPORT_IMAGE("importsupertype_wiz.png"),

   DATASTORE_IMAGE("datastore.gif"),
   NAMESPACE_IMAGE("package_mode.gif"),
   FILE_SOURCE_IMAGE("file.gif"),
   INHERITANCE("hierarchy_co.gif"),

   INHERITED_ATTRIBUTE("access_restriction_attrib.gif"),
   LOCAL_ATTRIBUTE("local_attribute.gif"),
   INHERITED_RELATION("access_restriction_relat.gif"),
   LOCAL_RELATION("arrows.gif"),

   EXPAND_ALL("expandAll.gif"),
   TWO_WAY_REFERENCE("two_way_reference.gif"),
   ONE_WAY_REFERENCE("one_way_reference.gif"),

   SNAP_TO_GRID("geometry.gif"),
   SNAP_TO_GRID_DISABLED("geometry_disabled.gif");

   private final String fileName;

   private ODMImage(String fileName) {
      this.fileName = fileName;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(ODMEditorActivator.PLUGIN_ID, "images", fileName);
   }

   @Override
   public String getImageKey() {
      return ODMEditorActivator.PLUGIN_ID + ".images." + fileName;
   }
}