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

package org.eclipse.osee.ats.editor.history;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class XHistoryViewer extends XWidget {

   private HistoryXViewer xViewer;
   private ToolBar toolBar;
   protected final AbstractWorkflowArtifact awa;
   protected final Collection<Change> changes = new ArrayList<Change>();

   private static Map<AbstractWorkflowArtifact, Integer> tableHeight = new HashMap<AbstractWorkflowArtifact, Integer>();

   public XHistoryViewer(AbstractWorkflowArtifact awa) {
      super("");
      this.awa = awa;
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      // Create Text Widgets
      if (isDisplayLabel() && !getLabel().equals("")) {
         labelWidget = new Label(parent, SWT.NONE);
         labelWidget.setText(getLabel() + ":");
         if (getToolTip() != null) {
            labelWidget.setToolTipText(getToolTip());
         }
      }

      final Composite mainComp = new Composite(parent, SWT.BORDER);
      mainComp.setLayoutData(new GridData(GridData.FILL_BOTH));
      mainComp.setLayout(ALayout.getZeroMarginLayout());
      if (toolkit != null) {
         toolkit.paintBordersFor(mainComp);
      }

      createToolBar(mainComp);
      HistoryRefreshAction refreshAction = new HistoryRefreshAction("Refresh History", SWT.PUSH, this);

      ActionContributionItem item = new ActionContributionItem(refreshAction);
      item.fill(toolBar, 0);

      xViewer = new HistoryXViewer(mainComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, this);
      xViewer.setContentProvider(new ArrayTreeContentProvider());
      xViewer.setLabelProvider(new HistoryLabelProvider(xViewer));
      new ActionContributionItem(xViewer.getCustomizeAction()).fill(toolBar, -1);

      if (toolkit != null) {
         toolkit.adapt(xViewer.getStatusLabel(), false, false);
      }

      refreshTableSize();
      // NOTE: Don't adapt the tree using xToolkit cause will loose xViewer's context menu

      final Sash sash = new Sash(parent, SWT.HORIZONTAL);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.heightHint = 3;
      sash.setLayoutData(gd);
      sash.setBackground(Displays.getSystemColor(SWT.COLOR_GRAY));
      sash.addListener(SWT.MouseUp, new Listener() {
         @Override
         public void handleEvent(Event e) {
            Rectangle treeRect = xViewer.getTree().getClientArea();
            int newHeight = treeRect.height + e.y;
            setTableHeight(newHeight);
            refreshTableSize();
            mainComp.layout();
            xViewer.refresh();
            if (getForm(mainComp) != null) {
               getForm(mainComp).reflow(true);
            }
         }
      });

      refreshAction.run();
   }

   private void refreshTableSize() {
      Tree tree = xViewer.getTree();
      GridData gridData = new GridData(GridData.FILL_BOTH);
      gridData.heightHint = getTableHeight();
      tree.setLayout(ALayout.getZeroMarginLayout());
      tree.setLayoutData(gridData);
      tree.setHeaderVisible(true);
      tree.setLinesVisible(true);
   }

   public ScrolledForm getForm(Composite composite) {
      ScrolledForm form = null;
      if (composite == null) {
         return null;
      }
      if (composite instanceof ScrolledForm) {
         return (ScrolledForm) composite;
      }
      if (!(composite instanceof ScrolledForm)) {
         form = getForm(composite.getParent());
      }
      return form;
   }

   private int getTableHeight() {
      if (awa != null && tableHeight.containsKey(awa)) {
         return tableHeight.get(awa);
      }
      return 200;
   }

   private void setTableHeight(int newHeight) {
      if (awa != null) {
         if (newHeight < 200) {
            newHeight = 200;
         }
         tableHeight.put(awa, newHeight);
      }
   }

   public void createToolBar(Composite parent) {
      // Button composite for state transitions, etc
      Composite bComp = new Composite(parent, SWT.NONE);
      // bComp.setBackground(mainSComp.getDisplay().getSystemColor(SWT.COLOR_CYAN));
      bComp.setLayout(new GridLayout(2, false));
      bComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      Composite actionComp = new Composite(bComp, SWT.NONE);
      actionComp.setLayout(new GridLayout());
      actionComp.setLayoutData(new GridData(GridData.END));

      toolBar = new ToolBar(actionComp, SWT.FLAT | SWT.RIGHT);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      toolBar.setLayoutData(gd);
   }

   @Override
   public void setXmlData(String str) {
      // do nothing
   }

   @Override
   public String getXmlData() {
      return null;
   }

   @Override
   public String getReportData() {
      return null;
   }

   /**
    * @return Returns the xViewer.
    */
   public HistoryXViewer getXViewer() {
      return xViewer;
   }

   @Override
   public Object getData() {
      return xViewer.getInput();
   }

   @Override
   public Control getErrorMessageControl() {
      return labelWidget;
   }

   @Override
   public IStatus isValid() {
      return null;
   }

   @Override
   public String toHTML(String labelFont) {
      return null;
   }

   @Override
   public Control getControl() {
      return null;
   }

   @Override
   public void setFocus() {
      getXViewer().getTree().setFocus();
   }

   @Override
   public void refresh() {
      getXViewer().refresh();
   }

}
