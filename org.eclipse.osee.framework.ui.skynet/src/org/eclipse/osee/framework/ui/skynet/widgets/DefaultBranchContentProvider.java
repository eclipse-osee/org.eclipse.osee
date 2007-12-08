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
package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.DefaultBranchChangedEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;

/**
 * @author Robert A. Fisher
 */
public class DefaultBranchContentProvider implements ITreeContentProvider, IEventReceiver {
   private SkynetEventManager eventManager = SkynetEventManager.getInstance();
   private final ITreeContentProvider provider;
   private Viewer viewer;

   /**
    * @param provider
    */
   public DefaultBranchContentProvider(final ITreeContentProvider provider) {
      if (provider == null) throw new IllegalArgumentException("provider can not be null");

      this.provider = provider;
      this.viewer = null;

      eventManager.register(DefaultBranchChangedEvent.class, this);
   }

   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      this.viewer = viewer;
   }

   public void dispose() {
      eventManager.unRegisterAll(this);
   }

   public void onEvent(Event event) {

      if (event instanceof DefaultBranchChangedEvent) {
         if (viewer != null) {
            viewer.refresh();
         }
      }
   }

   public boolean runOnEventInDisplayThread() {
      return true;
   }

   public Object[] getChildren(Object parentElement) {
      return provider.getChildren(parentElement);
   }

   public Object getParent(Object element) {
      return provider.getParent(element);
   }

   public boolean hasChildren(Object element) {
      return provider.hasChildren(element);
   }

   public Object[] getElements(Object inputElement) {
      return provider.getElements(BranchPersistenceManager.getInstance().getDefaultBranch());
   }
}
