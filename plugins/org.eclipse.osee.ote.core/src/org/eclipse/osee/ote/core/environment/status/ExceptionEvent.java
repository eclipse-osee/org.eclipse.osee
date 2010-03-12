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



/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class ExceptionEvent extends CommandStatusEvent implements Serializable{

   /**
    * 
    */
   private static final long serialVersionUID = -831107014787093973L;
   private Throwable throwable;
   /**
    * @param description
    * @param exception
    */
   public ExceptionEvent(CommandDescription description, Throwable exception) {
      super(description);
      this.throwable = exception;
   }

   public Throwable getException() {
      return this.throwable;
   }
}