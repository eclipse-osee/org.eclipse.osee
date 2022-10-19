/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.framework.ui.skynet.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.branch.ViewApplicabilityUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ViewBranchViewFilterTreeDialog;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Audrey Denk
 */
public class QuickSearchViewApplicability {

   private final Composite parent;
   private Text text;
   private Label label;
   private Button checkBox;
   private Button button;
   private SelectionAdapter changeableAdapter;
   private final QuickSearchView quickSearch;

   public QuickSearchViewApplicability(Composite parent, QuickSearchView explorer) {
      this.parent = parent;
      this.quickSearch = explorer;
   }

   public void create() {
      Composite applicabilityComp = new Composite(parent, SWT.WRAP);
      applicabilityComp.setLayout(ALayout.getZeroMarginLayout());

      applicabilityComp.setLayout(ALayout.getZeroMarginLayout(4, false));
      applicabilityComp.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, true, false));
      checkBox = new Button(applicabilityComp, SWT.CHECK);
      checkBox.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            Object object = e.getSource();
            if (object instanceof Button) {
               Button button = (Button) object;
               if (button.getSelection()) {
                  quickSearch.setApplicabilityCheckBox(false);
               }
               quickSearch.updateWidgetEnablements();
            }
         }
      });
      label = new Label(applicabilityComp, SWT.NONE);
      label.setText("View:");
      button = new Button(applicabilityComp, SWT.PUSH);
      button.setImage(ImageManager.getImage(FrameworkImage.GEAR));
      setButtonChangeable();

      text = new Text(applicabilityComp, SWT.READ_ONLY | SWT.BORDER);
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.widthHint = 200;
      text.setLayoutData(gd);
      text.setText(getArtifactViewApplicabiltyText());

   }

   public void refresh() {
      text.setText(getArtifactViewApplicabiltyText());
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
      String viewName = Widgets.NOT_SET;
      if (quickSearch != null) {
         BranchId branch = quickSearch.getBranch();
         if (branch.isValid()) {
            if (!ViewApplicabilityUtil.isBranchOfProductLine(branch)) {
               button.setEnabled(false);
               checkBox.setSelection(false);
               checkBox.setEnabled(false);
            } else {
               button.setEnabled(true);
               checkBox.setEnabled(true);
               ArtifactId viewId = quickSearch.getViewId();
               if (viewId.isValid()) {
                  viewName = ArtifactQuery.getArtifactTokenFromId(quickSearch.getBranch(), viewId).getName();
               }
            }
         }
      }
      return viewName;
   }

   private boolean changeView() {
      Map<Long, String> branchViews = ViewApplicabilityUtil.getBranchViews(quickSearch.getBranch());
      ViewBranchViewFilterTreeDialog dialog =
         new ViewBranchViewFilterTreeDialog("Select Branch View", "Select Branch View", branchViews);
      Collection<String> values = new ArrayList<>();
      values.add("<Clear View Selection>");
      values.addAll(branchViews.values());
      dialog.setInput(values);
      dialog.setMultiSelect(false);
      int result = dialog.open();
      if (result == Window.OK) {
         quickSearch.setViewId(ArtifactId.valueOf(dialog.getSelection()));
         if (quickSearch.getViewId().isInvalid()) {
            setCheckBox(false);
         }
         return true;
      }
      return false;
   }

   public void setCheckBox(boolean selected) {
      checkBox.setSelection(selected);
      if (selected) {
         quickSearch.setViewCheckBox(false);
      }
   }

   public boolean getCheckBox() {
      return checkBox.getSelection();
   }
}
