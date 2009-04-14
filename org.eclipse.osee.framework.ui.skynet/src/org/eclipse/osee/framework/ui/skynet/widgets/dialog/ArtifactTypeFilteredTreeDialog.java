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

package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import java.util.Collection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactTypeLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactTypeNameSorter;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.OSEEFilteredTreeDialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTypeFilteredTreeDialog extends OSEEFilteredTreeDialog {

   private final Collection<ArtifactType> artifactTypes;
   private ArtifactType selection;

   public ArtifactTypeFilteredTreeDialog(String title, String message, Collection<ArtifactType> artifactTypes) {
      super(title, message, new PatternFilter());
      this.artifactTypes = artifactTypes;
      setCheckTree(false);
      setMultiSelect(false);
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control comp = super.createDialogArea(container);
      try {
         getTreeViewer().getViewer().setContentProvider(new ArrayTreeContentProvider());
         getTreeViewer().getViewer().setLabelProvider(new ArtifactTypeLabelProvider());
         getTreeViewer().getViewer().setSorter(new ArtifactTypeNameSorter());
         getTreeViewer().getViewer().setInput(artifactTypes);
         getTreeViewer().getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
               IStructuredSelection sel = ((IStructuredSelection) getTreeViewer().getViewer().getSelection());
               if (sel.isEmpty()) {
                  selection = null;
               } else {
                  selection =
                        (ArtifactType) ((IStructuredSelection) getTreeViewer().getViewer().getSelection()).getFirstElement();
               }
            }
         });
         GridData gd = new GridData(GridData.FILL_BOTH);
         gd.heightHint = 500;
         getTreeViewer().getViewer().getTree().setLayoutData(gd);
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return comp;
   }

   @Override
   protected Result isComplete() {
      try {
         if (selection != null) {
            return Result.TrueResult;
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return super.isComplete();
   }

   /**
    * @return the selection
    */
   public ArtifactType getSelection() {
      return selection;
   }
}
