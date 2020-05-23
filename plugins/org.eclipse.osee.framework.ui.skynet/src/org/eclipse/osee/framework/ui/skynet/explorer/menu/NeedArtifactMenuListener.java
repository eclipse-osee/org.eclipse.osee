/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.framework.ui.skynet.explorer.menu;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.explorer.ArtifactExplorer;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.widgets.MenuItem;

/**
 * @author Donald G. Dunne
 */
public class NeedArtifactMenuListener implements MenuListener {

   private final HashCollection<Class<? extends Artifact>, MenuItem> menuItemMap;
   private final TreeViewer treeViewer;

   public NeedArtifactMenuListener(ArtifactExplorer artifactExplorer) {
      menuItemMap = new HashCollection<>();
      treeViewer = artifactExplorer.getTreeViewer();
   }

   public void add(MenuItem item) {
      menuItemMap.put(Artifact.class, item);
   }

   @Override
   public void menuHidden(MenuEvent e) {
      // do nothing
   }

   @Override
   public void menuShown(MenuEvent e) {
      IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();

      Object obj = selection.getFirstElement();
      if (obj != null && obj instanceof Artifact) {
         Class<? extends Artifact> selectedClass = obj.getClass().asSubclass(Artifact.class);

         for (Class<? extends Artifact> artifactClass : menuItemMap.keySet()) {
            boolean valid = artifactClass.isAssignableFrom(selectedClass);

            for (MenuItem item : menuItemMap.getValues(artifactClass)) {
               if (!(item.getData() instanceof Exception)) {
                  // Only modify enabling if no error is associated
                  item.setEnabled(valid);
               }
            }
         }
      }
   }
}
