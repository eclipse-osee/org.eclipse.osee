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

import java.util.Collection;
import java.util.LinkedList;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.ui.skynet.explorer.ArtifactExplorer;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.widgets.MenuItem;

/**
 * @author Donald G. Dunne
 */
public class NeedProjectMenuListener implements MenuListener {
   Collection<MenuItem> items;
   private final TreeViewer treeViewer;

   public NeedProjectMenuListener(ArtifactExplorer artifactExplorer) {
      items = new LinkedList<>();
      treeViewer = artifactExplorer.getTreeViewer();
   }

   public void add(MenuItem item) {
      items.add(item);
   }

   @Override
   public void menuHidden(MenuEvent e) {
      // do nothing
   }

   @Override
   public void menuShown(MenuEvent e) {
      boolean valid = treeViewer.getInput() != null;
      for (MenuItem item : items) {
         // Only modify enabling if no error is associated
         if (!(item.getData() instanceof Exception)) {
            item.setEnabled(valid);
         }
      }
   }
}
