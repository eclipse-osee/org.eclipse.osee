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

import org.eclipse.osee.ote.core.environment.command.CommandDescription;

public class SequentialCommandBegan implements IServiceStatusDataCommand {

   private static final long serialVersionUID = -3278399375292593249L;
   private CommandDescription description;

   public SequentialCommandBegan(CommandDescription description) {
      this.description = description;
   }

   public SequentialCommandBegan() {
   }

   public void set(CommandDescription description) {
      this.description = description;
   }

   public CommandDescription getDescription() {
      return description;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ote.core.environment.status.IServiceStatusData#accept(org.eclipse.osee.ote.core.environment.status.IServiceStatusDataVisitor)
    */
   public void accept(IServiceStatusDataVisitor visitor) {
      if (visitor != null) {
         visitor.asSequentialCommandBegan(this);
      }
   }
}
