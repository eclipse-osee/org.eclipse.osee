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
package org.eclipse.osee.define.ide.navigate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.define.ide.internal.Activator;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.OpenPerspectiveNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.IXNavigateCommonItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateCommonItems;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemOperation;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateViewItems;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.action.CompareTwoStringsAction;
import org.eclipse.osee.framework.ui.skynet.artifact.MassEditDirtyArtifactOperation;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public class DefineNavigateViewItems implements XNavigateViewItems, IXNavigateCommonItem {
   private final List<XNavigateItem> items = new CopyOnWriteArrayList<>();

   @Override
   public List<XNavigateItem> getSearchNavigateItems() {
      addDefineSectionChildren(null);
      XNavigateCommonItems.addCommonNavigateItems(items, Arrays.asList(getSectionId()));
      return items;
   }

   public void addDefineSectionChildren(XNavigateItem item) {
      try {
         items.add(new XNavigateItemAction(item, new CompareTwoStringsAction(), FrameworkImage.EDIT));
         items.add(new XNavigateItemAction(item,
            new org.eclipse.osee.framework.ui.skynet.action.CompareTwoArtifactIdListsAction(), FrameworkImage.EDIT));
         items.add(new XNavigateItemOperation(item, FrameworkImage.ARTIFACT_MASS_EDITOR,
            MassEditDirtyArtifactOperation.NAME, new MassEditDirtyArtifactOperation()));
         addExtensionPointItems(items);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void addExtensionPointItems(List<XNavigateItem> items) {
      IExtensionPoint point =
         Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.define.ide.DefineNavigateItem");
      if (point == null) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Can't access DefineNavigateItem extension point");
         return;
      }
      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         String classname = null;
         String bundleName = null;
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("IDefineNavigateItem")) {
               classname = el.getAttribute("classname");
               bundleName = el.getContributor().getName();
            }
         }
         if (classname != null && bundleName != null) {
            Bundle bundle = Platform.getBundle(bundleName);
            try {
               Class<?> taskClass = bundle.loadClass(classname);
               Object obj = taskClass.newInstance();
               IDefineNavigateItem task = (IDefineNavigateItem) obj;
               items.addAll(task.getNavigateItems());
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Error loading DefineNavigateItem extension", ex);
            }
         }
      }
   }

   @Override
   public void createCommonSection(List<XNavigateItem> items, List<String> excludeSectionIds) {
      XNavigateItem reviewItem = new XNavigateItem(null, "OSEE Define", FrameworkImage.LASER);
      new OpenPerspectiveNavigateItem(reviewItem, "Define", "osee.define.PerspectiveFactory", FrameworkImage.LASER);
      addDefineSectionChildren(reviewItem);
      items.add(reviewItem);
   }

   @Override
   public String getSectionId() {
      return "Define";
   }

}