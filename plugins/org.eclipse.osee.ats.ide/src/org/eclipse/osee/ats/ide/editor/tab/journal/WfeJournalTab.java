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

package org.eclipse.osee.ats.ide.editor.tab.journal;

import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.api.workflow.AtsActionEndpointApi;
import org.eclipse.osee.ats.api.workflow.journal.JournalData;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.WfeAbstractTab;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

/**
 * @author Donald G. Dunne
 */
public class WfeJournalTab extends WfeAbstractTab {
   private Composite bodyComp;
   public final static String ID = "ats.journal.tab";
   private final IAtsWorkItem workItem;
   private Browser browser;
   private final WorkflowEditor editor;
   private final AtsApiIde atsApi;
   private IManagedForm managedForm;
   private WfeJournalSubscribersComp journalComp;
   private JournalData journalData;
   private XText text;
   private Button submitButton;

   public WfeJournalTab(WorkflowEditor editor, IAtsWorkItem workItem, AtsApiIde atsApi) {
      super(editor, ID, workItem, "Journal");
      this.editor = editor;
      this.workItem = workItem;
      this.atsApi = atsApi;
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      this.managedForm = managedForm;

      try {
         bodyComp = managedForm.getForm().getBody();
         GridLayout gridLayout = new GridLayout(1, true);
         bodyComp.setLayout(gridLayout);
         GridData gd = new GridData(SWT.LEFT, SWT.LEFT, true, true);
         bodyComp.setLayoutData(gd);

         createWidgets();
         createBrowser();

         updateTitleBar(managedForm);
         createToolbar(managedForm);
         FormsUtil.addHeadingGradient(editor.getToolkit(), managedForm.getForm(), true);

         managedForm.reflow(true);

         refresh();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public void refreshTabName() {
      if (editor.isDisposed()) {
         return;
      }
      String tabName = "Journal";
      try {
         int count = AtsApiService.get().getAttributeResolver().getAttributeCount(workItem, AtsAttributeTypes.Journal);
         if (count > 0) {
            tabName = "Journal(*)";
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      setPartName(tabName);
   }

   private void createWidgets() {

      Composite composite = new Composite(bodyComp, SWT.NONE);
      composite.setLayout(ALayout.getZeroMarginLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
      getManagedForm().getToolkit().adapt(composite);

      journalComp = new WfeJournalSubscribersComp(composite, SWT.NONE, workItem, workItem.isInWork(), editor);

      text = new XText("New Entry");
      text.setVerticalLabel(true);
      text.setFillHorizontally(true);
      text.setFillVertically(true);
      text.createWidgets(composite, 1);
      GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
      gd.heightHint = 100;
      text.getStyledText().setLayoutData(gd);
      text.adaptControls(getManagedForm().getToolkit());

      submitButton = new Button(composite, SWT.PUSH);
      submitButton.setText("Submit");
      managedForm.getToolkit().adapt(submitButton, true, true);
      submitButton.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            String msg = text.get();
            if (Strings.isInValid(msg)) {
               AWorkbench.popup("Must enter entry");
            } else {
               submitButton.setEnabled(false);
               JournalData data = new JournalData();
               data.setAddMsg(msg);
               data.setUser(atsApi.getUserService().getCurrentUser());
               journalData = atsApi.getServerEndpoints().getActionEndpoint().addJournal(workItem.getAtsId(), data);
               if (journalData.getResults().isErrors()) {
                  AWorkbench.popup(journalData.getResults().toString());
               } else {
                  Set<IAtsWorkItem> workItems = Collections.singleton(workItem);
                  atsApi.getStoreService().reload(workItems);
                  atsApi.getEventService().postAtsWorkItemTopicEvent(AtsTopicEvent.WORK_ITEM_MODIFIED, workItems,
                     journalData.getTransaction());
                  journalData.setAddMsg("");
                  journalData.getResults().clear();
                  journalData.setTransaction(TransactionId.SENTINEL);
               }
            }
            text.getStyledText().setFocus();
         }
      });
   }

   private void createBrowser() {
      browser = new Browser(bodyComp, SWT.NONE);
      GridData gd2 = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd2.widthHint = 200;
      gd2.heightHint = 300;
      browser.setLayoutData(gd2);
   }

   @Override
   public void refresh() {
      if (Widgets.isAccessible(journalComp)) {
         IAtsWorkItem workItem = editor.getWorkItem();
         String msgStr = "No Entries";
         try {
            AtsActionEndpointApi actionUiEp = AtsApiService.get().getServerEndpoints().getActionEndpoint();
            journalData = actionUiEp.getJournalData(workItem.getAtsId());
            if (journalData != null) {
               if (journalData.getResults().isErrors()) {
                  msgStr = journalData.getResults().toString();
               } else {
                  msgStr = journalData.getCurrentMsg();
               }
            }
         } catch (Exception ex) {
            msgStr = AHTML.simplePage(Lib.exceptionToString(ex));
         }
         String fMsgStr = msgStr;
         Displays.ensureInDisplayThread(new Runnable() {

            @Override
            public void run() {
               if (Widgets.isAccessible(browser)) {

                  journalComp.refresh();
                  browser.setText(AHTML.textToHtml(fMsgStr));
                  getManagedForm().reflow(true);
                  refreshTabName();
                  submitButton.setEnabled(true);
               }
            }
         });
      }
   }

}
