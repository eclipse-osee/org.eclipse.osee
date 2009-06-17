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
package org.eclipse.osee.ote.core.environment.command;

import java.io.Serializable;
import org.eclipse.osee.framework.jdk.core.util.GUID;

/**
 * @author Robert A. Fisher
 */
public class CommandDescription implements Serializable {
   /**
    * 
    */
   private static final long serialVersionUID = 538355585678229304L;
   protected final GUID guid;
   protected final String description;

   /**
    * CommandDescription Constructor. Sets the command's description.
    * 
    * @param description
    */
   public CommandDescription(String description) {
      super();
      guid = new GUID();
      this.description = description;
   }

   /**
    * @return Returns the description.
    */
   public String getDescription() {
      return description;
   }

   public boolean equals(Object obj) {

      if (obj instanceof GUID)
         return guid.equals(obj);
      else if (obj instanceof CommandDescription) return guid.equals(((CommandDescription) obj).guid);

      return false;
   }

   /**
    * @return Returns the guid.
    */
   public GUID getGuid() {
      return guid;
   }

   public String toString() {
      return "GUID: " + guid + "\n" + "Desc: " + description;
   }
}
