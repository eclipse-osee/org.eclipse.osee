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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ArtifactListDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class ActionTeamListDialog extends ArtifactListDialog {

   XCheckBox recurseChildrenCheck = new XCheckBox("Include all children Team's Actions");
   boolean recurseChildren = false;
   XCheckBox showFinishedCheck = new XCheckBox("Show Completed and Cancelled Workflows");
   boolean showFinished = false;
   XCheckBox showActionCheck = new XCheckBox("Show Action instead of Workflows");
   boolean showAction = true;

   public ActionTeamListDialog(Active active) {
      super(Display.getCurrent().getActiveShell(), null);
      setTitle("Select Team(s)");
      setMessage("Select Team(s)");
      ArrayList<Artifact> arts = new ArrayList<Artifact>();
      try {
         for (Artifact prod : TeamDefinitionArtifact.getTeamDefinitions(active))
            arts.add(prod);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't Load product list.");
      }
      setArtifacts(arts);
   }

   /**
    * Return the style flags for the table viewer.
    * 
    * @return int
    */
   @Override
   protected int getTableStyle() {
      return SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER;
   }

   @Override
   protected Control createDialogArea(Composite container) {

      Control control = super.createDialogArea(container);
      Composite comp = new Composite(control.getParent(), SWT.NONE);
      comp.setLayout(new GridLayout(2, false));
      comp.setLayoutData(new GridData(GridData.FILL_BOTH));

      recurseChildrenCheck.createWidgets(comp, 2);
      recurseChildrenCheck.set(recurseChildren);
      recurseChildrenCheck.addSelectionListener(new SelectionListener() {
         public void widgetDefaultSelected(SelectionEvent e) {
         }

         public void widgetSelected(SelectionEvent e) {
            recurseChildren = recurseChildrenCheck.isSelected();
         };
      });
      showFinishedCheck.createWidgets(comp, 2);
      showFinishedCheck.set(showFinished);
      showFinishedCheck.addSelectionListener(new SelectionListener() {
         public void widgetDefaultSelected(SelectionEvent e) {
         }

         public void widgetSelected(SelectionEvent e) {
            showFinished = showFinishedCheck.isSelected();
         };
      });
      showActionCheck.createWidgets(comp, 2);
      showActionCheck.set(showAction);
      showActionCheck.addSelectionListener(new SelectionListener() {
         public void widgetDefaultSelected(SelectionEvent e) {
         }

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

}
