/*******************************************************************************
 * Copyright (c) 2014 Boeing.
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
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactTypeLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactTypeNameSorter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class FilteredCheckboxArtifactTypeDialog extends FilteredCheckboxTreeDialog {

   private Collection<? extends ArtifactTypeToken> selectable;

   public FilteredCheckboxArtifactTypeDialog(String title, String message) {
      this(title, message, ArtifactTypeManager.getAllTypes(), new ArrayTreeContentProvider(),
         new ArtifactTypeLabelProvider());
   }

   public FilteredCheckboxArtifactTypeDialog(String title, String message, Collection<? extends ArtifactTypeToken> selectable) {
      this(title, message, selectable, new ArrayTreeContentProvider(), new ArtifactTypeLabelProvider());
   }

   public FilteredCheckboxArtifactTypeDialog(String title, String message, Collection<? extends ArtifactTypeToken> selectable, ILabelProvider labelProvider) {
      this(title, message, selectable, new ArrayTreeContentProvider(), labelProvider);
   }

   public FilteredCheckboxArtifactTypeDialog(String title, String message, Collection<? extends ArtifactTypeToken> selectable, ITreeContentProvider contentProvider, ILabelProvider labelProvider) {
      super(title, message, contentProvider, labelProvider, new ArtifactTypeNameSorter());
      this.selectable = selectable;
   }

   public FilteredCheckboxArtifactTypeDialog(String title, Collection<? extends ArtifactTypeToken> selectable) {
      this(title, title, selectable, new ArtifactLabelProvider());
   }

   @SuppressWarnings("unchecked")
   @Override
   public Collection<ArtifactTypeToken> getChecked() {
      if (super.getTreeViewer() == null) {
         return Collections.emptyList();
      }
      Set<ArtifactTypeToken> checked = new HashSet<>();
      for (Object obj : getResult()) {
         checked.add((ArtifactTypeToken) obj);
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

   public Collection<? extends ArtifactTypeToken> getSelectable() {
      return selectable;
   }

   public void setSelectable(Collection<? extends ArtifactTypeToken> selectable) {
      this.selectable = selectable;
   }

}
