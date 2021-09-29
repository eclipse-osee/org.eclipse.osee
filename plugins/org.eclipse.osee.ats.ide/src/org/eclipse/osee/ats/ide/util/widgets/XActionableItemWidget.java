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
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.ActionableItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.dialog.AITreeContentProvider;
import org.eclipse.osee.ats.ide.util.widgets.dialog.AtsObjectNameSorter;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.util.IsEnabled;
import org.eclipse.osee.framework.ui.skynet.widgets.GenericXWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidgetUtility;
import org.eclipse.osee.framework.ui.skynet.widgets.checkbox.CheckBoxStateFilteredTreeViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.checkbox.CheckBoxStateTreeLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.checkbox.ICheckBoxStateTreeListener;
import org.eclipse.osee.framework.ui.skynet.widgets.checkbox.ICheckBoxStateTreeViewer;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Donald G. Dunne
 */
public class XActionableItemWidget extends GenericXWidget implements IsEnabled {

   public static final String WIDGET_ID = XActionableItemWidget.class.getSimpleName();
   protected CheckBoxStateFilteredTreeViewer<IAtsActionableItem> treeViewer;
   public static String NAME = "Actionable Item(s)";
   protected final AtsApi atsApi;
   private Composite mainComp;
   private Text descriptionLabel;

   public XActionableItemWidget() {
      super(NAME);
      atsApi = AtsApiService.get();
   }

   @Override
   public Control getControl() {
      return null;
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      mainComp = new Composite(parent, SWT.FLAT);
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

         Pair<CheckBoxStateFilteredTreeViewer<IAtsActionableItem>, Text> results =
            createActionableItemTreeViewer(tableComp, getSelectableAis());
         treeViewer = results.getFirst();
         treeViewer.setEnabledChecker(this);
         treeViewer.addCheckListener(new CheckStateListener());

         if (getInitialAias() != null) {
            treeViewer.setChecked(getInitialAias());
         }

         refresh();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      XWidgetUtility.setLabelFontsBold(labelWidget, FontManager.getDefaultLabelFont());
   }

   private Collection<IAtsActionableItem> getSelectableAis() {
      return null;
   }

   private Collection<IAtsActionableItem> getInitialAias() {
      return Collections.emptyList();
   }

   private class CheckStateListener implements ICheckBoxStateTreeListener {
      @Override
      public void checkStateChanged(Object obj) {
         if (descriptionLabel != null) {
            Collection<IAtsActionableItem> checked = treeViewer.getChecked();
            if (checked.isEmpty()) {
               descriptionLabel.setText("");
            } else {
               IAtsActionableItem ai = checked.iterator().next();
               descriptionLabel.setText(ai.getDescription());
            }
         }
      }

   }

   public Set<IAtsActionableItem> getSelectedIAtsActionableItems() {
      Set<IAtsActionableItem> selected = new HashSet<>();
      for (Object obj : treeViewer.getChecked()) {
         if (obj instanceof IAtsActionableItem) {
            selected.add((IAtsActionableItem) obj);
         } else if (obj instanceof ActionableItem) {
            ActionableItem ai = (ActionableItem) obj;
            selected.add(atsApi.getQueryService().getConfigItem(ai.getId()));
         }
      }
      return selected;
   }

   public Pair<CheckBoxStateFilteredTreeViewer<IAtsActionableItem>, Text> createActionableItemTreeViewer(Composite comp, Collection<IAtsActionableItem> selectableAis) {
      Composite aiComp = new Composite(comp, SWT.NONE);
      aiComp.setLayout(new GridLayout(1, false));
      aiComp.setLayoutData(new GridData(GridData.FILL_BOTH));

      new Label(aiComp, SWT.NONE).setText("Select Actionable Items:");
      CheckBoxStateFilteredTreeViewer<IAtsActionableItem> treeViewer =
         new CheckBoxStateFilteredTreeViewer<>(aiComp, SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
      treeViewer.getViewer().getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      treeViewer.getViewer().setContentProvider(new AITreeContentProvider(Active.Active));
      treeViewer.getViewer().setLabelProvider(new AITreeLabelProvider(treeViewer));
      try {
         if (selectableAis == null) {
            List<IAtsActionableItem> activeActionableItemTree = new LinkedList<>();
            AtsConfigurations configs = AtsApiService.get().getConfigService().getConfigurations();
            for (Long aiId : configs.getIdToAi().get(configs.getTopActionableItem().getId()).getChildren()) {
               ActionableItem ai = configs.getIdToAi().get(aiId);
               if (ai.isActive()) {
                  activeActionableItemTree.add(ai);
               }
            }
            treeViewer.getViewer().setInput(activeActionableItemTree);
            if (activeActionableItemTree.size() == 1) {
               treeViewer.expandOneLevel();
            }
         } else {
            treeViewer.getViewer().setInput(selectableAis);
            if (selectableAis.size() == 1) {
               treeViewer.expandOneLevel();
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      treeViewer.getViewer().setComparator(new AtsObjectNameSorter());
      GridData gridData1 = new GridData(GridData.FILL_BOTH);
      gridData1.heightHint = 200;
      treeViewer.setLayoutData(gridData1);

      new Label(aiComp, SWT.NONE).setText("Description of highlighted Actionable Item (if any):");
      descriptionLabel = new Text(aiComp, SWT.BORDER | SWT.WRAP);
      gridData1 = new GridData(GridData.FILL_BOTH);
      gridData1.heightHint = 15;
      descriptionLabel.setLayoutData(gridData1);
      descriptionLabel.setEnabled(false);

      Button deselectAll = new Button(aiComp, SWT.PUSH);
      deselectAll.setText("De-Select All");
      deselectAll.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            treeViewer.deSelectAll();
         };
      });

      return new Pair<>(treeViewer, descriptionLabel);
   }

   public static class AITreeLabelProvider extends CheckBoxStateTreeLabelProvider {

      public AITreeLabelProvider(ICheckBoxStateTreeViewer treeViewer) {
         super(treeViewer);
      }

      @Override
      protected boolean isEnabled(Object element) {
         boolean enabled = false;
         if (element instanceof IAtsActionableItem) {
            IAtsActionableItem ai = (IAtsActionableItem) element;
            if (ai.isActionable()) {
               enabled = true;
            }
         }
         return enabled;
      }

   }

   @Override
   public boolean isEnabled(Object obj) {
      if (obj instanceof IAtsActionableItem) {
         return ((IAtsActionableItem) obj).isActive() && ((IAtsActionableItem) obj).isActionable();
      }
      return false;
   }

   @Override
   public IStatus isValid() {
      Status returnStatus = new Status(IStatus.OK, getClass().getSimpleName(), "");
      try {
         if (isRequiredEntry()) {
            if (getSelectedIAtsActionableItems().isEmpty()) {
               returnStatus =
                  new Status(IStatus.ERROR, getClass().getSimpleName(), "Actionable Item(s) must be selected.");
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return new Status(IStatus.ERROR, getClass().getSimpleName(), ex.getLocalizedMessage());
      }
      return returnStatus;
   }

   public CheckBoxStateFilteredTreeViewer<IAtsActionableItem> getTreeViewer() {
      return treeViewer;
   }

}
