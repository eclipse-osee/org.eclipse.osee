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
package org.eclipse.osee.framework.ui.skynet.search.page.widget;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.ui.skynet.search.page.SkynetArtifactAdapter;
import org.eclipse.osee.framework.ui.swt.SearchCCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

public class RevisionSelectionWidget extends Composite implements IViewer {

   private static final String DEFAULT_BRANCH_STRING = "NO BRANCH AVAILABLE";
   private SearchCCombo branchCombo;
   private Text revisionField;
   private Map<String, Map<String, Integer>> branches;

   public RevisionSelectionWidget(Composite parent, int style) {
      super(parent, style);
      branches = new HashMap<String, Map<String, Integer>>();
      createControl();
   }

   public void createControl() {
      this.setLayout(new GridLayout(2, false));
      this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      createBranchSelectionCombo(this);
      createRevisionSelection(this);

      refresh();
   }

   private void createBranchSelectionCombo(Composite parent) {
      branchCombo = new SearchCCombo(parent, SWT.BORDER);
      branchCombo.setLayout(new GridLayout());
      branchCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      branchCombo.setText(DEFAULT_BRANCH_STRING);
      branchCombo.setToolTipText("Select the Branch to search.");

   }

   private void createRevisionSelection(Composite parent) {
      revisionField = new Text(parent, SWT.BORDER | SWT.Verify);
      revisionField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
      revisionField.setText("");
      revisionField.setEnabled(false);
   }

   public Text getRevisionField() {
      return revisionField;
   }

   public SearchCCombo getBranchCombo() {
      return branchCombo;
   }

   public void dispose() {
      branchCombo.dispose();
      revisionField.dispose();
   }

   public void refresh() {
      refreshCombos();
   }

   public int getValidMin(String branch) {
      Map<String, Integer> minMaxArray = branches.get(branch);
      if (minMaxArray != null) {
         return minMaxArray.get("minTX");
      }
      return -1;
   }

   public int getValidMax(String branch) {
      Map<String, Integer> minMaxArray = branches.get(branch);
      if (minMaxArray != null) {
         return minMaxArray.get("maxTX");
      }
      return -1;
   }

   public int getBranchId(String branch) {
      Map<String, Integer> minMaxArray = branches.get(branch);
      if (minMaxArray != null) {
         return minMaxArray.get("branchId");
      }
      return -1;
   }

   public void setTextFieldToolTip(String branch) {
      if (branches.containsKey(branch)) {
         revisionField.setToolTipText("Valid range is: [" + getValidMin(branch) + "..." + getValidMax(branch) + "]");
      } else {
         revisionField.setToolTipText("Select a Branch before entering revision number.");
      }
   }

   public String getCurrentBranchSelection() {
      return branchCombo.getItem(branchCombo.getSelectionIndex());
   }

   public String getDefaultBranchValue() {
      return DEFAULT_BRANCH_STRING;
   }

   private void populateCombos() {
      if (branches.size() > 0) {
         Set<String> branchNames = branches.keySet();
         branchCombo.clearSelection();
         for (String branch : branchNames) {
            branchCombo.add(branch);
         }
         branchCombo.select(0);
         setTextFieldToolTip(branchCombo.getItem(0));
         revisionField.setEnabled(true);
         revisionField.setEditable(true);
      } else {
         branchCombo.clearSelection();
         revisionField.setEnabled(false);
         branchCombo.setText(DEFAULT_BRANCH_STRING);
         revisionField.setText("");
      }
   }

   synchronized private void refreshCombos() {
      Display.getCurrent().asyncExec(new Runnable() {
         public void run() {
            branches = SkynetArtifactAdapter.getInstance().getBranchRevisions();
            populateCombos();
         }
      });
   }
}
