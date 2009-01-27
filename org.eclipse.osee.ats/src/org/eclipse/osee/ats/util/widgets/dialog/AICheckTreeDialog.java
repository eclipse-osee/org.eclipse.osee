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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.OSEECheckedFilteredTreeDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * @author Donald G. Dunne
 */
public class AICheckTreeDialog extends OSEECheckedFilteredTreeDialog {

   private static PatternFilter patternFilter = new PatternFilter();
   private final Active active;

   public AICheckTreeDialog(String title, String message, Active active) {
      super(title, message, patternFilter, new AITreeContentProvider(active), new ArtifactLabelProvider());
      this.active = active;
   }

   public Collection<ActionableItemArtifact> getChecked() {
      if (super.getTreeViewer() == null) return Collections.emptyList();
      Set<ActionableItemArtifact> checked = new HashSet<ActionableItemArtifact>();
      for (Object obj : super.getTreeViewer().getChecked()) {
         checked.add((ActionableItemArtifact) obj);
      }
      return checked;
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control comp = super.createDialogArea(container);
      try {
         getTreeViewer().getViewer().setInput(ActionableItemArtifact.getTopLevelActionableItems(active));
         getTreeViewer().getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            /* (non-Javadoc)
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
               try {
                  for (ActionableItemArtifact aia : getChecked()) {
                     if (!aia.isActionable()) {
                        AWorkbench.popup("ERROR", ActionableItemArtifact.getNotActionableItemError(aia));
                     }
                  }
               } catch (Exception ex) {
                  OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return comp;
   }

   @Override
   protected Result isComplete() {
      try {
         for (ActionableItemArtifact aia : getChecked()) {
            if (!aia.isActionable()) {
               return Result.FalseResult;
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return super.isComplete();
   }

}
