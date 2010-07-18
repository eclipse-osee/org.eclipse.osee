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
package org.eclipse.osee.ote.message.test;

import org.eclipse.osee.ote.message.enums.DataType;

/**
 * @author b1528444
 *
 */
public enum TestMemType implements DataType {
   ETHERNET, SERIAL;

   
   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.enums.DataType#getToolingBufferSize()
    */
   @Override
   public int getToolingBufferSize() {
      return 0;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.enums.DataType#getToolingDepth()
    */
   @Override
   public int getToolingDepth() {
      return 0;
   }

}
