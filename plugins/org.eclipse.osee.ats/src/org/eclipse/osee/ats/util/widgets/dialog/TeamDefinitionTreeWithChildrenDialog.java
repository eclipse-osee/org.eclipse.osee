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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.core.model.IAtsTeamDefinition;
import org.eclipse.osee.ats.util.AtsObjectLabelProvider;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinitionTreeWithChildrenDialog extends CheckedTreeSelectionDialog {

   XCheckBox recurseChildrenCheck = new XCheckBox("Include all children Team Definition Actions");
   boolean recurseChildren = false;
   protected Composite dialogComp;

   public TeamDefinitionTreeWithChildrenDialog(Active active) throws OseeCoreException {
      this(active, TeamDefinitions.getTeamTopLevelDefinitions(active));
   }

   public TeamDefinitionTreeWithChildrenDialog(Active active, Collection<IAtsTeamDefinition> TeamDefinitions) {
      super(Displays.getActiveShell(), new AtsObjectLabelProvider(), new TeamDefinitionTreeContentProvider(active));
      setTitle("Select Team Definition");
      setMessage("Select Team Definition");
      setComparator(new AtsObjectNameSorter());
      setInput(TeamDefinitions);
   }

   /**
    * @return selected team defs and children if recurseChildren was checked
    */
   public Collection<IAtsTeamDefinition> getResultAndRecursedTeamDefs() throws OseeCoreException {
      Set<IAtsTeamDefinition> teamDefs = new HashSet<IAtsTeamDefinition>(10);
      for (Object obj : getResult()) {
         teamDefs.add((IAtsTeamDefinition) obj);
         if (recurseChildren) {
            teamDefs.addAll(TeamDefinitions.getChildren((IAtsTeamDefinition) obj, true));
         }
      }
      return teamDefs;
   }

   @Override
   protected Control createDialogArea(Composite container) {

      Control control = super.createDialogArea(container);
      dialogComp = new Composite(control.getParent(), SWT.NONE);
      dialogComp.setLayout(new GridLayout(2, false));
      dialogComp.setLayoutData(new GridData(GridData.FILL_BOTH));

      recurseChildrenCheck.createWidgets(dialogComp, 2);
      recurseChildrenCheck.set(recurseChildren);
      recurseChildrenCheck.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            recurseChildren = recurseChildrenCheck.isSelected();
         };
      });

      return container;
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
