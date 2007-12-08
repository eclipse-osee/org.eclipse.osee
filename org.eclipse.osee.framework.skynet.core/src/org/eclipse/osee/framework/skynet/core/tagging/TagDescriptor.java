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
package org.eclipse.osee.framework.skynet.core.tagging;

/**
 * Describes a type of tag.
 * 
 * @author Robert A. Fisher
 */
public class TagDescriptor {
   private final String name;
   private final int tagTypeId;

   /**
    * @param name
    * @param tagTypeId
    */
   protected TagDescriptor(String name, int tagTypeId) {
      this.name = name;
      this.tagTypeId = tagTypeId;
   }

   /**
    * @return Returns the name.
    */
   public String getName() {
      return name;
   }

   /**
    * @return Returns the tagTypeId.
    */
   public int getTagTypeId() {
      return tagTypeId;
   }
}
