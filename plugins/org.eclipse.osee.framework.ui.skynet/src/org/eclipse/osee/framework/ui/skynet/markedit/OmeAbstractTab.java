/*******************************************************************************
 * Copyright (c) 2023 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.markedit;

import java.util.logging.Level;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction.IRefreshActionHandler;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.markedit.model.AbstractOmeData;
import org.eclipse.osee.framework.ui.skynet.util.LoadingComposite;
import org.eclipse.osee.framework.ui.swt.ExceptionComposite;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

/**
 * @author Donald G. Dunne
 */
public abstract class OmeAbstractTab extends FormPage implements IOmeEditorTab {
   protected final FormEditor editor;
   protected LoadingComposite loadingComposite;
   protected Composite bodyComp;
   protected final AbstractOmeData omeData;

   public OmeAbstractTab(FormEditor editor, String id, AbstractOmeData omeData, String tabName) {
      super(editor, id, tabName);
      this.editor = editor;
      this.omeData = omeData;
   }

   public void updateTitleBar(IManagedForm managedForm) {
      if (managedForm != null && Widgets.isAccessible(managedForm.getForm())) {

         String titleString = omeData.getEditorName();
         String displayableTitle = Strings.escapeAmpersands(titleString);
         managedForm.getForm().setToolTipText(displayableTitle);
         managedForm.getForm().setText(displayableTitle);
         managedForm.getForm().setImage(ImageManager.getImage(FrameworkImage.OSEE_MARKDOWN_EDIT));
      }
   }

   @Override
   public void showBusy(boolean busy) {
      super.showBusy(busy);
      IManagedForm managedForm = getManagedForm();
      if (managedForm != null && Widgets.isAccessible(getManagedForm().getForm())) {
         getManagedForm().getForm().getForm().setBusy(busy);
      }
   }

   public IToolBarManager createToolbar(IManagedForm managedForm) {
      IToolBarManager toolBarMgr = managedForm.getForm().getToolBarManager();
      toolBarMgr.add(new RefreshAction(new IRefreshActionHandler() {

         @Override
         public void refreshActionHandler() {
            handleRefreshAction();
         }
      }));

      managedForm.getForm().updateToolBar();
      return toolBarMgr;
   }

   public void handleRefreshAction() {
      // do nothing
   }

   public void handleException(Exception ex) {
      setLoading(false);
      OseeLog.log(Activator.class, Level.SEVERE, ex);
      new ExceptionComposite(bodyComp, ex);
      bodyComp.layout();
   }

   public void setLoading(boolean set) {
      if (set) {
         loadingComposite = new LoadingComposite(bodyComp);
         bodyComp.layout();
      } else {
         if (Widgets.isAccessible(loadingComposite)) {
            loadingComposite.dispose();
         }
      }
      showBusy(set);
   }

   public void refresh() {
      // do nothing
   }

}