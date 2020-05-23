/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.framework.ui.skynet.util;

import org.eclipse.debug.internal.ui.viewers.model.provisional.TreeModelViewer;
import org.eclipse.debug.internal.ui.views.launch.LaunchView;
import org.eclipse.debug.ui.actions.DebugCommandAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Donald G. Dunne
 */
@SuppressWarnings("restriction")
public class CollapseAllCommandAction extends DebugCommandAction implements IViewActionDelegate {

   private IViewPart viewPart;

   public CollapseAllCommandAction() {
      setActionDefinitionId("org.eclipse.debug.ui.commands.Terminate"); //$NON-NLS-1$
   }

   @Override
   public String getText() {
      return "Collapse-All";
   }

   @Override
   public String getId() {
      return "org.eclipse.debug.ui.debugview.toolbar.collapse"; //$NON-NLS-1$
   }

   @Override
   public ImageDescriptor getDisabledImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.COLLAPSE_ALL);
   }

   @Override
   public ImageDescriptor getHoverImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.COLLAPSE_ALL);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.COLLAPSE_ALL);
   }

   @Override
   protected Class<?> getCommandType() {
      return null;
   }

   @Override
   public String getHelpContextId() {
      return null;
   }

   @Override
   public String getToolTipText() {
      return getText();
   }

   @Override
   public void run() {
      // do nothing
   }

   @Override
   public void run(IAction arg0) {
      if (viewPart != null) {
         LaunchView view = (LaunchView) viewPart;
         Viewer viewer = view.getViewer();
         if (viewer != null && viewer instanceof TreeModelViewer) {
            TreeModelViewer tmv = (TreeModelViewer) viewer;
            tmv.collapseAll();
         }
      }
   }

   @Override
   public void selectionChanged(IAction arg0, ISelection arg1) {
      // do nothing
   }

   @Override
   public void init(IViewPart viewPart) {
      this.viewPart = viewPart;
   }

}
