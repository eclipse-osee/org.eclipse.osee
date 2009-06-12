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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.define.DefinePlugin;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
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
      if (!DbConnectionExceptionComposite.dbConnectionIsOk(parent)) return;

      xNavComp = new XNavigateComposite(new DefineNavigateViewItems(), parent, SWT.NONE);
      xNavComp.getFilteredTree().getViewer().setSorter(new DefineNavigateViewerSorter());

      DefinePlugin.getInstance().setHelp(xNavComp, HELP_CONTEXT_ID);
      createActions();
      xNavComp.refresh();
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

      OseeAts.addBugToViewToolbar(this, this, DefinePlugin.getInstance(), VIEW_ID, "Define Navigator");

   }

   public String getActionDescription() {
      IStructuredSelection sel = (IStructuredSelection) xNavComp.getFilteredTree().getViewer().getSelection();
      if (sel.iterator().hasNext()) return String.format("Currently Selected - %s",
            ((XNavigateItem) sel.iterator().next()).getName());
      return "";
   }

}