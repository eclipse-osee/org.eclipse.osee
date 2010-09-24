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
package org.eclipse.osee.define.navigate;

import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IRegistryEventListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.define.DefinePlugin;
import org.eclipse.osee.framework.plugin.core.IActionable;
import org.eclipse.osee.framework.ui.plugin.OseeUiActions;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * Insert the type's description here.
 * 
 * @see ViewPart
 */
public class DefineNavigateView extends ViewPart implements IActionable {

   public static final String VIEW_ID = "org.eclipse.osee.define.DefineNavigateView";
   public static final String HELP_CONTEXT_ID = "defineNavigator";
   private XNavigateComposite xNavComp;

   /**
    * The constructor.
    */
   public DefineNavigateView() {
   }

   @Override
   public void setFocus() {
   }

   /*
    * @see IWorkbenchPart#createPartControl(Composite)
    */
   @Override
   public void createPartControl(Composite parent) {
      if (!DbConnectionExceptionComposite.dbConnectionIsOk(parent)) {
         return;
      }

      xNavComp = new XNavigateComposite(new DefineNavigateViewItems(), parent, SWT.NONE);
      xNavComp.getFilteredTree().getViewer().setSorter(new DefineNavigateViewerSorter());

      HelpUtil.setHelp(xNavComp, HELP_CONTEXT_ID, "org.eclipse.osee.define.help.ui");
      createActions();
      xNavComp.refresh();
      addExtensionPointListenerBecauseOfWorkspaceLoading();
   }

   private void addExtensionPointListenerBecauseOfWorkspaceLoading() {
      IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
      extensionRegistry.addListener(new IRegistryEventListener() {
         @Override
         public void added(IExtension[] extensions) {
            xNavComp.refresh();
         }

         @Override
         public void added(IExtensionPoint[] extensionPoints) {
            xNavComp.refresh();
         }

         @Override
         public void removed(IExtension[] extensions) {
            xNavComp.refresh();
         }

         @Override
         public void removed(IExtensionPoint[] extensionPoints) {
            xNavComp.refresh();
         }
      }, "org.eclipse.osee.framework.ui.skynet.BlamOperation");
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

      OseeUiActions.addBugToViewToolbar(this, this, DefinePlugin.PLUGIN_ID, VIEW_ID, "Define Navigator");

   }

   @Override
   public String getActionDescription() {
      IStructuredSelection sel = (IStructuredSelection) xNavComp.getFilteredTree().getViewer().getSelection();
      if (sel.iterator().hasNext()) {
         return String.format("Currently Selected - %s", ((XNavigateItem) sel.iterator().next()).getName());
      }
      return "";
   }

}