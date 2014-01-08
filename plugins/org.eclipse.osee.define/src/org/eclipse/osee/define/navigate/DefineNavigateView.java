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
import org.eclipse.osee.framework.help.ui.OseeHelpContext;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.plugin.xnavigate.IXNavigateEventListener;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateEventManager;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.GenericViewPart;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * Insert the type's description here.
 * 
 * @see ViewPart
 */
public class DefineNavigateView extends GenericViewPart implements IXNavigateEventListener {

   public static final String VIEW_ID = "org.eclipse.osee.define.DefineNavigateView";
   private XNavigateComposite xNavComp;

   @Override
   public void refresh(XNavigateItem item) {
      if (DbConnectionExceptionComposite.dbConnectionIsOk()) {
         if (xNavComp != null && Widgets.isAccessible(xNavComp.getFilteredTree()) && Widgets.isAccessible(xNavComp.getFilteredTree().getViewer().getTree())) {
            xNavComp.getFilteredTree().getViewer().refresh(item);
         }
      }
   }

   /*
    * @see IWorkbenchPart#createPartControl(Composite)
    */
   @Override
   public void createPartControl(Composite parent) {
      if (DbConnectionExceptionComposite.dbConnectionIsOk(parent)) {

         xNavComp = new XNavigateComposite(new DefineNavigateViewItems(), parent, SWT.NONE);
         XNavigateEventManager.register(this);
         HelpUtil.setHelp(xNavComp, OseeHelpContext.DEFINE_NAVIGATOR);
         createActions();
         xNavComp.refresh();
         addExtensionPointListenerBecauseOfWorkspaceLoading();
         setFocusWidget(xNavComp);

      }
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

   }

}