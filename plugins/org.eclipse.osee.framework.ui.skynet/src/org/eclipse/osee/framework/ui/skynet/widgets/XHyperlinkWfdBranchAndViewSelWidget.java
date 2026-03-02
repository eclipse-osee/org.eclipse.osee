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
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.List;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.BranchViewToken;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XHyperlinkWfdBranchAndViewSelWidget extends XAbstractCompositeWidget {

   public static final WidgetId ID = WidgetId.XHyperlinkWfdBranchAndViewSelWidget;

   private XHyperlinkWfdBranchSelWidget branchWidget;
   private XHyperlinkWfdBranchViewSelWidget viewWidget;
   private BranchToken selectedBranch = BranchToken.SENTINEL;
   private BranchViewToken selectedBranchView = BranchViewToken.SENTINEL;

   public XHyperlinkWfdBranchAndViewSelWidget() {
      super(ID, "Branch / View Selection");
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      setupWidgets();
      super.createControls(parent, horizontalSpan);
   }

   private void setupWidgets() {
      setNumColumns(5);
      setFillHorizontally(true);

      branchWidget = new XHyperlinkWfdBranchSelWidget();
      addXWidget(branchWidget);
      branchWidget.getWidData().getBranchQuery().getBranchTypes().add(BranchType.BASELINE);
      branchWidget.setSingleSelect(true);
      branchWidget.addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            handleBranchSelection();
         }

      });

      viewWidget = new XHyperlinkWfdBranchViewSelWidget();
      addXWidget(viewWidget);
      viewWidget.setSingleSelect(true);
      viewWidget.addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            handleBranchViewSelection();
         }
      });
   }

   private void handleBranchSelection() {
      selectedBranch = branchWidget.getSelected();
      List<BranchViewToken> branchViewTokens =
         ServiceUtil.getOseeClient().getApplicabilityEndpoint(selectedBranch).getBranchViewTokens();
      viewWidget.setSelectable(branchViewTokens);
      selectedBranchView = BranchViewToken.SENTINEL;
      refresh();
   }

   protected void handleBranchViewSelection() {
      selectedBranchView = viewWidget.getSelected();
      refresh();
   }

   public BranchToken getSelectedBranch() {
      return selectedBranch;
   }

   public BranchViewToken getSelectedBranchView() {
      return selectedBranchView;
   }

}
