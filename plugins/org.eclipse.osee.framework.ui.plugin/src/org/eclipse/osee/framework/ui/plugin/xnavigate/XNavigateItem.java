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
package org.eclipse.osee.framework.ui.plugin.xnavigate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class XNavigateItem {

   private final List<XNavigateItem> children = new ArrayList<>();
   private String name;
   private XNavigateItem parent;
   protected KeyedImage oseeImage;
   private List<IXNavigateMenuItem> menuItems;
   private Object data;
   private long id = 0L;

   public XNavigateItem(XNavigateItem parent, String name, KeyedImage oseeImage) {
      this.parent = parent;
      this.name = name;
      this.oseeImage = oseeImage;
      if (parent != null) {
         parent.addChild(this);
      }
   }

   public void addChild(XNavigateItem item) {
      children.add(item);
      item.setParent(this);
   }

   public void removeChild(XNavigateItem item) {
      children.remove(item);
      item.setParent(null);
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
      // provided for subclass implementation
   }

   public boolean hasChildren() {
      return !getChildren().isEmpty();
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

   public List<IXNavigateMenuItem> getMenuItems() {
      if (menuItems == null) {
         return Collections.emptyList();
      }
      return menuItems;
   }

   /**
    * Add Menu creation listener to supply custom menu options when right-click on item.
    */
   public void addMenuItem(IXNavigateMenuItem listener) {
      if (menuItems == null) {
         menuItems = new LinkedList<>();
      }
      menuItems.add(listener);
   }

   public Object getData() {
      return data;
   }

   public void setData(Object data) {
      this.data = data;
   }

   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }
}