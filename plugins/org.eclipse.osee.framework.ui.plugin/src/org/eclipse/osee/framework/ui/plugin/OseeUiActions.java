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
package org.eclipse.osee.framework.ui.plugin;

import java.util.logging.Level;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.IActionReportingService;
import org.eclipse.osee.framework.plugin.core.IActionable;
import org.eclipse.osee.framework.ui.plugin.internal.OseePluginUiActivator;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

public final class OseeUiActions {
   private static final String BUG_TITLE = "Generate Action Against This Tool";

   private OseeUiActions() {
      super();
   }

   private static IActionReportingService getActionReportingService() throws OseeCoreException {
      return OseePluginUiActivator.getInstance().getActionReportingService();
   }

   private static boolean isActionReportingServiceAvailable() {
      boolean isAvailable = false;
      try {
         isAvailable = getActionReportingService() != null;
      } catch (OseeCoreException e) {
         // Do nothing;
      }
      return isAvailable;
   }

   private static void reportLogException(String item, String description) {
      try {
         getActionReportingService().report(item, description);
      } catch (Exception ex) {
         OseeLog.log(OseePluginUiActivator.class, Level.SEVERE, ex);
      }
   }

   public static void addButtonToEditorToolBar(final MultiPageEditorPart editorPart, final IActionable actionableObject, final String pluginId, IToolBarManager toolBar, final String editorId, final String actionableItem) {
      if (!isActionReportingServiceAvailable()) {
         return;
      }
      Action bugAction = new Action(BUG_TITLE, IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            String desc = getActionDescription(editorId, pluginId, actionableObject);
            reportLogException(actionableItem, desc);
         }
      };
      bugAction.setToolTipText(BUG_TITLE);
      bugAction.setImageDescriptor(ImageManager.getImageDescriptor(PluginUiImage.BUG));
      toolBar.add(bugAction);
   }

   public static void addButtonToEditorToolBar(IActionable actionableObject, final String pluginId, ToolBar toolBar, final String editorId, String actionableItem) {
      addButtonToEditorToolBar(actionableObject, pluginId, toolBar, null, editorId, actionableItem);
   }

   public static void addButtonToEditorToolBar(IActionable actionableObject, final String pluginId, Composite comp, final String editorId, String actionableItem) {
      addButtonToEditorToolBar(actionableObject, pluginId, null, comp, editorId, actionableItem);
   }

   private static void addButtonToEditorToolBar(final IActionable actionableObject, final String pluginId, ToolBar toolBar, Composite comp, final String editorId, final String actionableItem) {
      if (!isActionReportingServiceAvailable()) {
         return;
      }

      if (actionableObject == null) {
         throw new IllegalArgumentException("actionableObject can not be null");
      }
      if (!Strings.isValid(editorId)) {
         throw new IllegalArgumentException("editorId can not be null or empty");
      }
      if (actionableItem == null) {
         throw new IllegalArgumentException("aspect can not be null");
      }

      if (toolBar != null) {
         ToolItem item = new ToolItem(toolBar, SWT.PUSH);
         item.setImage(ImageManager.getImage(PluginUiImage.BUG));
         item.setToolTipText(BUG_TITLE);
         item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               String desc = getActionDescription(editorId, pluginId, actionableObject);
               reportLogException(actionableItem, desc);
            }
         });
      } else if (comp != null) {
         Button bugButton = new Button(comp, SWT.PUSH);
         bugButton.setToolTipText(BUG_TITLE);
         bugButton.setImage(ImageManager.getImage(PluginUiImage.BUG));
         bugButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               String desc = getActionDescription(editorId, pluginId, actionableObject);
               reportLogException(actionableItem, desc);
            }

         });
      } else {
         throw new IllegalArgumentException("Can't determine bug target.");
      }
   }

   public static String getActionDescription(String itemId, String pluginId, IActionable actionable) {
      String desc = "";
      try {
         Bundle bundle = Platform.getBundle(pluginId);
         if (bundle != null) {
            String version = (String) bundle.getHeaders().get("Bundle-Version");
            desc = String.format("\n\nItem: %s\nVersion: %s", itemId, version);
         }
         String moreDesc = actionable.getActionDescription();
         if (Strings.isValid(moreDesc)) {
            if (Strings.isValid(desc)) {
               desc += "\n" + moreDesc;
            } else {
               desc = moreDesc;
            }
         }
      } catch (Exception ex) {
         OseeLog.log(OseeUiActivator.class, Level.SEVERE, ex);
      }
      return desc;
   }

   public static void addBugToViewToolbar(final ViewPart viewPart, final IActionable actionableObject, final String pluginId, final String viewId, final String actionableItem) {
      if (!isActionReportingServiceAvailable()) {
         return;
      }
      if (!Strings.isValid(viewId)) {
         throw new IllegalArgumentException("viewId can not be null or empty");
      }
      if (actionableItem == null) {
         throw new IllegalArgumentException("Aspect can not be null.");
      }
      Action bugAction = new Action("Generate Action Against This View") {
         @Override
         public void run() {
            String desc = getActionDescription(viewId, pluginId, actionableObject);
            reportLogException(actionableItem, desc);
         }
      };
      bugAction.setImageDescriptor(ImageManager.getImageDescriptor(PluginUiImage.BUG));
      bugAction.setToolTipText("Generate Action Against This View");

      IToolBarManager toolbarManager = viewPart.getViewSite().getActionBars().getToolBarManager();
      toolbarManager.add(bugAction);
   }

   public static Action createBugAction(String pluginId, IAdaptable target, String itemId, String actionableItem) {
      return new BugAction(pluginId, target, itemId, actionableItem);
   }

   private static final class BugAction extends Action {
      private static String BUG_TITLE = "Generate Action Against This Tool";
      private final String pluginId;
      private final String itemId;
      private final IAdaptable target;
      private final String actionableItem;

      public BugAction(String pluginId, IAdaptable target, String itemId, String actionableItem) {
         super(BUG_TITLE, IAction.AS_PUSH_BUTTON);
         this.pluginId = pluginId;
         this.itemId = itemId;
         this.target = target;
         this.actionableItem = actionableItem;
         setToolTipText(BUG_TITLE);
         setImageDescriptor(ImageManager.getImageDescriptor(PluginUiImage.BUG));
      }

      @Override
      public void run() {
         IActionable actionable = (IActionable) target.getAdapter(IActionable.class);
         String desc = getActionDescription(itemId, pluginId, actionable);
         reportLogException(actionableItem, desc);
      }
   }
}
