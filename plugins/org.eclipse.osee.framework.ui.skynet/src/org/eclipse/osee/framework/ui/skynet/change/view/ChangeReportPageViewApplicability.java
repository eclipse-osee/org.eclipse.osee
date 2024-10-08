/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.framework.ui.skynet.change.view;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.branch.ViewApplicabilityUtil;
import org.eclipse.osee.framework.ui.skynet.change.ChangeReportEditorInput;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiData;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ViewBranchViewFilterTreeDialog;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Megumi Telles
 */
public class ChangeReportPageViewApplicability {

   private final FormToolkit toolkit;
   private final ScrolledForm form;
   private FormText text;
   private Button button;
   private SelectionAdapter changeableAdapter;
   private final ChangeReportEditor editor;

   public ChangeReportPageViewApplicability(ChangeReportEditor editor, FormToolkit toolkit, ScrolledForm form) {
      this.toolkit = toolkit;
      this.form = form;
      this.editor = editor;
   }

   public void create() {
      Composite applicabilityComp = toolkit.createComposite(form.getForm().getBody(), SWT.WRAP);
      applicabilityComp.setLayout(ALayout.getZeroMarginLayout(2, false));
      applicabilityComp.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, true, false));

      text = toolkit.createFormText(applicabilityComp, false);
      text.setText(getArtifactViewApplicabiltyText(), true, false);
      text.setForeground(Displays.getSystemColor(SWT.COLOR_DARK_GRAY));

      button = toolkit.createButton(applicabilityComp, "", SWT.PUSH);
      button.setText("Set View");
      setButtonChangeable();
   }

   public void refresh() {
      text.setText(getArtifactViewApplicabiltyText(), true, false);
      editor.refresh();
   }

   private void setButtonChangeable() {
      button.addSelectionListener(getChangeableAdapter());
   }

   private SelectionAdapter getChangeableAdapter() {
      if (changeableAdapter == null) {
         changeableAdapter = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               super.widgetSelected(e);
               changeView();
               refresh();
            }
         };
      }
      return changeableAdapter;
   }

   private String getArtifactViewApplicabiltyText() {
      ArtifactId view = editor.getViewId();
      String viewText =
         view.isValid() ? ArtifactQuery.getArtifactTokenFromId(getBranch(), view).getName() : Widgets.NOT_SET;
      return "<form><p><b>Branch View:</b> " + viewText + "</p></form>";
   }

   private BranchToken getBranch() {
      Conditions.assertNotNull(editor, "Change Editor");
      ChangeReportEditorInput editorInput = editor.getEditorInput();
      BranchToken branch = BranchToken.SENTINEL;
      if (editorInput != null) {
         branch = editorInput.getBranch();
         if (branch == null) {
            ChangeUiData changeData = editorInput.getChangeData();
            if (changeData != null) {
               if (!changeData.getChanges().isEmpty()) {
                  branch = changeData.getChanges().iterator().next().getBranch();
               }
            }
         }
         if (branch == null && button != null && !button.isDisposed()) {
            button.setEnabled(false);
         }
      }
      // bug[tw23643] resolves UI issue of not displaying transaction(s) change report on working branches
      if (branch == null) {
         branch = BranchToken.SENTINEL;
      }
      return branch;
   }

   private boolean changeView() {
      Collection<ArtifactToken> branchViews = ViewApplicabilityUtil.getBranchViewTokens(getBranch());
      ViewBranchViewFilterTreeDialog dialog =
         new ViewBranchViewFilterTreeDialog("Branch View", "Branch View", branchViews);
      Collection<ArtifactToken> values = new ArrayList<>();
      values.add(ArtifactToken.CLEAR_SELECTION);
      values.addAll(branchViews);
      dialog.setInput(values);
      dialog.setMultiSelect(false);
      int result = dialog.open();
      if (result == Window.OK) {
         editor.setViewId(dialog.getSelection());
         return true;
      }
      return false;
   }
}
