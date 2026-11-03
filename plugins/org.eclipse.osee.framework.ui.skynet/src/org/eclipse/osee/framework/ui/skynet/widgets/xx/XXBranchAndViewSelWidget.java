/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.xx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.BranchViewToken;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.osgi.service.component.annotations.Component;

/**
 * Composite widget that wraps XXBranchSelWidget and XXBranchViewWidget, acting as a single widget. Delegates XWidget
 * lifecycle methods (validate, refresh, dispose, setEditable, setArtifact, etc.) to its children.
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXBranchAndViewSelWidget extends XXAbstractBaseXWidget<String> {

   public static final WidgetId ID = WidgetId.XXBranchAndViewSelWidget;

   private XXBranchSelWidget branchWidget;
   private XXBranchViewWidget viewWidget;
   private BranchToken selectedBranch = BranchToken.SENTINEL;
   private BranchViewToken selectedBranchView = BranchViewToken.SENTINEL;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXBranchAndViewSelWidget() {
      super(ID, "Branch / View Selection");
      set(XOption.NO_LABEL);
      set(XOption.VAL_NOT_APPLICABLE);
   }

   //////////////////////////////////////////
   // UI Construction
   //////////////////////////////////////////

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      comp = new Composite(parent, SWT.BORDER);
      GridLayout layout = new GridLayout(1, false);
      layout.marginHeight = 1;
      layout.marginWidth = 0;
      comp.setLayout(layout);
      GridData compGd = new GridData(SWT.FILL, SWT.CENTER, true, false);
      compGd.horizontalSpan = horizontalSpan;
      comp.setLayoutData(compGd);
      if (getToolkit() != null) {
         getToolkit().adapt(comp);
      }

      createLabelWidget();

      branchWidget = new XXBranchSelWidget();
      branchWidget.setToolkit(getToolkit());
      branchWidget.getWidData().getBranchQuery().getBranchTypes().add(BranchType.BASELINE);
      branchWidget.setValueSingleSelect(true);
      branchWidget.createWidgets(getManagedForm(), comp, 2);
      branchWidget.getComp().setLayout(ALayout.getZeroMarginLayout(2, false));
      branchWidget.addXModifiedListener(new XModifiedListener() {
         @Override
         public void widgetModified(XWidget widget) {
            handleBranchSelection();
            notifyXModifiedListeners();
         }
      });

      updateBranchViewComp();

      refresh();
   }

   @Override
   public Collection<? extends XWidget> getChildrenXWidgets() {
      List<XWidget> children = new ArrayList<>();
      if (branchWidget != null) {
         children.add(branchWidget);
      }
      if (viewWidget != null) {
         children.add(viewWidget);
      }
      return children;
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public String getSentinel() {
      return "";
   }

   @Override
   protected boolean handleSelection() {
      return false;
   }

   @Override
   public String getDisplayValue() {
      StringBuilder sb = new StringBuilder();
      if (selectedBranch.isValid()) {
         sb.append(selectedBranch.getShortName());
      }
      if (selectedBranchView.isValid()) {
         sb.append(" / ").append(selectedBranchView.getName());
      }
      return sb.length() > 0 ? sb.toString() : "";
   }

   public void setSelected(BranchToken branch) {
      this.selectedBranch = branch;
      if (branchWidget != null) {
         branchWidget.setSelected(branch);
      }
      updateBranchViewComp();
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   public XResultData isValid() {
      XResultData rd = super.isValid();
      if (rd.isErrors()) {
         return rd;
      }
      if (branchWidget != null) {
         XResultData branchRd = branchWidget.isValid();
         if (branchRd.isErrors()) {
            return branchRd;
         }
      }
      if (viewWidget != null) {
         XResultData viewRd = viewWidget.isValid();
         if (viewRd.isErrors()) {
            return viewRd;
         }
      }
      return rd;
   }

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

   //////////////////////////////////////////
   // Refresh / Lifecycle
   //////////////////////////////////////////

   @Override
   public void refresh() {
      if (!Widgets.isAccessible(comp)) {
         return;
      }
      if (branchWidget != null) {
         branchWidget.refresh();
      }
      if (viewWidget != null) {
         viewWidget.refresh();
      }
      super.validate();
   }

   @Override
   public void setEditable(boolean editable) {
      super.setEditable(editable);
      if (branchWidget != null) {
         branchWidget.setEditable(editable);
      }
      if (viewWidget != null) {
         viewWidget.setEditable(editable);
      }
   }

   @Override
   public Control getControl() {
      return comp;
   }

   @Override
   public void dispose() {
      if (branchWidget != null) {
         branchWidget.dispose();
      }
      if (viewWidget != null) {
         viewWidget.dispose();
      }
      super.dispose();
   }

   @Override
   public void setNotificationsAllowed(boolean areAllowed) {
      super.setNotificationsAllowed(areAllowed);
      if (branchWidget != null) {
         branchWidget.setNotificationsAllowed(areAllowed);
      }
      if (viewWidget != null) {
         viewWidget.setNotificationsAllowed(areAllowed);
      }
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   private void updateBranchViewComp() {
      if (!Widgets.isAccessible(comp)) {
         return;
      }
      if (isPleBranch()) {
         if (viewWidget == null) {
            viewWidget = new XXBranchViewWidget();
            viewWidget.setToolkit(getToolkit());
            viewWidget.setValueSingleSelect(true);
            viewWidget.createWidgets(getManagedForm(), comp, 2);
            viewWidget.getComp().setLayout(ALayout.getZeroMarginLayout(2, false));
            viewWidget.addXModifiedListener(new XModifiedListener() {
               @Override
               public void widgetModified(XWidget widget) {
                  handleBranchViewSelection();
                  notifyXModifiedListeners();
               }
            });
         }
         if (viewWidget != null) {
            List<BranchViewToken> branchViewTokens =
               ServiceUtil.getOseeClient().getApplicabilityEndpoint(selectedBranch).getBranchViewTokens();
            viewWidget.setSelectable(branchViewTokens);
         }
         selectedBranchView = BranchViewToken.SENTINEL;
      } else {
         if (viewWidget != null) {
            viewWidget.dispose();
            viewWidget = null;
         }
      }
      comp.layout(true);
      comp.getParent().layout(true);
   }

   private void handleBranchSelection() {
      selectedBranch = branchWidget.getSelectedFirst();
      updateBranchViewComp();
      refresh();
   }

   private void handleBranchViewSelection() {
      selectedBranchView = viewWidget.getSelectedFirst();
      refresh();
   }

   public BranchToken getBranch() {
      if (branchWidget != null) {
         return branchWidget.getSelectedFirst();
      }
      return selectedBranch;
   }

   public boolean isPleBranch() {
      if (getBranch().isInvalid()) {
         return false;
      }
      return BranchManager.isPle(selectedBranch);
   }

   public BranchToken getSelectedBranch() {
      return selectedBranch;
   }

   public BranchViewToken getSelectedBranchView() {
      return selectedBranchView;
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   public void setArtifact(Artifact artifact) {
      super.setArtifact(artifact);
      if (branchWidget != null) {
         branchWidget.setArtifact(artifact);
      }
      if (viewWidget != null) {
         viewWidget.setArtifact(artifact);
      }
   }

}
