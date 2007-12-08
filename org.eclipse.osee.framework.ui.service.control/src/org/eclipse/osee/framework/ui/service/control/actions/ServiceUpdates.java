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
package org.eclipse.osee.framework.ui.service.control.actions;

import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.type.IInputListener;
import org.eclipse.osee.framework.jdk.core.type.TreeParent;
import org.eclipse.osee.framework.ui.service.control.widgets.IServiceManager;

/**
 * @author Roberto E. Escobar
 */
public class ServiceUpdates implements IInputListener<TreeParent> {

   private IServiceManager<TreeParent> parentWindow;

   @SuppressWarnings("unchecked")
   public ServiceUpdates(IServiceManager parentWindow) {
      this.parentWindow = parentWindow;
      this.parentWindow.getInputManager().addInputListener(this);
   }

   public void refresh() {
      this.parentWindow.getServicesViewer().refresh();
   }

   public void addNode(TreeParent node) {
      refresh();
   }

   public void removeNode(TreeParent node) {
      refresh();
   }

   public void removeAll() {
      refresh();
   }

   public void inputChanged() {
      refresh();
   }

   public void addNodes(Collection<TreeParent> nodes) {
      refresh();
   }

   public void nodeChanged(TreeParent inNode) {
      refresh();
   }
}
