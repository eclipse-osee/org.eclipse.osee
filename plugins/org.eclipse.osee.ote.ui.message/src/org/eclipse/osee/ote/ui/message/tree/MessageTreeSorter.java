/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.message.tree;

import org.eclipse.jface.viewers.ViewerSorter;

public class MessageTreeSorter extends ViewerSorter {

   @Override
   public int category(Object element) {
      // TODO: we should categorize by message type (Mux, PUB SUB, WIRE, etc)
      return 1;
   }
}