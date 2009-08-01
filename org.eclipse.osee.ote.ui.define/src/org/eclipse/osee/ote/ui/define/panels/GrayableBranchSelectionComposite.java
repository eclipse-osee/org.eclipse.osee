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
package org.eclipse.osee.ote.ui.define.panels;

import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.event.AccessControlEventType;
import org.eclipse.osee.framework.skynet.core.event.IAccessControlEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.panels.BranchSelectSimpleComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class GrayableBranchSelectionComposite extends Composite implements IAccessControlEventListener {

   private static final String GROUP_TEXT = "Upload results into OSEE";
   private static final String CHECK_BUTTON_TEXT = "Enable upload";
   private static final String CHECK_BUTTON_TOOL_TIP =
         "Select to store outfiles as Test Run Artifacts.\n" + "NOTE: User must be authenticated to create Artifacts.";
   private static final String AUTHENTICATION_WARNING_MESSAGE =
         "Authentication failure - Outfile upload not allowed. Double-Click on the lock icon at the bottom of the screen to authenticate.";
   private static final String FEATURE_DISABLED = "Feature disabled.";

   private BranchSelectSimpleComposite branchSelectComposite;
   private Button branchSelectEnabled;
   private Composite statusComposite;
   private Composite selectableComposite;
   private Label statusLabel;
   private StackLayout stackedLayout;
   private Group group;
   private boolean featureEnabled;

   public GrayableBranchSelectionComposite(Composite parent, int style) {
      super(parent, style);
      createControl(this);
   }

   @Override
   public void handleAccessControlArtifactsEvent(Sender sender, AccessControlEventType accessControlModType, LoadedArtifacts loadedArtifactss) {
      if (accessControlModType == AccessControlEventType.UserAuthenticated) {
         handleUserAuthenticated();
      }
   }

   private void createControl(Composite parent) {
      GridLayout gL = new GridLayout();
      gL.marginWidth = 0;
      gL.marginHeight = 0;
      parent.setLayout(gL);
      parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      group = new Group(parent, SWT.NONE);
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      stackedLayout = new StackLayout();
      group.setLayout(stackedLayout);
      group.setText(GROUP_TEXT);

      createStatusBar(group);
      createSelectableArea(group);
      handleBranchSelectEnabled(false);
      OseeEventManager.addListener(this);
   }

   private void createSelectableArea(Composite parent) {
      selectableComposite = new Composite(parent, SWT.NONE);
      selectableComposite.setLayout(new GridLayout());
      selectableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      branchSelectEnabled = new Button(selectableComposite, SWT.CHECK);
      branchSelectEnabled.setText(CHECK_BUTTON_TEXT);
      branchSelectEnabled.setToolTipText(CHECK_BUTTON_TOOL_TIP);

      branchSelectEnabled.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleBranchSelectEnabled(branchSelectEnabled.getSelection());
         }
      });
      this.branchSelectComposite =
            BranchSelectSimpleComposite.createWorkingBranchSelectComposite(selectableComposite, SWT.NONE);
   }

   private void createStatusBar(Composite parent) {
      statusComposite = new Composite(parent, SWT.BORDER);
      statusComposite.setLayout(new GridLayout(2, false));
      statusComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
      statusComposite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
      statusComposite.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_FOREGROUND));

      Label imageLabel = new Label(statusComposite, SWT.NONE);
      imageLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
      imageLabel.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_WARN_TSK));
      imageLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
      imageLabel.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_FOREGROUND));

      statusLabel = new Label(statusComposite, SWT.NONE);
      statusLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      statusLabel.setText("");
      statusLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
      statusLabel.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
   }

   private void handleBranchSelectEnabled(boolean isSelected) {
      setSelectable(true);
      if (ClientSessionManager.isSessionValid() != true) {
         statusLabel.setText(AUTHENTICATION_WARNING_MESSAGE);
         setSelectable(false);
         isSelected = false;
      }
      branchSelectEnabled.setSelection(isSelected);
      branchSelectComposite.setEnabled(isSelected);
      for (Control control : branchSelectComposite.getChildren()) {
         control.setEnabled(isSelected);
      }
   }

   public Branch getSelectedBranch() {
      return branchSelectComposite.getSelectedBranch();
   }

   public boolean isBranchSelectEnabled() {
      return branchSelectEnabled.getSelection();
   }

   public void setFeatureEnabled(final boolean isEnabled) {
      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            featureEnabled = isEnabled;
            handleBranchSelectEnabled(false);
            statusLabel.setText(featureEnabled != true ? FEATURE_DISABLED : "");
            setEnabled(featureEnabled);
            setSelectable(featureEnabled);
         }
      });
   }

   private boolean isFeatureEnabled() {
      return featureEnabled;
   }

   @Override
   public void setEnabled(boolean enabled) {
      super.setEnabled(enabled);
      for (Control child : group.getChildren()) {
         child.setEnabled(enabled);
      }
      branchSelectEnabled.setEnabled(enabled);
   }

   public String[] getBranchIds() {
      return branchSelectComposite.getBranchIds();
   }

   public void restoreWidgetValues(boolean saveAsArtifact, String[] branchIds, String lastSelected) {
      branchSelectEnabled.setSelection(saveAsArtifact && ClientSessionManager.isSessionValid());
      branchSelectComposite.restoreWidgetValues(branchIds, lastSelected);
   }

   private void setSelectable(final boolean isSelectable) {
      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            if (isSelectable != false) {
               stackedLayout.topControl = selectableComposite;
            } else {
               stackedLayout.topControl = statusComposite;
            }
            group.layout();
         }
      });
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      super.dispose();
   }

   private void handleUserAuthenticated() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (branchSelectEnabled != null && branchSelectEnabled.isDisposed() != true && isFeatureEnabled() != false) {
               handleBranchSelectEnabled(branchSelectEnabled.getSelection());
            }
         }
      });
   }

   public boolean runOnEventInDisplayThread() {
      return true;
   }
}
