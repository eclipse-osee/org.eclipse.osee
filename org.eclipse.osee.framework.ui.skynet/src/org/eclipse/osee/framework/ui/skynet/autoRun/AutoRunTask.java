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

package org.eclipse.osee.framework.ui.skynet.autoRun;

/**
 * @author Donald G. Dunne
 */
public abstract class AutoRunTask implements IAutoRunTask {

   public AutoRunTask() {
   }

   private String autoRunUniqueId;

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.util.IAutoRunTask#getName()
    */
   public String getAutoRunUniqueId() {
      if (autoRunUniqueId != null) return autoRunUniqueId;
      return "Un-named";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.util.IAutoRunTask#setName(java.lang.String)
    */
   public void setAutoRunUniqueId(String name) {
      this.autoRunUniqueId = name;
   }

}
