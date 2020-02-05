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

package org.eclipse.osee.ats.ide.actions.wizard;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osee.ats.api.ai.ActionableItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.help.ui.AtsHelpContext;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.IAtsClient;
import org.eclipse.osee.ats.ide.util.widgets.dialog.AITreeContentProvider;
import org.eclipse.osee.ats.ide.util.widgets.dialog.AtsObjectNameSorter;
import org.eclipse.osee.ats.ide.workflow.ATSXWidgetOptionResolver;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.util.IsEnabled;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.checkbox.CheckBoxStateFilteredTreeViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.checkbox.CheckBoxStateTreeLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.checkbox.ICheckBoxStateTreeListener;
import org.eclipse.osee.framework.ui.skynet.widgets.checkbox.ICheckBoxStateTreeViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * @author Donald G. Dunne
 */
public class NewActionPage1 extends WizardPage implements IsEnabled {
   private final NewActionWizard wizard;
   private XWidgetPage page;
   protected CheckBoxStateFilteredTreeViewer<IAtsActionableItem> treeViewer;
   private Text descriptionLabel;
   private boolean debugPopulated = false;
   private static IAtsActionableItem atsAi;
   protected static final String TITLE = "Title";

   protected NewActionPage1(NewActionWizard actionWizard) {
      super("Create new ATS Action", "Create ATS Action", null);
      setMessage("Enter title and select impacted items.");
      this.wizard = actionWizard;
   }

   private final XModifiedListener xModListener = new XModifiedListener() {
      @Override
      public void widgetModified(XWidget widget) {
         getContainer().updateButtons();
      }
   };

   protected String getWidgetXml() {
      return "<WorkPage>" + //
         "<XWidget displayName=\"" + TITLE + "\" storageName=\"Name\" required=\"true\" xwidgetType=\"XText\" toolTip=\"" + AtsAttributeTypes.Title.getDescription() + "\"/>" + //
         "</WorkPage>";
   }

   @Override
   public void createControl(Composite parent) {

      try {
         String xWidgetXml = getWidgetXml();
         Composite comp = new Composite(parent, SWT.NONE);
         comp.setLayout(new GridLayout(1, false));
         comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

         page = new XWidgetPage(xWidgetXml, ATSXWidgetOptionResolver.getInstance());
         page.createBody(null, comp, null, xModListener, true);

         ((XText) getXWidget(TITLE)).getLabelWidget().addListener(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
               if (event.button == 3) {
                  handlePopulateWithDebugInfo();
               }
            }
         });

         Pair<CheckBoxStateFilteredTreeViewer<IAtsActionableItem>, Text> results =
            createActionableItemTreeViewer(comp, wizard.getSelectableAis());
         treeViewer = results.getFirst();
         treeViewer.setEnabledChecker(this);
         descriptionLabel = results.getSecond();
         treeViewer.addCheckListener(new ICheckBoxStateTreeListener() {
            @Override
            public void checkStateNodesChanged() {
               getContainer().updateButtons();
            }
         });
         treeViewer.addCheckListener(new CheckStateListener());

         setControl(comp);
         setHelpContexts();

         if (wizard.getInitialAias() != null) {
            treeViewer.setChecked(wizard.getInitialAias());
         }
         ((XText) getXWidget(TITLE)).setFocus();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public static Pair<CheckBoxStateFilteredTreeViewer<IAtsActionableItem>, Text> createActionableItemTreeViewer(Composite comp, Collection<IAtsActionableItem> selectableAis) {
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
            IAtsClient atsClient = AtsClientService.get();
            AtsConfigurations configs = atsClient.getConfigService().getConfigurations();
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
      gridData1.heightHint = 400;
      treeViewer.setLayoutData(gridData1);

      new Label(aiComp, SWT.NONE).setText("Description of highlighted Actionable Item (if any):");
      Text descriptionLabel = new Text(aiComp, SWT.BORDER | SWT.WRAP);
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

   /**
    * Method is used to quickly create a unique action against the ATS actionable item. This is used for developmental
    * and training purposes which is why it's in production code.
    */
   private void handlePopulateWithDebugInfo() {
      if (debugPopulated) {
         return;
      }
      try {
         ((XText) getXWidget(TITLE)).set("tt");
         if (atsAi == null) {
            atsAi = AtsClientService.get().getActionableItemService().getActionableItemById(
               AtsArtifactToken.TopActionableItem);
            if (atsAi != null) {
               treeViewer.getViewer().setSelection(new StructuredSelection(Arrays.asList(atsAi)));
               treeViewer.setChecked(atsAi, true);
            }
         }
         getContainer().updateButtons();
         debugPopulated = true;
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
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

   private void setHelpContexts() {
      HelpUtil.setHelp(this.getControl(), AtsHelpContext.NEW_ACTION_PAGE_1);
   }

   public Set<IAtsActionableItem> getSelectedIAtsActionableItems() {
      Set<IAtsActionableItem> selected = new HashSet<>();
      for (Object obj : treeViewer.getChecked()) {
         if (obj instanceof IAtsActionableItem) {
            selected.add((IAtsActionableItem) obj);
         } else if (obj instanceof ActionableItem) {
            ActionableItem ai = (ActionableItem) obj;
            selected.add(AtsClientService.get().getQueryService().getConfigItem(ai.getId()));
         }
      }
      return selected;
   }

   public XWidget getXWidget(String attrName) {
      Conditions.checkNotNull(page, "WorkPage");
      return page.getLayoutData(attrName).getXWidget();
   }

   @Override
   public boolean isPageComplete() {
      if (treeViewer.getChecked().isEmpty()) {
         return false;
      }
      try {
         for (IAtsActionableItem aia : getSelectedIAtsActionableItems()) {
            if (!aia.isActionable() || !userActionCreationEnabled(aia)) {
               AWorkbench.popup("ERROR",
                  AtsClientService.get().getActionableItemService().getNotActionableItemError(aia));
               treeViewer.setChecked(aia, false);
               return false;
            }
         }
         Collection<IAtsTeamDefinition> teamDefs =
            TeamDefinitions.getImpactedTeamDefs(getSelectedIAtsActionableItems());
         if (teamDefs.isEmpty()) {
            AWorkbench.popup("ERROR", "No Teams Associated with selected Actionable Items");
            return false;
         }
      } catch (Exception ex) {
         AWorkbench.popup("ERROR", ex.getLocalizedMessage());
         return false;
      }
      if (!page.isPageComplete().isTrue()) {
         return false;
      }
      return true;
   }

   protected boolean userActionCreationEnabled(IAtsActionableItem aia) {
      return aia.isAllowUserActionCreation();
   }

   public CheckBoxStateFilteredTreeViewer<IAtsActionableItem> getTreeViewer() {
      return treeViewer;
   }

   @Override
   public boolean isEnabled(Object obj) {
      if (obj instanceof IAtsActionableItem) {
         return ((IAtsActionableItem) obj).isActive() && ((IAtsActionableItem) obj).isActionable();
      }
      return false;
   }

}
