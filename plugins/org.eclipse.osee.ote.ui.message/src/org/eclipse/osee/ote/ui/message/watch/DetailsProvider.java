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
package org.eclipse.osee.ote.ui.message.watch;

import org.eclipse.osee.ote.ui.message.tree.AbstractTreeNode;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Ken J. Aguilar
 */
public abstract class DetailsProvider extends Composite {

   public DetailsProvider(Composite parent, int style) {
      super(parent, style);
   }

   public abstract void render(AbstractTreeNode node);

   public abstract String getTabText();

   public abstract String getTabToolTipText();
}
