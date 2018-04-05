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
import java.util.HashSet;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactNameSorter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class FilteredCheckboxTreeArtifactDialog extends FilteredCheckboxTreeDialog<Artifact> {

   public FilteredCheckboxTreeArtifactDialog(String title, String message, Collection<? extends Artifact> selectable) {
      this(title, message, selectable, new ArrayTreeContentProvider(), new ArtifactLabelProvider());
   }

   public FilteredCheckboxTreeArtifactDialog(String title, String message, Collection<? extends Artifact> selectable, ILabelProvider labelProvider) {
      this(title, message, selectable, new ArrayTreeContentProvider(), labelProvider);
   }

   public FilteredCheckboxTreeArtifactDialog(String title, String message, Collection<? extends Artifact> selectable, ITreeContentProvider contentProvider, ILabelProvider labelProvider) {
      super(title, message, new HashSet<Artifact>(selectable), contentProvider, labelProvider,
         new ArtifactNameSorter());
   }

   public FilteredCheckboxTreeArtifactDialog(String title, Collection<? extends Artifact> selectable) {
      this(title, title, selectable, new ArtifactLabelProvider());
   }

   @Override
   protected Control createDialogArea(Composite container) {
      return super.createDialogArea(container);
   }

   @Override
   protected Result isComplete() {
      return super.isComplete();
   }

}
