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

import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IRegistryEventListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Donald G. Dunne
 */
public class CoverageNavigateView extends ViewPart implements IActionable {

   public static final String VIEW_ID = "org.eclipse.osee.coverage.navigate.CoverageNavigateView";
   private XNavigateComposite xNavComp;

   public CoverageNavigateView() {
   }

   @Override
   public void setFocus() {
   }

   @Override
   public void createPartControl(Composite parent) {
      xNavComp = new XNavigateComposite(new CoverageNavigateViewItems(), parent, SWT.NONE);

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
      }, "org.eclipse.osee.coverage.CoverageNavigateItem");
   }

   protected void createActions() {
      Action refreshAction = new Action("Refresh") {

         @Override
         public void run() {
            xNavComp.refresh();
         }
      };
      refreshAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.REFRESH));
      refreshAction.setToolTipText("Refresh");

      OseeAts.addBugToViewToolbar(this, this, Activator.getInstance(), VIEW_ID, "Coverage Navigator");

   }

   public String getActionDescription() {
      IStructuredSelection sel = (IStructuredSelection) xNavComp.getFilteredTree().getViewer().getSelection();
      if (sel.iterator().hasNext()) return String.format("Currently Selected - %s",
            ((XNavigateItem) sel.iterator().next()).getName());
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