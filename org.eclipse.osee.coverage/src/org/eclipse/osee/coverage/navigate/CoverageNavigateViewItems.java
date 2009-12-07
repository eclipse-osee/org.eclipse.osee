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
package org.eclipse.osee.coverage.navigate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.coverage.CoverageManager;
import org.eclipse.osee.coverage.action.DeleteCoveragePackageAction;
import org.eclipse.osee.coverage.action.NewCoveragePackageAction;
import org.eclipse.osee.coverage.action.OpenCoveragePackageAction;
import org.eclipse.osee.coverage.blam.AbstractCoverageBlam;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.skynet.core.SystemGroup;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemBlam;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateViewItems;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public class CoverageNavigateViewItems extends XNavigateViewItems {
   private static CoverageNavigateViewItems navigateItems = new CoverageNavigateViewItems();

   public CoverageNavigateViewItems() {
      super();
   }

   public static CoverageNavigateViewItems getInstance() {
      return navigateItems;
   }

   @Override
   public List<XNavigateItem> getSearchNavigateItems() {
      List<XNavigateItem> items = new ArrayList<XNavigateItem>();
      addExtensionPointItems(items);
      return items;
   }

   private void addExtensionPointItems(List<XNavigateItem> items) {
      items.add(new XNavigateItemAction(null, new NewCoveragePackageAction(), NewCoveragePackageAction.OSEE_IMAGE));
      items.add(new XNavigateItemAction(null, new OpenCoveragePackageAction(), OpenCoveragePackageAction.OSEE_IMAGE));
      items.add(new XNavigateItemAction(null, new DeleteCoveragePackageAction(), DeleteCoveragePackageAction.OSEE_IMAGE));

      try {
         if (SystemGroup.OseeAdmin.isCurrentUserMember()) {
            for (ICoverageNavigateItem navigateItem : getExtensionPointNavigateItems()) {
               try {
                  items.addAll(navigateItem.getNavigateItems());
               } catch (Throwable th) {
                  OseeLog.log(CoverageNavigateViewItems.class, Level.SEVERE, th);
               }
            }

            for (AbstractCoverageBlam blam : CoverageManager.getCoverageBlams()) {
               items.add(new XNavigateItemBlam(null, blam));
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(CoverageNavigateViewItems.class, Level.SEVERE, ex);
      }
      if (CoverageUtil.isAdmin()) {
         items.add(new DoesNotWorkItemCoverage());
      }
   }

   private List<ICoverageNavigateItem> getExtensionPointNavigateItems() {
      List<ICoverageNavigateItem> data = new ArrayList<ICoverageNavigateItem>();
      List<IConfigurationElement> elements =
            ExtensionPoints.getExtensionElements("org.eclipse.osee.coverage.CoverageNavigateItem",
                  "ICoverageNavigateItem");
      for (IConfigurationElement element : elements) {
         String className = element.getAttribute("classname");
         String bundleName = element.getContributor().getName();

         if (Strings.isValid(bundleName) && Strings.isValid(className)) {
            try {
               Bundle bundle = Platform.getBundle(bundleName);
               Class<?> taskClass = bundle.loadClass(className);
               Object object;
               try {
                  Method getInstance = taskClass.getMethod("getInstance", new Class[] {});
                  object = getInstance.invoke(null, new Object[] {});
               } catch (Exception ex) {
                  object = taskClass.newInstance();
               }
               data.add((ICoverageNavigateItem) object);
            } catch (Exception ex) {
               throw new IllegalArgumentException(String.format("Unable to Load: [%s - %s]", bundleName, className), ex);
            }
         }
      }
      return data;
   }
}
