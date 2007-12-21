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

package org.eclipse.osee.ats.editor.toolbar;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public class ToolbarArea {

   private final SMAManager smaMgr;
   private List<IAtsEditorToolBarService> services = new ArrayList<IAtsEditorToolBarService>();

   public ToolbarArea(SMAManager smaMgr) {
      this.smaMgr = smaMgr;
      loadAllStateItems();
   }

   public void dispose() {
      for (IAtsEditorToolBarService service : services)
         service.dispose();
   }

   public void create(IToolBarManager toolbarManager) {
      for (final IAtsEditorToolBarService atsEditorToolBarService : services) {
         try {
            if (atsEditorToolBarService.showInToolbar(null)) {
               toolbarManager.add(atsEditorToolBarService.getToolbarAction(smaMgr));
            }
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, false);
         }
      }
   }

   public void refresh() {
      for (IAtsEditorToolBarService service : services)
         service.refreshToolbarAction();
   }

   /**
    * @return the services
    */
   public List<IAtsEditorToolBarService> getServices() {
      return services;
   }

   @SuppressWarnings( {"deprecation", "unchecked"})
   private void loadAllStateItems() {
      if (services.size() > 0) return;
      IExtensionPoint point =
            Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.ats.AtsEditorToolbarService");
      if (point == null) {
         OSEELog.logSevere(AtsPlugin.class, "Can't access AtsStateItem extension point", true);
         return;
      }
      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         String classname = null;
         String bundleName = null;
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("AtsEditorToolbarService")) {
               classname = el.getAttribute("classname");
               bundleName = el.getContributor().getName();
               if (classname != null && bundleName != null) {
                  Bundle bundle = Platform.getBundle(bundleName);
                  try {
                     Class taskClass = bundle.loadClass(classname);
                     Class[] params = new Class[1];
                     params[0] = SMAManager.class;
                     Object obj = taskClass.getConstructor(params).newInstance(smaMgr);
                     services.add((IAtsEditorToolBarService) obj);
                  } catch (Exception ex) {
                     OSEELog.logException(AtsPlugin.class, "Error loading AtsEditorToolbarService extension", ex, true);
                  }
               }

            }
         }
      }
   }

}
