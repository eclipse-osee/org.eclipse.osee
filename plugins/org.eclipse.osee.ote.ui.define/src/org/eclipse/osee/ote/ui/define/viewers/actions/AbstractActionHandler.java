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
package org.eclipse.osee.ote.ui.define.viewers.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.ui.define.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractActionHandler extends Action implements ISelectionChangedListener {

   private final StructuredViewer viewer;

   public AbstractActionHandler(StructuredViewer viewer, String text) throws Exception {
      super(text);
      updateState();
      this.viewer = viewer;
      viewer.addSelectionChangedListener(this);
   }

   public AbstractActionHandler(StructuredViewer viewer, String text, ImageDescriptor image) throws Exception {
      super(text, image);
      updateState();
      this.viewer = viewer;
      viewer.addSelectionChangedListener(this);
   }

   protected StructuredViewer getViewer() {
      return viewer;
   }

   public abstract void updateState() throws Exception;

   @Override
   public void selectionChanged(SelectionChangedEvent event) {
      try {
         updateState();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   };
}
