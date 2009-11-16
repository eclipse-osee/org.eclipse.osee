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
package org.eclipse.osee.framework.ui.skynet.widgets.xnavigate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.OseeImage;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class XNavigateItem {

   private final List<XNavigateItem> children = new ArrayList<XNavigateItem>();
   private String name;
   private XNavigateItem parent;
   private final OseeImage oseeImage;

   public XNavigateItem(XNavigateItem parent, String name, OseeImage oseeImage) {
      this.parent = parent;
      this.name = name;
      this.oseeImage = oseeImage;
      if (parent != null) parent.addChild(this);
   }

   public void addChild(XNavigateItem item) {
      children.add(item);
   }

   public void removeChild(XNavigateItem item) {
      children.remove(item);
   }

   public List<XNavigateItem> getChildren() {
      return children;
   }

   public List<XNavigateItem> getDynamicChildren() {
      return Collections.emptyList();
   }

   public String getName() {
      return name;
   }

   public XNavigateItem getParent() {
      return parent;
   }

   public String getDescription() {
      return "";
   }

   /**
    * @return the image
    */
   public Image getImage() {
      if (oseeImage != null) {
         return ImageManager.getImage(oseeImage);
      }
      return null;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name) {
      this.name = name;
   }

   public void run(TableLoadOption... tableLoadOptions) throws Exception {
   }

   public boolean hasChildren() throws OseeCoreException {
      return getChildren().size() > 0;
   }

   /**
    * @param parent the parent to set
    */
   public void setParent(XNavigateItem parent) {
      this.parent = parent;
   }

   @Override
   public String toString() {
      return getName();
   }
}