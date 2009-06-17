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
package org.eclipse.osee.ote.messaging.dds.condition;

import java.util.Collection;
import java.util.concurrent.locks.Condition;

import org.eclipse.osee.ote.messaging.dds.Duration;
import org.eclipse.osee.ote.messaging.dds.NotImplementedException;
import org.eclipse.osee.ote.messaging.dds.ReturnCode;


/**
 * This class is here for future functionality that is described in the DDS specification
 * but has not been implemented or used.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class WaitSet {
   public WaitSet() {

      // This class, and the use of it has not been implemented
      throw new NotImplementedException();
   }

   /**
    * Not Implemented. Attach a condition to this <code>WaitSet</code>.
    * 
    * @param condition The condition to attach
    * @return ERROR - This method is not implemented
    */
   public ReturnCode attachCondition(Condition condition) {
      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();
      return ReturnCode.ERROR;

   }

   /**
    * Not Implemented. Detach a condition from this <code>WaitSet</code>.
    * 
    * @param condition The condition to detach
    * @return ERROR - This method is not implemented
    */
   public ReturnCode detachCondition(Condition condition) {
      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();
      return ReturnCode.ERROR;

   }

   /**
    * Not Implemented. Block execution until a <code>Condition</code> in this <code>WaitSet</code>
    * is triggered. The provided collection will be used to return a list of the triggered conditions
    * when then call unblocks. The timeout parameter is used to specify a maximum amount of time
    * that should be consumed by this call, allowing the block to release with out any of the 
    * conditions being met.
    * 
    * @param conditions A collection to place the triggered conditions in.
    * @param timeout A max amount of time to wait on this <code>WaitSet</code>
    * @return ERROR - This method is not implemented
    */
   public ReturnCode wait(Collection<?> conditions, Duration timeout) {
      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();
      return ReturnCode.ERROR;

   }
}
