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
package org.eclipse.osee.framework.db.connection.core.schema;

/**
 * @author Ryan D. Brooks
 */
public class View extends Table {
   private final String definition;

   /**
    * @param name
    * @param definition
    */
   public View(String name, String definition) {
      super(name);
      this.definition = definition;
   }

   public String getDefinition() {
      return definition;
   }
}
