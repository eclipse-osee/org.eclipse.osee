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

import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.type.IInputListener;
import org.eclipse.osee.framework.ui.skynet.search.page.ArtifactSearchComposite;
import org.eclipse.osee.framework.ui.skynet.search.page.data.ArtifactTypeNode;

public class TreeRefreshListener implements IInputListener<ArtifactTypeNode> {

   private ArtifactSearchComposite parentWindow;

   public TreeRefreshListener(ArtifactSearchComposite parentWindow) {
      this.parentWindow = parentWindow;
      this.parentWindow.getTreeWidget().getInputManager().addInputListener(this);
   }

   public void refresh() {
      this.parentWindow.getTreeWidget().refresh();
   }

   public void addNode(ArtifactTypeNode node) {
      refresh();
   }

   public void removeNode(ArtifactTypeNode node) {
      refresh();
   }

   public void removeAll() {
      refresh();
   }

   public void inputChanged() {
      refresh();
   }

   public void addNodes(Collection<ArtifactTypeNode> nodes) {
      refresh();
   }

   public void nodeChanged(ArtifactTypeNode inNode) {
      refresh();
   }
}
