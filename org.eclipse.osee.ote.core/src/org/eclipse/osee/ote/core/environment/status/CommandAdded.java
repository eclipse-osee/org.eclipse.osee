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
package org.eclipse.osee.ote.core.environment.status;

import java.io.Serializable;
import org.eclipse.osee.ote.core.environment.command.CommandDescription;

public class CommandAdded implements IServiceStatusDataCommand, Serializable {

   private static final long serialVersionUID = -2555474494093618398L;

   private CommandDescription description;

   public CommandAdded(CommandDescription description) {
      this.description = description;
   }

   public CommandAdded() {
   }

   public CommandDescription getDescription() {
      return description;
   }

   public void set(CommandDescription description) {
      this.description = description;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ote.core.environment.status.IServiceStatusData#accept(org.eclipse.osee.ote.core.environment.status.IServiceStatusDataVisitor)
    */
   public void accept(IServiceStatusDataVisitor visitor) {
      if (visitor != null) {
         visitor.asCommandAdded(this);
      }
   }
}
