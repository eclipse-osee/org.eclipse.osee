/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.explorer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.branch.ViewApplicabilityUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ViewBranchViewFilterTreeDialog;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormText;

/**
 * @author Megumi Telles
 */
public class ArtifactExplorerViewApplicability {

   private final Composite parent;
   private FormText text;
   private Button button;
   private SelectionAdapter changeableAdapter;
   private final ArtifactExplorer explorer;

   public ArtifactExplorerViewApplicability(Composite parent, ArtifactExplorer explorer) {
      this.parent = parent;
      this.explorer = explorer;
   }

   public void create() {
      Composite applicabilityComp = new Composite(parent, SWT.WRAP);
      applicabilityComp.setLayout(ALayout.getZeroMarginLayout(2, false));
      applicabilityComp.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, true, false));

      button = new Button(applicabilityComp, SWT.PUSH);
      button.setText("Set View");
      setButtonChangeable();

      text = new FormText(applicabilityComp, SWT.WRAP);
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.widthHint = 200;
      text.setLayoutData(gd);
      text.setText(getArtifactViewApplicabiltyText(), true, false);
      text.setForeground(Displays.getSystemColor(SWT.COLOR_DARK_GRAY));

   }

   public void refresh() {
      text.setText(getArtifactViewApplicabiltyText(), true, false);
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
      String viewName = "Not Set";
      if (explorer != null) {
         BranchId branch = explorer.getBranch();
         if (branch.isValid()) {
            if (!ViewApplicabilityUtil.isBranchOfProductLine(branch)) {
               button.setEnabled(false);
            } else {
               button.setEnabled(true);
               ArtifactId viewId = explorer.getViewId();
               if (viewId.isValid()) {
                  viewName = ArtifactQuery.getArtifactTokenFromId(explorer.getBranch(), viewId).getName();
               }
            }
         }
      }
      return String.format("<form><p>%s</p></form>", viewName);
   }

   private boolean changeView() {
      Map<Long, String> branchViews = ViewApplicabilityUtil.getBranchViews(explorer.getBranch());
      ViewBranchViewFilterTreeDialog dialog =
         new ViewBranchViewFilterTreeDialog("Select Branch View", "Select Branch View", branchViews);
      Collection<String> values = new ArrayList<>();
      values.add("<Clear View Selection>");
      values.addAll(branchViews.values());
      dialog.setInput(values);
      dialog.setMultiSelect(false);
      int result = dialog.open();
      if (result == Window.OK) {
         explorer.setViewId(ArtifactId.valueOf(dialog.getSelection()));
         return true;
      }
      return false;
   }

}
