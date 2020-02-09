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

package org.eclipse.osee.ats.ide.util.widgets.dialog;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.AtsObjectLabelProvider;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinitionTreeWithChildrenDialog extends FilteredCheckboxTreeDialog<TeamDefinition> {

   XCheckBox recurseChildrenCheck = new XCheckBox("Include all children Team Definition Actions");
   boolean recurseChildren = false;
   protected Composite dialogComp;

   public TeamDefinitionTreeWithChildrenDialog(Active active) {
      this(active, AtsClientService.get().getTeamDefinitionService().getTeamTopLevelJaxDefinitions(active));
   }

   public TeamDefinitionTreeWithChildrenDialog(Active active, Collection<TeamDefinition> TeamDefinitions) {
      super("Select Team Defintion", "Select Team Definition", new TeamDefinitionTreeContentProvider(active),
         new AtsObjectLabelProvider(), new AtsObjectNameSorter());
      setInput(TeamDefinitions);
   }

   /**
    * @return selected team defs and children if recurseChildren was checked
    */
   public Collection<TeamDefinition> getResultAndRecursedTeamDefs() {
      Set<TeamDefinition> teamDefs = new HashSet<>(10);
      for (Object obj : getResult()) {
         teamDefs.add((TeamDefinition) obj);
         if (recurseChildren) {
            teamDefs.addAll(getChildren((TeamDefinition) obj, true));
         }
      }
      return teamDefs;
   }

   public static Collection<TeamDefinition> getChildren(TeamDefinition teamDef, boolean recurse) {
      Set<TeamDefinition> children = new HashSet<>();
      for (Long childId : teamDef.getChildren()) {
         TeamDefinition child =
            AtsClientService.get().getConfigService().getConfigurations().getIdToTeamDef().get(childId);
         if (child != null) {
            children.add(child);
            if (recurse) {
               children.addAll(getChildren(child, recurse));
            }
         }
      }
      return children;
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

}
