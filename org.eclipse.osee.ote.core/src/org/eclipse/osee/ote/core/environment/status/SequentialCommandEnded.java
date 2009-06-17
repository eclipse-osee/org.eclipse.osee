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

public class SequentialCommandEnded implements IServiceStatusDataCommand, Serializable {

   private static final long serialVersionUID = 9093713855323793915L;
   private CommandDescription description;
   private CommandEndedStatusEnum status;

   public SequentialCommandEnded(CommandDescription description, CommandEndedStatusEnum status) {
      this.description = description;
      this.status = status;
   }

   public SequentialCommandEnded() {
   }

   public CommandDescription getDescription() {
      return description;
   }

   public CommandEndedStatusEnum getStatus() {
      return status;
   }

   public void set(CommandDescription description, CommandEndedStatusEnum status) {
      this.description = description;
      this.status = status;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ote.core.environment.status.IServiceStatusData#accept(org.eclipse.osee.ote.core.environment.status.IServiceStatusDataVisitor)
    */
   public void accept(IServiceStatusDataVisitor visitor) {
      if (visitor != null) {
         visitor.asSequentialCommandEnded(this);
      }
   }
}
