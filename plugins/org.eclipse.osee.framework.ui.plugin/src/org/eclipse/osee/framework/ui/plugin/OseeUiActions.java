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
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.IActionReportingService;
import org.eclipse.osee.framework.plugin.core.IActionable;
import org.eclipse.osee.framework.ui.plugin.internal.OseePluginUiActivator;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Version;

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

   public static void addButtonToEditorToolBar(final MultiPageEditorPart editorPart, IActionable actionableObject, final OseeUiActivator oseePlugin, IToolBarManager toolBar, final String editorId, final String actionableItem) {
      if (!isActionReportingServiceAvailable()) {
         return;
      }
      Action bugAction = new Action(BUG_TITLE, Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            String version = (String) oseePlugin.getBundle().getHeaders().get("Bundle-Version");
            String desc = String.format("Found in \"%s\" version %s.", editorId, version);
            if (editorPart instanceof IActionable) {
               String moreDesc = ((IActionable) editorPart).getActionDescription();
               if (moreDesc != null && !moreDesc.equals("")) {
                  desc += "\n" + moreDesc;
               }
            }
            reportLogException(actionableItem, desc);
         }
      };
      bugAction.setToolTipText(BUG_TITLE);
      bugAction.setImageDescriptor(ImageManager.getImageDescriptor(PluginUiImage.BUG));
      toolBar.add(bugAction);
   }

   public static void addButtonToEditorToolBar(IActionable actionableObject, final OseeUiActivator oseePlugin, ToolBar toolBar, final String editorId, String actionableItem) {
      addButtonToEditorToolBar(actionableObject, oseePlugin, toolBar, null, editorId, actionableItem);
   }

   public static void addButtonToEditorToolBar(IActionable actionableObject, final OseeUiActivator oseePlugin, Composite comp, final String editorId, String actionableItem) {
      addButtonToEditorToolBar(actionableObject, oseePlugin, null, comp, editorId, actionableItem);
   }

   private static void addButtonToEditorToolBar(final IActionable actionableObject, final OseeUiActivator oseePlugin, ToolBar toolBar, Composite comp, final String editorId, final String actionableItem) {
      if (!isActionReportingServiceAvailable()) {
         return;
      }

      if (actionableObject == null) {
         throw new IllegalArgumentException(String.format("actionableObject can not be null"));
      }
      if (editorId == null || editorId.equals("")) {
         throw new IllegalArgumentException(String.format("editorId can not be null or empty"));
      }
      if (actionableItem == null) {
         throw new IllegalArgumentException(String.format("aspect can not be null"));
      }

      if (toolBar != null) {
         ToolItem item = new ToolItem(toolBar, SWT.PUSH);
         item.setImage(ImageManager.getImage(PluginUiImage.BUG));
         item.setToolTipText(BUG_TITLE);
         item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               String version = (String) oseePlugin.getBundle().getHeaders().get("Bundle-Version");
               String desc = String.format("\n\nItem: %s\nVersion: %s", editorId, version);
               if (actionableObject != null) {
                  String moreDesc = actionableObject.getActionDescription();
                  if (moreDesc != null && !moreDesc.equals("")) {
                     desc += "\n" + moreDesc;
                  }
               }
               reportLogException(actionableItem, desc);
            }
         });
      } else if (comp != null) {
         Button bugButton = new Button(comp, SWT.PUSH);
         bugButton.setToolTipText(BUG_TITLE);
         bugButton.setImage(ImageManager.getImage(PluginUiImage.BUG));
         bugButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
               String version = (String) oseePlugin.getBundle().getHeaders().get("Bundle-Version");
               String desc = String.format("\n\nItem: %s\nVersion: %s", editorId, version);
               if (actionableObject != null) {
                  String moreDesc = actionableObject.getActionDescription();
                  if (moreDesc != null && !moreDesc.equals("")) {
                     desc += "\n" + moreDesc;
                  }
               }
               reportLogException(actionableItem, desc);
            }

         });
      } else {
         throw new IllegalArgumentException("Can't determine bug target.");
      }
   }

   public static void addBugToViewToolbar(final ViewPart viewPart, final IActionable actionableObject, final OseeUiActivator oseePlugin, final String viewId, final String actionableItem) {
      if (!isActionReportingServiceAvailable()) {
         return;
      }
      if (viewId == null || viewId.equals("")) {
         throw new IllegalArgumentException(String.format("viewId can not be null or empty"));
      }
      if (actionableItem == null) {
         throw new IllegalArgumentException("Aspect can not be null.");
      }
      Action bugAction = new Action("Generate Action Against This View") {
         @Override
         public void run() {
            String version = (String) oseePlugin.getBundle().getHeaders().get("Bundle-Version");
            String desc = String.format("\n\nItem: %s\nVersion: %s", viewId, version);
            String moreDesc = actionableObject.getActionDescription();
            if (!moreDesc.equals("")) {
               desc += "\n" + moreDesc;
            }
            reportLogException(actionableItem, desc);
         }
      };
      bugAction.setImageDescriptor(ImageManager.getImageDescriptor(PluginUiImage.BUG));
      bugAction.setToolTipText("Generate Action Against This View");

      IToolBarManager toolbarManager = viewPart.getViewSite().getActionBars().getToolBarManager();
      toolbarManager.add(bugAction);
   }

   public static Action createBugAction(OseeUiActivator oseePlugin, IAdaptable target, String itemId, String actionableItem) {
      return new BugAction(oseePlugin, target, itemId, actionableItem);
   }

   private static final class BugAction extends Action {
      private static String BUG_TITLE = "Generate Action Against This Tool";
      private final OseeUiActivator oseePlugin;
      private final String itemId;
      private final IAdaptable target;
      private final String actionableItem;

      public BugAction(OseeUiActivator oseePlugin, IAdaptable target, String itemId, String actionableItem) {
         super(BUG_TITLE, Action.AS_PUSH_BUTTON);
         this.oseePlugin = oseePlugin;
         this.itemId = itemId;
         this.target = target;
         this.actionableItem = actionableItem;
         setToolTipText(BUG_TITLE);
         setImageDescriptor(ImageManager.getImageDescriptor(PluginUiImage.BUG));
      }

      @Override
      public void run() {
         Version version =
               new Version(
                     (String) oseePlugin.getBundle().getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION));
         String desc = String.format("Found in \"%s\" version %s.", itemId, version);
         IActionable actionable = (IActionable) target.getAdapter(IActionable.class);
         if (actionable != null) {
            String moreDesc = actionable.getActionDescription();
            if (moreDesc != null && !moreDesc.equals("")) {
               desc += "\n" + moreDesc;
            }
         }
         reportLogException(actionableItem, desc);
      }
   }
}
