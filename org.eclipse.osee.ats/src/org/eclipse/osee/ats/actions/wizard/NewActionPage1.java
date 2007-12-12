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

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.util.widgets.dialog.AITreeContentProvider;
import org.eclipse.osee.ats.workflow.ATSXWidgetOptionResolver;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactNameSorter;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class NewActionPage1 extends WizardPage {
   private final NewActionWizard wizard;
   private AtsWorkPage page;
   private XText filterText;
   private CheckboxTreeViewer treeViewer;
   private ActionableItemFilter nameFilter;
   private Set<ActionableItemArtifact> selectedAias;

   /**
    * @param actionWizard
    */
   public NewActionPage1(NewActionWizard actionWizard) {
      super("Create new ATS Action", "Create ATS Action", null);
      setMessage("Enter title and select impacted items.");
      this.wizard = actionWizard;
      this.filterText = new XText("Filter");
      selectedAias = new HashSet<ActionableItemArtifact>();
      if (wizard.getCheckedArtifacts() != null) selectedAias.addAll(wizard.getCheckedArtifacts());
   }

   private XModifiedListener xModListener = new XModifiedListener() {
      public void widgetModified(XWidget widget) {
         getContainer().updateButtons();
      }
   };

   public void createControl(Composite parent) {

      String xWidgetXml =
            "<WorkPage><XWidget displayName=\"Title\" required=\"true\" xwidgetType=\"XText\" toolTip=\"" + ATSAttributes.TITLE_ATTRIBUTE.getDescription() + "\"/></WorkPage>";
      Composite comp = new Composite(parent, SWT.NONE);
      comp.setLayout(new GridLayout(1, false));
      comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      page = new AtsWorkPage("Action", "", xWidgetXml, ATSXWidgetOptionResolver.getInstance());
      page.createBody(null, comp, null, xModListener, true);

      Composite aiComp = new Composite(comp, SWT.NONE);
      aiComp.setLayout(new GridLayout(1, false));
      aiComp.setLayoutData(new GridData(GridData.FILL_BOTH));

      (new Label(aiComp, SWT.NONE)).setText("Select Actionable Items:");
      treeViewer =
            new CheckboxTreeViewer(aiComp,
                  SWT.MULTI | SWT.CHECK | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
      treeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      treeViewer.setContentProvider(new AITreeContentProvider(Active.Active));
      treeViewer.setLabelProvider(new ArtifactLabelProvider());
      try {
         // Load all AIs due to performance
         ActionableItemArtifact.getActionableItems();
         treeViewer.setInput(ActionableItemArtifact.getTopLevelActionableItems(Active.Active));
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
      nameFilter = new ActionableItemFilter(treeViewer);
      treeViewer.addFilter(nameFilter);
      treeViewer.setSorter(new ArtifactNameSorter());
      treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
         public void selectionChanged(SelectionChangedEvent event) {
            storeSelectedActionableItemArtifacts();
            getContainer().updateButtons();
         }
      });
      if (wizard.getCheckedArtifacts() != null && wizard.getCheckedArtifacts().size() > 0) {
         treeViewer.setCheckedElements(wizard.getCheckedArtifacts().toArray(
               new Object[wizard.getCheckedArtifacts().size()]));
         for (Artifact art : wizard.getCheckedArtifacts())
            treeViewer.reveal(art);
      }

      Composite filterComp = new Composite(aiComp, SWT.NONE);
      filterComp.setLayout(new GridLayout(2, false));
      filterComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      (new Label(filterComp, SWT.NONE)).setText("Filter");
      filterText.setDisplayLabel(false);
      filterText.createWidgets(filterComp, 2);
      filterText.addModifyListener(new ModifyListener() {
         public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
            nameFilter.setContains(filterText.get());
            treeViewer.refresh();
         };
      });

      setControl(comp);
      setHelpContexts();
   }

   private void setHelpContexts() {
      AtsPlugin.getInstance().setHelp(this.getControl(), "new_action_wizard_page_1");
   }

   private void storeSelectedActionableItemArtifacts() {
      selectedAias.clear();
      for (Object obj : treeViewer.getCheckedElements())
         selectedAias.add((ActionableItemArtifact) obj);
   }

   public Set<ActionableItemArtifact> getSelectedActionableItemArtifacts() {
      return selectedAias;
   }

   public XWidget getXWidget(String attrName) {
      if (page == null) throw new IllegalArgumentException("WorkPage == null");
      return page.getLayoutData(attrName).getXWidget();
   }

   @Override
   public boolean isPageComplete() {
      if (treeViewer.getCheckedElements().length == 0) return false;
      try {
         TeamDefinitionArtifact.getImpactedTeamDefs(getSelectedActionableItemArtifacts());
      } catch (Exception ex) {
         AWorkbench.popup("ERROR", ex.getLocalizedMessage());
         return false;
      }
      if (!page.isPageComplete().isTrue()) return false;
      return true;
   }

}
