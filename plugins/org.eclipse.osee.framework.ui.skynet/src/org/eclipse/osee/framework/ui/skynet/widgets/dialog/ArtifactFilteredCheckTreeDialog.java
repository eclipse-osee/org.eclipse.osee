/*******************************************************************************
 * Copyright (c) 2010 Boeing.
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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactNameSorter;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.OSEECheckedFilteredTreeDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class ArtifactFilteredCheckTreeDialog extends OSEECheckedFilteredTreeDialog {

   private Collection<? extends Artifact> selectable;

   public ArtifactFilteredCheckTreeDialog(String title, String message, Collection<? extends Artifact> selectable) {
      super(title, message, new ArrayTreeContentProvider(), new ArtifactLabelProvider(), new ArtifactNameSorter());
      this.selectable = selectable;
   }

   public Collection<Artifact> getChecked() {
      if (super.getTreeViewer() == null) {
         return Collections.emptyList();
      }
      Set<Artifact> checked = new HashSet<Artifact>();
      for (Object obj : super.getTreeViewer().getChecked()) {
         checked.add((Artifact) obj);
      }
      return checked;
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control comp = super.createDialogArea(container);
      try {
         getTreeViewer().getViewer().setInput(selectable);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return comp;
   }

   @Override
   protected Result isComplete() {
      return super.isComplete();
   }

   public Collection<? extends Artifact> getSelectable() {
      return selectable;
   }

   public void setSelectable(Collection<Artifact> selectable) {
      this.selectable = selectable;
   }

}
