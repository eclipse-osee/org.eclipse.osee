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

package org.eclipse.osee.framework.ui.plugin.workspace.internal;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.osee.framework.ui.plugin.workspace.WrapResourceChangeListener;

/**
 * @author Andrew M. Finkbeiner
 */
public class ResourceChangeListenerImpl implements IResourceChangeListener {

   private final WrapResourceChangeListener listener;

   public ResourceChangeListenerImpl(WrapResourceChangeListener listener) {
      this.listener = listener;
   }

   @Override
   public void resourceChanged(IResourceChangeEvent event) {
      listener.resourceChanged(event);
   }

}
