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
package org.eclipse.osee.ats.util.widgets.dialog;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsObjectLabelProvider;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author Donald G. Dunne
 */
public class ActionTeamListDialog extends ListDialog {

   XCheckBox recurseChildrenCheck = new XCheckBox("Include all children Team's Actions");
   boolean recurseChildren = false;
   XCheckBox showFinishedCheck = new XCheckBox("Show Completed and Cancelled Workflows");
   boolean showFinished = false;
   XCheckBox showActionCheck = new XCheckBox("Show Action instead of Workflows");
   boolean showAction = false;
   boolean requireSelection = true;
   private ViewerSorter viewerSorter;

   public ActionTeamListDialog(Active active) {
      super(Displays.getActiveShell());
      setContentProvider(new ArrayContentProvider());
      setLabelProvider(new AtsObjectLabelProvider());
      setShellStyle(getShellStyle() | SWT.RESIZE);
      setTitle("Select Team(s)");
      setMessage("Select Team(s)");
      ArrayList<IAtsTeamDefinition> arts = new ArrayList<IAtsTeamDefinition>();
      try {
         for (IAtsTeamDefinition prod : TeamDefinitions.getTeamDefinitions(active, AtsClientService.get().getConfig())) {
            arts.add(prod);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Can't Load product list.");
      }
      setArtifacts(arts);
   }

   /**
    * Return the style flags for the table viewer.
    */
   @Override
   protected int getTableStyle() {
      return SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER;
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control c = super.createDialogArea(container);
      if (viewerSorter == null) {
         getTableViewer().setSorter(new ViewerSorter() {
            @SuppressWarnings("unchecked")
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
               return getComparator().compare(((Artifact) e1).getName(), ((Artifact) e2).getName());
            }
         });
      } else {
         getTableViewer().setSorter(viewerSorter);
      }

      Composite comp = new Composite(c.getParent(), SWT.NONE);
      comp.setLayout(new GridLayout(2, false));
      comp.setLayoutData(new GridData(GridData.FILL_BOTH));

      recurseChildrenCheck.createWidgets(comp, 2);
      recurseChildrenCheck.set(recurseChildren);
      recurseChildrenCheck.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            recurseChildren = recurseChildrenCheck.isSelected();
         };
      });
      showFinishedCheck.createWidgets(comp, 2);
      showFinishedCheck.set(showFinished);
      showFinishedCheck.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            showFinished = showFinishedCheck.isSelected();
         };
      });
      showActionCheck.createWidgets(comp, 2);
      showActionCheck.set(showAction);
      showActionCheck.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            showAction = showActionCheck.isSelected();
         };
      });

      return container;
   }

   public boolean isShowFinished() {
      return showFinished;
   }

   public boolean isShowAction() {
      return showAction;
   }

   /**
    * @param showFinished the showFinished to set
    */
   public void setShowFinished(boolean showFinished) {
      this.showFinished = showFinished;
   }

   /**
    * @param showAction the showAction to set
    */
   public void setShowAction(boolean showAction) {
      this.showAction = showAction;
   }

   /**
    * @return the recurseChildren
    */
   public boolean isRecurseChildren() {
      return recurseChildren;
   }

   /**
    * @param recurseChildren the recurseChildren to set
    */
   public void setRecurseChildren(boolean recurseChildren) {
      this.recurseChildren = recurseChildren;
   }

   public boolean isRequireSelection() {
      return requireSelection;
   }

   public void setRequireSelection(boolean requireSelection) {
      this.requireSelection = requireSelection;
   }

   public IAtsTeamDefinition getSelection() {
      return (IAtsTeamDefinition) getResult()[0];
   }

   public void setArtifacts(Collection<? extends IAtsTeamDefinition> teamDefs) {
      setInput(teamDefs);
   }

   public void updateArtifacts(Collection<? extends IAtsTeamDefinition> teamDefs) {
      getTableViewer().setInput(teamDefs);
      getTableViewer().refresh();
   }

   @Override
   protected void okPressed() {
      if (requireSelection && getTableViewer().getSelection().isEmpty()) {
         AWorkbench.popup("ERROR", "Must make selection.");
         return;
      }
      super.okPressed();
   }

}
