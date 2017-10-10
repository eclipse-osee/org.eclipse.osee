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
package org.eclipse.osee.framework.ui.branch.graph.core;

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osee.framework.ui.branch.graph.Activator;
import org.eclipse.osee.framework.ui.branch.graph.parts.GraphEditPart;
import org.eclipse.osee.framework.ui.branch.graph.utility.GraphOptions;
import org.eclipse.osee.framework.ui.branch.graph.utility.GraphOptions.ConnectionFilter;
import org.eclipse.osee.framework.ui.branch.graph.utility.GraphOptions.TxFilter;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorPart;

/**
 * @author Roberto E. Escobar
 */
public class BranchGraphActionBarContributor extends ActionBarContributor {
   private BranchGraphEditor editor;

   private static ToggleFilterConnectionAction[] toggleFilterConnectionAction;
   private static ToggleTxFilterAction[] toggleTxFilterAction;

   @Override
   public void setActiveEditor(IEditorPart editor) {
      super.setActiveEditor(editor);
      this.editor = (BranchGraphEditor) editor;
   }

   @Override
   protected void buildActions() {
      // do nothing
   }

   @Override
   protected void declareGlobalActionKeys() {
      // do nothing
   }

   @Override
   public void contributeToToolBar(IToolBarManager toolBarManager) {
      super.contributeToToolBar(toolBarManager);
      toolBarManager.add(new Separator());
      toolBarManager.add(new ZoomComboContributionItem(getPage()));
      toolBarManager.add(new Separator());

      toggleFilterConnectionAction = new ToggleFilterConnectionAction[] {
         new ToggleFilterConnectionAction("None", ConnectionFilter.NO_FILTER),
         new ToggleFilterConnectionAction("Filter All Connections", ConnectionFilter.FILTER_ALL_CONNECTIONS),
         new ToggleFilterConnectionAction("Filter Branch Hierarchy Connections",
            ConnectionFilter.FILTER_CHILD_BRANCH_CONNECTIONS),
         new ToggleFilterConnectionAction("Filter Merge Connections", ConnectionFilter.FILTER_MERGE_CONNECTIONS)};

      toolBarManager.add(new FilterConnectionAction());

      toggleTxFilterAction = new ToggleTxFilterAction[] {
         new ToggleTxFilterAction("Show All", TxFilter.NO_FILTER),
         new ToggleTxFilterAction("Hide All", TxFilter.HIDE_ALL)};
      toolBarManager.add(new FilterTxAction());
   }

   private final class ToggleFilterConnectionAction extends Action {

      private final ConnectionFilter show;

      public ToggleFilterConnectionAction(String text, ConnectionFilter show) {
         super(text, AS_RADIO_BUTTON);
         this.show = show;
         setChecked(show.ordinal() == Activator.getInstance().getPreferenceStore().getInt(
            GraphOptions.FILTER_CONNECTIONS_PREFERENCE));
      }

      @Override
      public void run() {
         if (isChecked()) {
            Activator.getInstance().getPreferenceStore().setValue(GraphOptions.FILTER_CONNECTIONS_PREFERENCE,
               show.ordinal());
            GraphEditPart graphEditPart = (GraphEditPart) editor.getViewer().getContents();
            graphEditPart.setConnectionVisibility();
         }
      }
   }

   private final class ToggleTxFilterAction extends Action {

      private final TxFilter show;

      public ToggleTxFilterAction(String text, TxFilter show) {
         super(text, AS_RADIO_BUTTON);
         this.show = show;
         setChecked(
            show.ordinal() == Activator.getInstance().getPreferenceStore().getInt(GraphOptions.TRANSACTION_FILTER));
      }

      @Override
      public void run() {
         if (isChecked()) {
            Activator.getInstance().getPreferenceStore().setValue(GraphOptions.TRANSACTION_FILTER, show.ordinal());
            GraphEditPart graphEditPart = (GraphEditPart) editor.getViewer().getContents();
            graphEditPart.setTxVisibility();
         }
      }
   }

   private static final class FilterTxAction extends Action implements IMenuCreator {
      private Menu menu;

      public FilterTxAction() {
         setText("Filter transactions");
         setToolTipText("Filter transactions");
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.DB_ICON_BLUE));
         setMenuCreator(this);
      }

      @Override
      public void dispose() {
         if (menu != null) {
            menu.dispose();
            menu = null;
         }
      }

      @Override
      public Menu getMenu(Control parent) {
         if (menu != null) {
            menu.dispose();
         }
         menu = new Menu(parent);
         for (int index = 0; index < toggleTxFilterAction.length; index++) {
            addActionToMenu(menu, toggleTxFilterAction[index]);
         }
         return menu;
      }

      @Override
      public Menu getMenu(Menu parent) {
         return null;
      }

      private void addActionToMenu(Menu parent, Action action) {
         ActionContributionItem item = new ActionContributionItem(action);
         item.fill(parent, -1);
      }

   }

   private static final class FilterConnectionAction extends Action implements IMenuCreator {
      private Menu menu;

      public FilterConnectionAction() {
         setText("Filter connections");
         setToolTipText("Filter connections");
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.FILTERS));
         setMenuCreator(this);
      }

      @Override
      public void dispose() {
         if (menu != null) {
            menu.dispose();
            menu = null;
         }
      }

      @Override
      public Menu getMenu(Control parent) {
         if (menu != null) {
            menu.dispose();
         }
         menu = new Menu(parent);
         for (int index = 0; index < toggleFilterConnectionAction.length; index++) {
            addActionToMenu(menu, toggleFilterConnectionAction[index]);
         }
         return menu;
      }

      @Override
      public Menu getMenu(Menu parent) {
         return null;
      }

      private void addActionToMenu(Menu parent, Action action) {
         ActionContributionItem item = new ActionContributionItem(action);
         item.fill(parent, -1);
      }

   }
}
