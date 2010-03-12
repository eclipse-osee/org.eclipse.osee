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
package org.eclipse.osee.ote.ui.mux.msgtable;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.ote.ui.mux.model.MessageModel;

/**
 * @author Ky Komadino
 * 
 */
public class MuxMsgContentProvider implements IStructuredContentProvider {
   private final static Object[] EMPTY_ARRAY = new Object[0];
   private Viewer viewer;

   public void refresh() {
      viewer.refresh();
   }
   
   public void inputChanged(Viewer v, Object oldInput, Object newInput) {
      viewer = v;
   }

   public void dispose() {
   }

   public Object[] getElements(Object parent) {
      if (parent instanceof MessageModel) {
         return ((MessageModel)parent).getChildren().toArray();
      }
      else {
         return EMPTY_ARRAY;
      }
   }
}
