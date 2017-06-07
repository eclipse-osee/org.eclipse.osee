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

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * @author Roberto E. Escobar
 */
public class BranchSelectComposite extends Composite {
   protected static final int SIZING_TEXT_FIELD_WIDTH = 250;

   private Button branchSelectButton;
   private Text branchSelectTextWidget;
   private BranchId selectedBranch;
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

      createButton(parent);

      branchSelectTextWidget = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
      GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
      data.widthHint = SIZING_TEXT_FIELD_WIDTH;
      branchSelectTextWidget.setLayoutData(data);
      branchSelectTextWidget.setBackground(Displays.getSystemColor(SWT.COLOR_WHITE));
      branchSelectTextWidget.setText(" -- Select A Branch -- ");
      branchSelectTextWidget.setDoubleClickEnabled(false);
      branchSelectTextWidget.addListener(SWT.MouseDoubleClick, new Listener() {
         @Override
         public void handleEvent(Event event) {
            handleSelectedBranch(event);
            notifyListener(event);
         }
      });

   }

   private void createButton(Composite parent) {
      branchSelectButton = new Button(parent, SWT.PUSH);
      branchSelectButton.setText("Select Branch...");
      branchSelectButton.addListener(SWT.Selection, new Listener() {

         @Override
         public void handleEvent(Event event) {
            handleSelectedBranch(event);
            notifyListener(event);
         }
      });
      branchSelectButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
      branchSelectButton.addListener(SWT.MouseUp, new Listener() {
         @Override
         public void handleEvent(Event event) {
            if (event.button == 3) {
               try {
                  setSelected(COMMON);
                  notifyListener(event);
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      });
   }

   public BranchId getSelectedBranch() {
      return selectedBranch;
   }

   private boolean areOnlyWorkingBranchesAllowed() {
      return allowOnlyWorkingBranches;
   }

   private void handleSelectedBranch(Event event) {
      if (event.widget == branchSelectButton || event.widget == branchSelectTextWidget && branchSelectTextWidget.getDoubleClickEnabled()) {
         if (areOnlyWorkingBranchesAllowed()) {
            BranchId newBranch = BranchSelectionDialog.getWorkingBranchFromUser();
            if (newBranch != null) {
               setSelected(newBranch);
            }
         } else {
            BranchId newBranch = BranchSelectionDialog.getBranchFromUser();
            if (newBranch != null) {
               setSelected(newBranch);
            }
         }
      }
   }

   public void setSelected(BranchId branch) {
      selectedBranch = branch;
      if (branch == null) {
         branchSelectTextWidget.setText(" -- Select A Branch -- ");
      } else {
         branchSelectTextWidget.setText(BranchManager.getBranchName(selectedBranch));
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
   public void setDefaultSelectedBranch(BranchId defaultSelectedBranch) {
      setSelected(defaultSelectedBranch);
   }

   /**
    * @return the branchSelectLabel
    */
   public Text getBranchSelectText() {
      return branchSelectTextWidget;
   }

   /**
    * @return the branchSelectLabel
    */
   public Button getBranchSelectButton() {
      return branchSelectButton;
   }
}
