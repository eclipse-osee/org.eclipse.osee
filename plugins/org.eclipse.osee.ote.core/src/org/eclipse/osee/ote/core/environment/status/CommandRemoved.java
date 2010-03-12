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

public class CommandRemoved implements Serializable, IServiceStatusDataCommand {

   private static final long serialVersionUID = -177791874608013281L;
   private CommandDescription description;
   private CommandEndedStatusEnum reason;

   public CommandRemoved(CommandDescription description, CommandEndedStatusEnum reason) {
      this.description = description;
      this.reason = reason;
   }

   public CommandRemoved() {
   }

   public CommandDescription getDescription() {
      return description;
   }

   public void setDescription(CommandDescription description) {
      this.description = description;
   }

   public void setReason(CommandEndedStatusEnum reason) {
      this.reason = reason;
   }

   public CommandEndedStatusEnum getReason() {
      return reason;
   }

   public void accept(IServiceStatusDataVisitor visitor) {
      if (visitor != null) {
         visitor.asCommandRemoved(this);
      }
   }
}
