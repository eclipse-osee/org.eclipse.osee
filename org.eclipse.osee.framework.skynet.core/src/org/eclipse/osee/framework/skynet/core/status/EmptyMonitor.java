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
package org.eclipse.osee.framework.skynet.core.status;

/**
 * @author Theron Virgin
 */
public class EmptyMonitor implements IStatusMonitor {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.status.IStatusMonitor#done()
    */
   @Override
   public void done() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.status.IStatusMonitor#startJob(java.lang.String, int)
    */
   @Override
   public void startJob(String name, int totalWork) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.status.IStatusMonitor#updateWork(int)
    */
   @Override
   public void updateWork(int workCompleted) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.status.IStatusMonitor#setSubtaskName()
    */
   @Override
   public void setSubtaskName(String name) {
   }

}
