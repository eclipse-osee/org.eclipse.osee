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
package org.eclipse.osee.framework.ui.skynet.change;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xchange.ChangeXViewer;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Ryan D. Brooks
 */
public class ChangeReportPage extends FormPage {
   private static String HELP_CONTEXT_ID = "ChangeView";

   private ChangeReportTable changeReportTable;
   private ChangeReportInfo infoWidget;

   public ChangeReportPage(ChangeReportEditor editor) {
      super(editor, "change.report", "Change Report");
   }

   @Override
   public void showBusy(boolean busy) {
      super.showBusy(busy);
      if (Widgets.isAccessible(getManagedForm().getForm())) {
         getManagedForm().getForm().getForm().setBusy(busy);
      }
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      final ScrolledForm form = managedForm.getForm();
      final FormToolkit toolkit = managedForm.getToolkit();

      GridLayout layout = new GridLayout();
      layout.numColumns = 1;
      layout.marginHeight = 10;
      layout.marginWidth = 6;
      layout.horizontalSpacing = 20;
      form.getBody().setLayout(layout);
      form.getBody().setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));

      updateTitle(form);
      updateImage(form);

      managedForm.getMessageManager().setAutoUpdate(false);

      ChangeUiData uiData = getEditorInput().getChangeData();
      this.changeReportTable = new ChangeReportTable(uiData);
      this.infoWidget = new ChangeReportInfo(uiData);

      int sectionStyle = Section.TITLE_BAR | Section.EXPANDED | Section.TWISTIE;
      managedForm.addPart(new EditorSection(infoWidget, "Info", form.getBody(), managedForm.getToolkit(), sectionStyle,
            false));

      managedForm.addPart(new EditorSection(changeReportTable, "Changes", form.getBody(), managedForm.getToolkit(),
            sectionStyle, true));

      addToolBar(toolkit, form, true);
      managedForm.refresh();
      form.layout();

      PlatformUI.getWorkbench().getHelpSystem().setHelp(form.getBody(),
            "org.eclipse.osee.framework.help.ui." + HELP_CONTEXT_ID);
      bindMenu();
   }

   private void bindMenu() {
      final ChangeXViewer xviewer = changeReportTable.getXViewer();

      MenuManager manager = xviewer.getMenuManager();
      manager.setRemoveAllWhenShown(true);
      manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
      manager.addMenuListener(new ChangeReportMenuListener());

      Control control = xviewer.getTree();
      Menu menu = manager.createContextMenu(control);
      control.setMenu(menu);

      getSite().registerContextMenu("org.eclipse.osee.framework.ui.skynet.widgets.xchange.ChangeView", manager, xviewer);
      getSite().setSelectionProvider(xviewer);
   }

   private static final class ChangeReportMenuListener implements IMenuListener {
      public void menuAboutToShow(IMenuManager manager) {
         MenuManager menuManager = (MenuManager) manager;
         menuManager.insertBefore(XViewer.MENU_GROUP_PRE, new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
         //         IContributionItem[] items = menuManager.getItems();
         //         int lookingForItemsAt = items.length - 4;
         //         for (int index = 0; index < items.length; index++) {
         //            IContributionItem item = items[index];
         //            if (index > lookingForItemsAt && !XViewer.MENU_GROUP_POST.equals(item.getId())) {
         //               System.out.println(item.getId() + " " + item.getClass().getSimpleName());
         //               menuManager.remove(item);
         //            }
         //         }
         //            menuManager.add(new Separator());
         //            menuManager.add(new TableCustomizationAction(xviewer));
         //            menuManager.add(new ViewTableReportAction(xviewer));
         //            menuManager.add(new ViewSelectedCellDataAction(xviewer));
      }
   }

   private void updateTitle(ScrolledForm form) {
      form.setText(getEditorInput().getName());
   }

   private void updateImage(ScrolledForm form) {
      form.setImage(getEditor().getEditorInput().getImage());
   }

   private void addToolBar(FormToolkit toolkit, ScrolledForm form, boolean add) {
      IToolBarManager manager = form.getToolBarManager();
      if (add) {
         getEditor().getActionBarContributor().contributeToToolBar(manager);
         manager.add(changeReportTable.getXViewer().getCustomizeAction());
         manager.update(true);
      } else {
         manager.removeAll();
      }
      form.reflow(true);
   }

   @Override
   public ChangeReportEditor getEditor() {
      return (ChangeReportEditor) super.getEditor();
   }

   @Override
   public ChangeReportEditorInput getEditorInput() {
      return (ChangeReportEditorInput) super.getEditorInput();
   }

   public void onLoad() {
      if (changeReportTable != null && infoWidget != null) {
         changeReportTable.onLoading();
         infoWidget.onLoading();
      }
   }

   public void refresh() {
      final ScrolledForm sForm = getManagedForm().getForm();
      for (IFormPart part : getManagedForm().getParts()) {
         part.refresh();
      }
      sForm.getBody().layout(true);
      sForm.reflow(true);
      getManagedForm().refresh();
   }
}