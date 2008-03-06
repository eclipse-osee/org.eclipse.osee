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

package org.eclipse.osee.framework.skynet.core.conflict;

import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class RelationConflict {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.conflict.IConflict#getDestGamma()
    */
   public int getDestGamma() {
      return 0;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.conflict.IConflict#getImage()
    */
   public Image getImage() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.conflict.IConflict#getSourceGamma()
    */
   public int getSourceGamma() {
      return 0;
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   public Object getAdapter(Class adapter) {
      return null;
   }

}
