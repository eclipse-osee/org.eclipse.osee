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

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactDescriptiveLabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;

/**
 * @author Donald G. Dunne
 */
public class ArtifactCheckTreeDialog extends CheckedTreeSelectionDialog {

   public ArtifactCheckTreeDialog(Collection<? extends Artifact> artifacts) {
      this(artifacts, new ArtifactDescriptiveLabelProvider());
   }

   public ArtifactCheckTreeDialog(Collection<? extends Artifact> artifacts, ILabelProvider iLabelProvider) {
      super(Display.getCurrent().getActiveShell(), iLabelProvider, new ArtifactTreeContentProvider());
      if (artifacts != null) setInput(artifacts);
   }

   public ArtifactCheckTreeDialog() {
      this(null);
   }

   public Collection<Artifact> getSelection() {
      ArrayList<Artifact> arts = new ArrayList<Artifact>();
      for (Object obj : getResult())
         arts.add((Artifact) obj);
      return arts;
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control c = super.createDialogArea(container);
      getTreeViewer().setSorter(new ViewerSorter() {
         @SuppressWarnings("unchecked")
         @Override
         public int compare(Viewer viewer, Object e1, Object e2) {
            return getComparator().compare(((Artifact) e1).getDescriptiveName(), ((Artifact) e2).getDescriptiveName());
         }
      });
      return c;
   }

   public void setArtifacts(Collection<? extends Artifact> artifacts) {
      setInput(artifacts);
   }

}
