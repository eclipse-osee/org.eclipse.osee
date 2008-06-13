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

package org.eclipse.osee.framework.skynet.core.exception;


/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
public class UserInDatabaseMultipleTimes extends OseeCoreException {
   private static final long serialVersionUID = 1L;

   /**
    * @param message
    */
   public UserInDatabaseMultipleTimes(String message) {
      super(message);
   }

   /**
    * @param message
    * @param cause
    */
   public UserInDatabaseMultipleTimes(String message, Throwable cause) {
      super(message, cause);
   }
}