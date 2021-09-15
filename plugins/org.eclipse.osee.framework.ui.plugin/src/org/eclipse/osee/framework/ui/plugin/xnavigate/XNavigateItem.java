/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.plugin.xnavigate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public abstract class XNavigateItem {

   public static final XNavItemCat TOP_ADMIN = new XNavItemCat("Admin");

   public static final XNavItemCat REPORTS = new XNavItemCat("Reports");
   public static final XNavItemCat EMAIL_NOTIFICATIONS = new XNavItemCat("Email & Notifications");
   public static final XNavItemCat DEMO = new XNavItemCat("Demo");

   public static final XNavItemCat UTILITY = new XNavItemCat("Util");
   public static final XNavItemCat UTILITY_EXAMPLES = new XNavItemCat("Util.Examples");

   public static final XNavItemCat ADVANCED_SEARCHES = new XNavItemCat("Advanced Searches");

   public static final XNavItemCat USER_MANAGEMENT = new XNavItemCat("User Management");
   public static final XNavItemCat USER_MANAGEMENT_ADMIN = new XNavItemCat("User Management.Admin");

   public static final XNavItemCat TRACE = new XNavItemCat("Traceability");

   public static final XNavItemCat DEFINE = new XNavItemCat("Define");
   public static final XNavItemCat DEFINE_HEALTH = new XNavItemCat("Define.Health");
   public static final XNavItemCat DEFINE_ADMIN = new XNavItemCat("Define.Admin");

   public static final XNavItemCat OTE = new XNavItemCat("OTE");

   public static final XNavItemCat PLE = new XNavItemCat("PLE");

   private final List<XNavigateItem> children = new ArrayList<>();
   private String name;
   private XNavigateItem parent;
   protected KeyedImage oseeImage;
   private List<IXNavigateMenuItem> menuItems;
   private Object data;
   private long id = 0L;
   protected INavigateItemRefresher refresher;
   protected final Collection<XNavItemCat> categories;
   Image image;

   public XNavigateItem(String name, OseeImage oseeImage, XNavItemCat... xNavItemCat) {
      this(name, ImageManager.create(oseeImage), xNavItemCat);
   }

   public XNavigateItem(String name, KeyedImage oseeImage, XNavItemCat... xNavItemCat) {
      this.name = name;
      this.oseeImage = oseeImage;
      if (parent != null) {
         parent.addChild(this);
      }
      this.categories = org.eclipse.osee.framework.jdk.core.util.Collections.asHashSet(xNavItemCat);
   }

   public abstract Collection<IUserGroupArtifactToken> getUserGroups();

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

   public String getName() {
      return name;
   }

   public XNavigateItem getParent() {
      return parent;
   }

   public String getDescription() {
      return "";
   }

   public void setImage(Image image) {
      this.image = image;
   }

   public Image getImage() {
      if (image == null) {
         if (oseeImage != null) {
            image = ImageManager.getImage(oseeImage);
         }
      }
      return image;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      // provided for subclass implementation
   }

   public boolean hasChildren() {
      return !getChildren().isEmpty();
   }

   @Override
   public String toString() {
      return String.format("[%s] cats %s", getName(), categories);
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

   /**
    * Will be called on refresh of Navigator and on clear of filter box. This gives late-loading NavigateItems the
    * chance to refresh once visible. If filtered on start-up and then filter removed, all items will receive refresh
    * call. XNavigateItem should refresh in background and then call refresher once completed.
    */
   public void refresh() {
      // do nothing
   }

   public void setRefresher(INavigateItemRefresher refresher) {
      this.refresher = refresher;
   }

   public void setParent(XNavigateItem parent) {
      this.parent = parent;
   }

   public Collection<XNavItemCat> getCategories() {
      return categories;
   }

}