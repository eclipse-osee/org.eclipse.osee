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

package org.eclipse.osee.framework.ui.skynet.group;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class GroupTreeViewer extends TreeViewer {

   private final GroupExplorer groupExplorer;

   public GroupTreeViewer(GroupExplorer groupExplorer, Composite parent) {
      super(parent);
      this.groupExplorer = groupExplorer;
   }

   @Override
   public void refresh() {
      super.refresh();
      //      System.out.println("TreeViewer: refresh");
      groupExplorer.restoreExpandedAndSelection();
   }

   @Override
   public void refresh(boolean updateLabels) {
      super.refresh(updateLabels);
      //      System.out.println("TreeViewer: refresh(updateLabels)");
      groupExplorer.restoreExpandedAndSelection();
   }

}
