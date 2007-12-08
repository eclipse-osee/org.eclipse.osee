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
package org.eclipse.osee.framework.database.core;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseNotSupportedException extends Exception {

   private static final long serialVersionUID = 155062269031207620L;

   public DatabaseNotSupportedException() {
      super("Database is not supported.");
   }

   public DatabaseNotSupportedException(String message) {
      super("Database is not supported. \n" + message);
   }
}
