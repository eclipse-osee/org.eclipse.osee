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
package org.eclipse.osee.framework.ui.plugin.workspace.internal;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.osee.framework.ui.plugin.workspace.WrapResourceChangeListener;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class ResourceChangeListenerImpl implements IResourceChangeListener {

   private WrapResourceChangeListener listener;
   
   public ResourceChangeListenerImpl(WrapResourceChangeListener listener){
      this.listener = listener;
   }

   @Override
   public void resourceChanged(IResourceChangeEvent event) {
      listener.resourceChanged(event);
   }
   
}
