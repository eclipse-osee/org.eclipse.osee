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
package org.eclipse.osee.framework.ui.skynet.search.page.actions;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.ui.skynet.search.page.ArtifactSearchComposite;
import org.eclipse.osee.framework.ui.skynet.search.page.SkynetArtifactAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public class ListSelectionListener implements ISelectionChangedListener, SelectionListener {

   private ArtifactSubtypeDescriptor selectedArtifact;
   private ArtifactSearchComposite parentWindow;

   public ListSelectionListener(ArtifactSearchComposite parentWindow) {
      this.parentWindow = parentWindow;
      this.parentWindow.addSelectionListener(this);
      this.parentWindow.getListWidget().addSelectionListener(this);
   }

   public void selectionChanged(SelectionChangedEvent event) {
      IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      selectedArtifact = (ArtifactSubtypeDescriptor) selection.getFirstElement();
   }

   public void addCurrentSelectionToTree() {
      if (parentWindow.getTreeWidget() != null && selectedArtifact != null) {
         int revision = parentWindow.getRevisionDataManager().getRevision();
         int branchId =
               parentWindow.getRevisionWidget().getBranchId(parentWindow.getRevisionDataManager().getBranchName());
         parentWindow.getTreeWidget().getInputManager().addNode(
               SkynetArtifactAdapter.getInstance().createArtifactTypeNode(selectedArtifact, branchId, revision));
         parentWindow.getTreeWidget().refresh();
      }
   }

   public void widgetSelected(SelectionEvent e) {
      addCurrentSelectionToTree();
   }

   public void widgetDefaultSelected(SelectionEvent e) {
      widgetSelected(e);
   }
}
