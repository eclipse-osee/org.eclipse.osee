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
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactDescriptiveLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author Donald G. Dunne
 */
public class ArtifactListDialog extends ListDialog {

   boolean requireSelection = true;

   /**
    * @return the requireSelection
    */
   public boolean isRequireSelection() {
      return requireSelection;
   }

   /**
    * @param requireSelection the requireSelection to set
    */
   public void setRequireSelection(boolean requireSelection) {
      this.requireSelection = requireSelection;
   }

   public ArtifactListDialog(Shell parent) {
      this(parent, null);
   }

   public ArtifactListDialog(Shell parent, Collection<? extends Artifact> artifacts) {
      super(parent);
      setContentProvider(new ArrayContentProvider());
      setLabelProvider(new ArtifactDescriptiveLabelProvider());
      setShellStyle(getShellStyle() | SWT.RESIZE);
   }

   public Artifact getSelection() {
      return (Artifact) getResult()[0];
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control c = super.createDialogArea(container);
      getTableViewer().setSorter(new ViewerSorter() {
         @SuppressWarnings("unchecked")
         @Override
         public int compare(Viewer viewer, Object e1, Object e2) {
            return getComparator().compare(((Artifact) e1).getName(), ((Artifact) e2).getName());
         }
      });
      return c;
   }

   public void setArtifacts(Collection<? extends Artifact> artifacts) {
      setInput(artifacts);
   }
   
   public void updateArtifacts(Collection<? extends Artifact> artifacts) {
      getTableViewer().setInput(artifacts);
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
