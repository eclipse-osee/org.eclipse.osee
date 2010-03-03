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
package org.eclipse.osee.ote.ui.navigate;

import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IRegistryEventListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.plugin.core.IActionable;
import org.eclipse.osee.framework.ui.plugin.OseeUiActions;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.ote.ui.internal.TestCoreGuiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * Insert the type's description here.
 * 
 * @see ViewPart
 */
public class OteNavigateView extends ViewPart implements IActionable {

   public static final String VIEW_ID = "org.eclipse.osee.ote.ui.navigate.OteNavigateView";
   private XNavigateComposite xNavComp;

   /**
    * The constructor.
    */
   public OteNavigateView() {
   }

   @Override
   public void setFocus() {
   }

   /*
    * @see IWorkbenchPart#createPartControl(Composite)
    */
   @Override
   public void createPartControl(Composite parent) {
      PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, "org.eclipse.osee.ote.ui.oteNavigator");
      xNavComp = new XNavigateComposite(new OteNavigateViewItems(), parent, SWT.NONE);

      xNavComp.getFilteredTree().getViewer().setSorter(new OteNavigateViewerSorter());

      createActions();
      xNavComp.refresh();

      addExtensionPointListenerBecauseOfWorkspaceLoading();
   }

   private void addExtensionPointListenerBecauseOfWorkspaceLoading() {
      IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
      extensionRegistry.addListener(new IRegistryEventListener() {
         @Override
         public void added(IExtension[] extensions) {
            refresh();
         }

         @Override
         public void added(IExtensionPoint[] extensionPoints) {
            refresh();
         }

         @Override
         public void removed(IExtension[] extensions) {
            refresh();
         }

         @Override
         public void removed(IExtensionPoint[] extensionPoints) {
            refresh();
         }
      }, "org.eclipse.osee.framework.ui.plugin.XNavigateItem");
   }

   protected void createActions() {
      Action refreshAction = new Action("Refresh") {

         @Override
         public void run() {
            xNavComp.refresh();
         }
      };
      refreshAction.setImageDescriptor(ImageManager.getImageDescriptor(PluginUiImage.REFRESH));
      refreshAction.setToolTipText("Refresh");

      OseeUiActions.addBugToViewToolbar(this, this, TestCoreGuiPlugin.getDefault(), VIEW_ID, "OTE Navigator");

   }

   public String getActionDescription() {
      IStructuredSelection sel = (IStructuredSelection) xNavComp.getFilteredTree().getViewer().getSelection();
      if (sel.iterator().hasNext()) {
         return String.format("Currently Selected - %s", ((XNavigateItem) sel.iterator().next()).getName());
      }
      return "";
   }

   public void refresh() {
      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            xNavComp.refresh();
         }
      });
   }
}