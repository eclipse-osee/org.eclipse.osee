/*********************************************************************
 * Copyright (c) 2011 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public final class ArtifactStructuredSelection implements IStructuredSelection {

   private final List<Artifact> selectedItems;

   public ArtifactStructuredSelection(Artifact... selectedItems) {
      this(Arrays.asList(selectedItems));
   }

   public ArtifactStructuredSelection(List<Artifact> selectedItems) {
      this.selectedItems = selectedItems;
   }

   @Override
   public boolean isEmpty() {
      return selectedItems.isEmpty();
   }

   @Override
   public Artifact getFirstElement() {
      return isEmpty() ? null : selectedItems.iterator().next();
   }

   @Override
   public Iterator<Artifact> iterator() {
      return selectedItems.iterator();
   }

   @Override
   public int size() {
      return selectedItems.size();
   }

   @Override
   public Object[] toArray() {
      return selectedItems.toArray();
   }

   @Override
   public List<Artifact> toList() {
      return selectedItems;
   }
}
