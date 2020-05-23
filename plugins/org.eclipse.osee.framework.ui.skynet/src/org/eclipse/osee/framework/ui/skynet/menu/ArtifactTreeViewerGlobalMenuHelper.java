/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.menu;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.menu.GlobalMenu.GlobalMenuItem;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTreeViewerGlobalMenuHelper implements IGlobalMenuHelper {

   private final TreeViewer treeViewer;

   public ArtifactTreeViewerGlobalMenuHelper(TreeViewer treeViewer) {
      this.treeViewer = treeViewer;
   }

   @Override
   public Collection<Artifact> getArtifacts() {
      Set<Artifact> artifacts = new HashSet<>();
      if (treeViewer == null || treeViewer.getTree().isDisposed()) {
         return artifacts;
      }
      IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
      Iterator<?> iterator = selection.iterator();
      while (iterator.hasNext()) {
         Object obj = iterator.next();
         if (obj instanceof Artifact) {
            artifacts.add((Artifact) obj);
         }
      }
      return artifacts;
   }

   @Override
   public Collection<GlobalMenuItem> getValidMenuItems() {
      return GlobalMenuItem.ALL;
   }

}
