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

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.menu.IGlobalMenuHelper#getSelectedArtifacts()
    */
   public Collection<Artifact> getArtifacts() {
      Set<Artifact> artifacts = new HashSet<Artifact>();
      if (treeViewer == null || treeViewer.getTree().isDisposed()) return artifacts;
      IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
      Iterator<?> iterator = selection.iterator();
      while (iterator.hasNext()) {
         Object obj = iterator.next();
         if (obj instanceof Artifact) artifacts.add((Artifact) obj);
      }
      return artifacts;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.menu.IGlobalMenuHelper#getValidMenuItems()
    */
   public Collection<GlobalMenuItem> getValidMenuItems() {
      return GlobalMenuItem.ALL;
   }

}
