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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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
      parent.setLayout(ALayout.getZeroMarginLayout(2, false));
      parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      branchSelectTextWidget = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
      GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
      data.widthHint = SIZING_TEXT_FIELD_WIDTH;
      branchSelectTextWidget.setLayoutData(data);
      branchSelectTextWidget.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
      branchSelectTextWidget.setText(" -- Select A Branch -- ");

      branchSelectButton = new Button(parent, SWT.PUSH);
      branchSelectButton.setText("Select Branch...");
      branchSelectButton.addListener(SWT.Selection, this);
      branchSelectButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
      branchSelectButton.addListener(SWT.MouseUp, new Listener() {
         @Override
         public void handleEvent(Event event) {
            if (event.button == 3) {
               try {
                  setSelected(BranchManager.getCommonBranch());
                  notifyListener(event);
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      });
   }

   public Branch getSelectedBranch() {
      return selectedBranch;
   }

   private boolean areOnlyWorkingBranchesAllowed() {
      return allowOnlyWorkingBranches;
   }

   public void handleEvent(Event event) {
      if (event.widget == branchSelectButton) {
         if (areOnlyWorkingBranchesAllowed()) {
            Branch newBranch = BranchSelectionDialog.getWorkingBranchFromUser();
            if (newBranch != null) {
               setSelected(newBranch);
            }
         } else {
            Branch newBranch = BranchSelectionDialog.getBranchFromUser();
            if (newBranch != null) {
               setSelected(newBranch);
            }
         }
      }
      notifyListener(event);
   }

   public void setSelected(Branch branch) {
      if (branch != null) {
         selectedBranch = branch;
         branchSelectTextWidget.setText(selectedBranch.getName());
      } else {
         branchSelectTextWidget.setText(" -- Select A Branch -- ");
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
