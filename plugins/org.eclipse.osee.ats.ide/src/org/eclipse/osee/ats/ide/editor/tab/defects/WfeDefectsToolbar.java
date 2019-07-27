/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor.tab.defects;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.ide.util.widgets.defect.DefectUtil;
import org.eclipse.osee.ats.ide.util.widgets.defect.DefectXViewer;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction.IRefreshActionHandler;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class WfeDefectsToolbar {

   private final ScrolledForm scrolledForm;
   private final IAtsPeerToPeerReview review;
   private final DefectXViewer defectXViewer;
   private DefectUtil defectUtil;
   private Action importDefectsAction;
   private final IRefreshActionHandler refreshActionHandler;

   public WfeDefectsToolbar(ScrolledForm scrolledForm, DefectXViewer defectXViewer, IAtsPeerToPeerReview review, IRefreshActionHandler refreshActionHandler) {
      this.scrolledForm = scrolledForm;
      this.defectXViewer = defectXViewer;
      this.review = review;
      this.refreshActionHandler = refreshActionHandler;
   }

   public DefectUtil getDefectUtil() {
      if (defectUtil == null) {
         defectUtil = new DefectUtil(defectXViewer, review, refreshActionHandler);
      }
      return defectUtil;
   }

   public void build() {
      IToolBarManager toolBarMgr = scrolledForm.getToolBarManager();
      toolBarMgr.removeAll();
      toolBarMgr.add(new NewDefectAction());
      toolBarMgr.add(new DeleteDefectAction());
      toolBarMgr.add(new Separator());
      toolBarMgr.add(new RefreshAction(refreshActionHandler));
      toolBarMgr.add(new Separator());
      toolBarMgr.add(defectXViewer.getCustomizeAction());
      createDropDownMenuActions();
      toolBarMgr.add(new DropDownAction());
      scrolledForm.updateToolBar();
   }

   private class NewDefectAction extends Action {

      @Override
      public void run() {
         getDefectUtil().handleNewDefect();
      }

      @Override
      public ImageDescriptor getImageDescriptor() {
         return ImageManager.getImageDescriptor(FrameworkImage.GREEN_PLUS);
      }

      @Override
      public String getText() {
         return "New Defect";
      }

      @Override
      public boolean isEnabled() {
         return review.isInWork();
      }

   }

   private class DeleteDefectAction extends Action {

      @Override
      public void run() {
         getDefectUtil().handleDeleteDefect(true);
      }

      @Override
      public ImageDescriptor getImageDescriptor() {
         return ImageManager.getImageDescriptor(FrameworkImage.X_RED);
      }

      @Override
      public String getText() {
         return "Delete Defect";
      }

      @Override
      public boolean isEnabled() {
         return review.isInWork();
      }

   }

   private void createDropDownMenuActions() {

      importDefectsAction = new Action("Import Defects from Simple List", IAction.AS_PUSH_BUTTON) {

         @Override
         public void run() {
            getDefectUtil().handleImportDefectsViaList();
         }
      };
      importDefectsAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.IMPORT));
   }

   public class DropDownAction extends Action implements IMenuCreator {
      private Menu fMenu;

      public DropDownAction() {
         setText("Other");
         setMenuCreator(this);
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.GEAR));
      }

      @Override
      public Menu getMenu(Control parent) {
         if (fMenu != null) {
            fMenu.dispose();
         }

         fMenu = new Menu(parent);

         addActionToMenu(fMenu, importDefectsAction);

         return fMenu;
      }

      @Override
      public void dispose() {
         if (fMenu != null) {
            fMenu.dispose();
            fMenu = null;
         }
      }

      @Override
      public Menu getMenu(Menu parent) {
         return null;
      }

      protected void addActionToMenu(Menu parent, Action action) {
         ActionContributionItem item = new ActionContributionItem(action);
         item.fill(parent, -1);
      }

      void clear() {
         dispose();
      }

   }

}
