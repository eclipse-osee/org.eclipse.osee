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

package org.eclipse.osee.framework.ui.skynet.branch;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * @author Roberto E. Escobar
 */
public class BranchSelectComposite extends Composite implements Listener {
   protected static final int SIZING_TEXT_FIELD_WIDTH = 250;

   private Button branchSelectButton;
   private Text branchSelectTextWidget;
   private Branch selectedBranch;
   private final Set<Listener> listeners;
   private final boolean allowOnlyWorkingBranches;

   public BranchSelectComposite(Composite parent, int style, boolean allowOnlyWorkingBranches) {
      super(parent, style);
      this.allowOnlyWorkingBranches = allowOnlyWorkingBranches;
      this.listeners = Collections.synchronizedSet(new HashSet<Listener>());
      createControl(this);
   }

   public static BranchSelectComposite createWorkingBranchSelectComposite(Composite parent, int style) {
      return new BranchSelectComposite(parent, style, true);
   }

   public static BranchSelectComposite createBranchSelectComposite(Composite parent, int style) {
      return new BranchSelectComposite(parent, style, false);
   }

   private void createControl(Composite parent) {
      parent.setLayout(new GridLayout(2, false));
      parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      branchSelectTextWidget = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
      GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
      data.widthHint = SIZING_TEXT_FIELD_WIDTH;
      branchSelectTextWidget.setLayoutData(data);

      branchSelectButton = new Button(parent, SWT.PUSH);
      branchSelectButton.setText("Select Branch...");
      branchSelectButton.addListener(SWT.Selection, this);
      branchSelectButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
   }

   public Branch getSelectedBranch() {
      return selectedBranch;
   }

   private boolean areOnlyWorkingBranchesAllowed() {
      return allowOnlyWorkingBranches;
   }

   /* (non-Javadoc)
    * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
    */
   public void handleEvent(Event event) {
      if (event.widget == branchSelectButton) {
         if (areOnlyWorkingBranchesAllowed() != false) {
            setSelected(BranchSelectionDialog.getWorkingBranchFromUser());
         } else {
            setSelected(BranchSelectionDialog.getBranchFromUser());
         }
      }
      notifyListener(event);
   }

   public void setSelected(Branch branch) {
      if (branch != null) {
         selectedBranch = branch;
         branchSelectTextWidget.setText(selectedBranch.getBranchName());
      }
   }

   private void notifyListener(Event event) {
      synchronized (listeners) {
         for (Listener listener : listeners) {
            listener.handleEvent(event);
         }
      }
   }

   public void addListener(Listener listener) {
      synchronized (listeners) {
         listeners.add(listener);
      }
   }

   public void removeListener(Listener listener) {
      synchronized (listeners) {
         listeners.remove(listener);
      }
   }

   /**
    * @param defaultSelectedBranch the defaultSelectedBranch to set
    */
   public void setDefaultSelectedBranch(Branch defaultSelectedBranch) {
      setSelected(defaultSelectedBranch);
   }

   /**
    * @return the branchSelectLabel
    */
   public Text getBranchSelectText() {
      return branchSelectTextWidget;
   }
}
