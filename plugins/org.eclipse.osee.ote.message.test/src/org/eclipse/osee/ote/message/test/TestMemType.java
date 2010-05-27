/*
 * Created on May 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
