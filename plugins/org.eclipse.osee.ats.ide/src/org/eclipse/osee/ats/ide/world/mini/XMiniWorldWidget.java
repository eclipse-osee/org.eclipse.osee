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
package org.eclipse.osee.ats.ide.world.mini;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.world.WorldContentProvider;
import org.eclipse.osee.ats.ide.world.WorldLabelProvider;
import org.eclipse.osee.ats.ide.world.WorldXViewer;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.GenericXWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XLabelValue;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;

/**
 * @author Donald G. Dunne
 */
public abstract class XMiniWorldWidget extends GenericXWidget {

   private WorldXViewer xWorldViewer;
   public final static String normalColor = "#EEEEEE";
   private static final int paddedTableHeightHint = 2;
   private Label extraInfoLabel;
   private int lastSize = 0;
   private final int MAX_TABLE_SIZE = 10;
   private Composite mainComp;
   private Composite parentComp;
   protected final AtsApi atsApi;
   private Collection<IAtsWorkItem> workItems;
   private XLabelValue pointsLabel;
   private final IXViewerFactory xViewerFactory;

   public XMiniWorldWidget(String label, IXViewerFactory xViewerFactory) {
      super(label);
      this.xViewerFactory = xViewerFactory;
      atsApi = AtsApiService.get();
   }

   abstract public Collection<IAtsWorkItem> getWorkItems();

   public Collection<IAtsWorkItem> getItems() {
      return workItems;
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      // parentComp needs to be created and remain intact; mainComp will be disposed and re-created as necessary
      parentComp = new Composite(parent, SWT.FLAT);
      parentComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      parentComp.setLayout(ALayout.getZeroMarginLayout());

      redrawComposite();
   }

   private void redrawComposite() {
      if (parentComp == null || !Widgets.isAccessible(parentComp)) {
         return;
      }
      if (mainComp != null && Widgets.isAccessible(mainComp)) {
         mainComp.dispose();
         xWorldViewer = null;
      }
      mainComp = new Composite(parentComp, SWT.FLAT);
      mainComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      mainComp.setLayout(new GridLayout(1, true));
      if (toolkit != null) {
         toolkit.paintBordersFor(mainComp);
      }

      labelWidget = new Label(mainComp, SWT.NONE);
      labelWidget.setText(getLabel() + ":");
      if (getToolTip() != null) {
         labelWidget.setToolTipText(getToolTip());
      }

      try {
         Composite tableComp = new Composite(mainComp, SWT.BORDER);
         GridData gd = new GridData(GridData.FILL_HORIZONTAL);
         tableComp.setLayoutData(gd);
         tableComp.setLayout(ALayout.getZeroMarginLayout());
         if (toolkit != null) {
            toolkit.paintBordersFor(tableComp);
         }

         ToolBar toolBar = createActionBar(tableComp);

         xWorldViewer = new WorldXViewer(tableComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, xViewerFactory, null);
         xWorldViewer.getTree().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

         xWorldViewer.setContentProvider(new WorldContentProvider(xWorldViewer));
         xWorldViewer.setLabelProvider(new WorldLabelProvider(xWorldViewer));

         new ActionContributionItem(xWorldViewer.getCustomizeAction()).fill(toolBar, -1);

         if (toolkit != null && xWorldViewer.getStatusLabel() != null) {
            toolkit.adapt(xWorldViewer.getStatusLabel(), false, false);
         }

         pointsLabel = new XLabelValue("Total Estimated Points", "0");
         pointsLabel.createWidgets(tableComp, 2);
         pointsLabel.getComp().setLayout(new GridLayout(2, false));
         pointsLabel.getLabelWidget().setFont(FontManager.getCourierNew12Bold());
         if (toolkit != null) {
            toolkit.adapt(pointsLabel.getLabelWidget(), false, false);
            toolkit.adapt(pointsLabel.getValueTextWidget(), false, false);
         }

         refresh();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      // reset bold for label
      WorkflowEditor.setLabelFonts(labelWidget, FontManager.getDefaultLabelFont());

      parentComp.layout();
   }

   public abstract ToolBar createActionBar(Composite tableComp);

   public void setXviewerTreeSize() {
      Tree tree = xWorldViewer.getTree();
      int size = xWorldViewer.getTree().getItemCount();
      if (size > MAX_TABLE_SIZE) {
         size = MAX_TABLE_SIZE;
      }
      if (size == lastSize) {
         return;
      }
      lastSize = size;
      int treeItemHeight = xWorldViewer.getTree().getItemHeight();
      GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
      gridData.heightHint = treeItemHeight * (paddedTableHeightHint + size);
      tree.setLayout(ALayout.getZeroMarginLayout());
      tree.setLayoutData(gridData);
      tree.setHeaderVisible(true);
      tree.setLinesVisible(true);
   }

   public void loadTable() {
      try {
         if (xWorldViewer != null && xWorldViewer.getContentProvider() != null) {
            workItems = getWorkItems();
            xWorldViewer.setInput(workItems);
            xWorldViewer.refresh();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public Control getControl() {
      if (xWorldViewer == null) {
         return null;
      }
      return xWorldViewer.getTree();
   }

   @Override
   public void dispose() {
      if (xWorldViewer != null) {
         xWorldViewer.dispose();
      }
   }

   @Override
   public void refresh() {
      if (xWorldViewer == null || xWorldViewer.getTree() == null || xWorldViewer.getTree().isDisposed()) {
         return;
      }
      loadTable();
      setXviewerTreeSize();
      updateExtraInfoLabel();
      pointsLabel.setValueText("Put Label Here");
   }

   public WorldXViewer getxWorldViewer() {
      return xWorldViewer;
   }

   public Label getExtraInfoLabel() {
      return extraInfoLabel;
   }

   public void setExtraInfoLabel(Label extraInfoLabel) {
      this.extraInfoLabel = extraInfoLabel;
   }

   public abstract Pair<Integer, String> getExtraInfoString();

   private void updateExtraInfoLabel() {
      Pair<Integer, String> entry = getExtraInfoString();
      updateExtraInfoLabel(entry.getFirst(), entry.getSecond());
   }

   private void updateExtraInfoLabel(final int color, final String infoStr) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (Widgets.isAccessible(extraInfoLabel)) {
               String currentString = extraInfoLabel.getText();
               if (infoStr == null && currentString != null || //
               infoStr != null && currentString == null || //
               infoStr != null && currentString != null && !infoStr.equals(currentString)) {
                  extraInfoLabel.setText(infoStr);
               }
               extraInfoLabel.setForeground(Displays.getSystemColor(color));
            }
         }
      });
   }

}
