/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.branch;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchViewImageHandler;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class BranchLabelProvider extends LabelProvider {
   @Override
   public Image getImage(Object element) {
      if (element instanceof BranchId) {
         return BranchViewImageHandler.getImage(element, 0);
      }
      return null;
   }
}
