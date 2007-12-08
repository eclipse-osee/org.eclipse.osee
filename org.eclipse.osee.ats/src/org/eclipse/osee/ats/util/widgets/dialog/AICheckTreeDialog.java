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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.wizard.ActionableItemFilter;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactDescriptiveLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactNameSorter;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;

/**
 * @author Donald G. Dunne
 */
public class AICheckTreeDialog extends CheckedTreeSelectionDialog {

   private ActionableItemFilter nameFilter;
   private XText filterText;

   public AICheckTreeDialog(String title, String message, Active active) {
      super(Display.getCurrent().getActiveShell(), new ArtifactDescriptiveLabelProvider(), new AITreeContentProvider(
            active));
      try {
         setInput(ActionableItemArtifact.getTopLevelActionableItems(active));
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      this.filterText = new XText("Filter");
      setTitle(title);
      setMessage(message);
   }

   public Collection<ActionableItemArtifact> getSelection() {
      ArrayList<ActionableItemArtifact> arts = new ArrayList<ActionableItemArtifact>();
      for (Object obj : getResult())
         arts.add((ActionableItemArtifact) obj);
      return arts;
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control comp = super.createDialogArea(container);
      getTreeViewer().setSorter(new ArtifactNameSorter());

      nameFilter = new ActionableItemFilter(getTreeViewer());
      getTreeViewer().addFilter(nameFilter);

      Composite filterComp = new Composite(comp.getParent(), SWT.NONE);
      filterComp.setLayout(new GridLayout(2, false));
      filterComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      (new Label(filterComp, SWT.NONE)).setText("Filter");
      filterText.setDisplayLabel(false);
      filterText.createWidgets(filterComp, 2);
      filterText.addModifyListener(new ModifyListener() {
         public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
            nameFilter.setContains(filterText.get());
            getTreeViewer().refresh();
         };
      });

      return comp;
   }

}
