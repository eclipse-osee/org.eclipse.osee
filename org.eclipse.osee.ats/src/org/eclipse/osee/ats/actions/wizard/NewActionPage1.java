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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.util.widgets.dialog.AITreeContentProvider;
import org.eclipse.osee.ats.workflow.ATSXWidgetOptionResolver;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactNameSorter;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.OSEECheckedFilteredTree;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * @author Donald G. Dunne
 */
public class NewActionPage1 extends WizardPage {
   private final NewActionWizard wizard;
   private WorkPage page;
   private OSEECheckedFilteredTree treeViewer;
   private static PatternFilter patternFilter = new PatternFilter();
   private static Text descriptionLabel;

   /**
    * @param actionWizard
    */
   protected NewActionPage1(NewActionWizard actionWizard) {
      super("Create new ATS Action", "Create ATS Action", null);
      setMessage("Enter title and select impacted items.");
      this.wizard = actionWizard;
   }

   private final XModifiedListener xModListener = new XModifiedListener() {
      public void widgetModified(XWidget widget) {
         getContainer().updateButtons();
      }
   };

   public void createControl(Composite parent) {

      try {
         String xWidgetXml =
               "<WorkPage><XWidget displayName=\"Title\" required=\"true\" xwidgetType=\"XText\" toolTip=\"" + ATSAttributes.TITLE_ATTRIBUTE.getDescription() + "\"/></WorkPage>";
         Composite comp = new Composite(parent, SWT.NONE);
         comp.setLayout(new GridLayout(1, false));
         comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

         page = new WorkPage(xWidgetXml, ATSXWidgetOptionResolver.getInstance());
         page.createBody(null, comp, null, xModListener, true);

         Composite aiComp = new Composite(comp, SWT.NONE);
         aiComp.setLayout(new GridLayout(1, false));
         aiComp.setLayoutData(new GridData(GridData.FILL_BOTH));

         (new Label(aiComp, SWT.NONE)).setText("Select Actionable Items:");
         treeViewer =
               new OSEECheckedFilteredTree(aiComp,
                     SWT.CHECK | SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, patternFilter);
         treeViewer.getViewer().getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
         treeViewer.getViewer().setContentProvider(new AITreeContentProvider(Active.Active));
         treeViewer.getViewer().setLabelProvider(new ArtifactLabelProvider());
         try {
            treeViewer.getViewer().setInput(ActionableItemArtifact.getTopLevelActionableItems(Active.Active));
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
         treeViewer.getViewer().setSorter(new ArtifactNameSorter());
         treeViewer.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
               getContainer().updateButtons();
            }
         });

         (new Label(aiComp, SWT.NONE)).setText("Description of highlighted Actionable Item (if any):");
         descriptionLabel = new Text(aiComp, SWT.BORDER | SWT.WRAP);
         GridData gd = new GridData(GridData.FILL_BOTH);
         gd.heightHint = 30;
         descriptionLabel.setLayoutData(gd);
         descriptionLabel.setEnabled(false);

         treeViewer.getViewer().addSelectionChangedListener(new SelectionChangedListener());

         Button deselectAll = new Button(aiComp, SWT.PUSH);
         deselectAll.setText("De-Select All");
         deselectAll.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
               treeViewer.clearChecked();
            };
         });

         setControl(comp);
         setHelpContexts();
         if (wizard.getInitialAias() != null) treeViewer.setInitalChecked(wizard.getInitialAias());
         ((XText) getXWidget("Title")).setFocus();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private class SelectionChangedListener implements ISelectionChangedListener {
      public void selectionChanged(SelectionChangedEvent event) {
         IStructuredSelection sel = (IStructuredSelection) treeViewer.getViewer().getSelection();
         if (sel.isEmpty()) return;
         ActionableItemArtifact aia = (ActionableItemArtifact) sel.getFirstElement();
         try {
            descriptionLabel.setText(aia.getSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), ""));
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
         }
      }
   }

   private void setHelpContexts() {
      AtsPlugin.getInstance().setHelp(this.getControl(), "new_action_wizard_page_1", "org.eclipse.osee.ats.help.ui");
   }

   public Set<ActionableItemArtifact> getSelectedActionableItemArtifacts() {
      Set<ActionableItemArtifact> selected = new HashSet<ActionableItemArtifact>();
      for (Object obj : treeViewer.getChecked()) {
         selected.add((ActionableItemArtifact) obj);
      }
      return selected;
   }

   public XWidget getXWidget(String attrName) {
      if (page == null) throw new IllegalArgumentException("WorkPage == null");
      return page.getLayoutData(attrName).getXWidget();
   }

   @Override
   public boolean isPageComplete() {
      if (treeViewer.getChecked().size() == 0) return false;
      try {
         for (ActionableItemArtifact aia : getSelectedActionableItemArtifacts()) {
            if (!aia.isActionable()) {
               AWorkbench.popup("ERROR", ActionableItemArtifact.getNotActionableItemError(aia));
               return false;
            }
         }
         Collection<TeamDefinitionArtifact> teamDefs =
               TeamDefinitionArtifact.getImpactedTeamDefs(getSelectedActionableItemArtifacts());
         if (teamDefs.size() == 0) {
            AWorkbench.popup("ERROR", "No Teams Associated with selected Actionable Items");
            return false;
         }
      } catch (Exception ex) {
         AWorkbench.popup("ERROR", ex.getLocalizedMessage());
         return false;
      }
      if (!page.isPageComplete().isTrue()) return false;
      return true;
   }

}
