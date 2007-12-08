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
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.menu.GlobalMenu.GlobalMenuItem;
import org.eclipse.search.ui.text.Match;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTableViewerGlobalMenuHelper implements IGlobalMenuHelper {

   private final org.eclipse.jface.viewers.TableViewer tableViewer;

   public ArtifactTableViewerGlobalMenuHelper(TableViewer tableViewer) {
      this.tableViewer = tableViewer;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.menu.IGlobalMenuHelper#getSelectedArtifacts()
    */
   public Collection<Artifact> getArtifacts() {
      Set<Artifact> artifacts = new HashSet<Artifact>();
      if (tableViewer == null || tableViewer.getTable().isDisposed()) return artifacts;
      IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
      Iterator<?> iterator = selection.iterator();
      while (iterator.hasNext()) {
         Object obj = iterator.next();
         if (obj instanceof Artifact)
            artifacts.add((Artifact) obj);
         else if ((obj instanceof Match) && (((Match) obj).getElement() instanceof Artifact)) artifacts.add((Artifact) ((Match) obj).getElement());
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
