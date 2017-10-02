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

package org.eclipse.osee.ats.actions.wizard;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.JaxActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.ats.core.config.ActionableItem;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.help.ui.AtsHelpContext;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsObjectLabelProvider;
import org.eclipse.osee.ats.util.widgets.dialog.AITreeContentProvider;
import org.eclipse.osee.ats.util.widgets.dialog.AtsObjectNameSorter;
import org.eclipse.osee.ats.workflow.ATSXWidgetOptionResolver;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTree;
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
public class NewActionPage1 extends WizardPage {
   private final NewActionWizard wizard;
   private XWidgetPage page;
   protected FilteredCheckboxTree treeViewer;
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

         Pair<FilteredCheckboxTree, Text> results = createActionableItemTreeViewer(comp, wizard.getSelectableAis());
         treeViewer = results.getFirst();
         descriptionLabel = results.getSecond();
         treeViewer.getCheckboxTreeViewer().addCheckStateListener(new ICheckStateListener() {

            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
               getContainer().updateButtons();
            }
         });
         treeViewer.getCheckboxTreeViewer().addCheckStateListener(new CheckStateListener());

         setControl(comp);
         setHelpContexts();

         if (wizard.getInitialAias() != null) {
            treeViewer.setInitalChecked(wizard.getInitialAias());
         }
         ((XText) getXWidget(TITLE)).setFocus();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public static Pair<FilteredCheckboxTree, Text> createActionableItemTreeViewer(Composite comp, Collection<IAtsActionableItem> selectableAis) {
      Composite aiComp = new Composite(comp, SWT.NONE);
      aiComp.setLayout(new GridLayout(1, false));
      aiComp.setLayoutData(new GridData(GridData.FILL_BOTH));

      new Label(aiComp, SWT.NONE).setText("Select Actionable Items:");
      FilteredCheckboxTree treeViewer = new FilteredCheckboxTree(aiComp,
         SWT.CHECK | SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
      treeViewer.getViewer().getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      treeViewer.getViewer().setContentProvider(new AITreeContentProvider(Active.Active));
      treeViewer.getViewer().setLabelProvider(new AtsObjectLabelProvider());
      try {
         if (selectableAis == null) {
            List<IAtsActionableItem> activeActionableItemTree = new LinkedList<>();
            IAtsClient atsClient = AtsClientService.get();
            for (Long aiId : atsClient.getConfigurations().getIdToAi().get(
               atsClient.getConfigurations().getTopActionableItem().getId()).getChildren()) {
               JaxActionableItem jai = atsClient.getConfigurations().getIdToAi().get(aiId);
               if (jai.isActive()) {
                  activeActionableItemTree.add(new ActionableItem(atsClient.getLogger(), atsClient, jai));
               }
            }
            treeViewer.getViewer().setInput(activeActionableItemTree);
         } else {
            treeViewer.getViewer().setInput(selectableAis);
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
            treeViewer.clearChecked();
         };
      });

      return new Pair<FilteredCheckboxTree, Text>(treeViewer, descriptionLabel);
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
            atsAi = AtsClientService.get().getConfigItem(AtsArtifactToken.TopActionableItem);
            if (atsAi != null) {
               treeViewer.getViewer().setSelection(new StructuredSelection(Arrays.asList(atsAi)));
               treeViewer.setInitalChecked(Arrays.asList(atsAi));
            }
         }
         getContainer().updateButtons();
         debugPopulated = true;
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private class CheckStateListener implements ICheckStateListener {
      @Override
      public void checkStateChanged(CheckStateChangedEvent event) {
         IStructuredSelection sel = (IStructuredSelection) treeViewer.getViewer().getSelection();
         if (sel.isEmpty()) {
            return;
         }
         Collection<Object> checked = treeViewer.getChecked();
         if (checked.isEmpty()) {
            descriptionLabel.setText("");
         } else {
            Object obj = checked.iterator().next();
            if (obj instanceof IAtsActionableItem) {
               IAtsActionableItem ai = (IAtsActionableItem) obj;
               descriptionLabel.setText(ai.getDescription());
            } else if (obj instanceof JaxActionableItem) {
               JaxActionableItem jai = (JaxActionableItem) obj;
               descriptionLabel.setText(jai.getDescription());
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
         } else if (obj instanceof JaxActionableItem) {
            JaxActionableItem jai = (JaxActionableItem) obj;
            selected.add(AtsClientService.get().getConfigItem(jai.getUuid()));
         }
      }
      return selected;
   }

   public XWidget getXWidget(String attrName)  {
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
               AWorkbench.popup("ERROR", ActionableItems.getNotActionableItemError(aia));
               treeViewer.getCheckboxTreeViewer().setChecked(aia, false);
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

   public FilteredCheckboxTree getTreeViewer() {
      return treeViewer;
   }

}
