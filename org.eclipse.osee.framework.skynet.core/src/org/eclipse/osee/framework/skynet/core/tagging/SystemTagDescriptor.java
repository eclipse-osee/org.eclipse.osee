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
 * All of the system level tag types.
 * 
 * @author Robert A. Fisher
 */
public enum SystemTagDescriptor {
   AUTO_INDEXED("System Index");

   private static final TagManager tagManager = TagManager.getInstance();

   private final String name;
   private TagDescriptor descriptor;

   private SystemTagDescriptor(String name) {
      this.name = name;
      this.descriptor = null;
   }

   /**
    * @return Returns the descriptor.
    */
   public TagDescriptor getDescriptor() {
      if (descriptor == null) {
         // Look for the descriptor already in the system
         for (TagDescriptor tagDescriptor : tagManager.getTagDescriptors()) {
            if (tagDescriptor.getName().equals(name)) {
               descriptor = tagDescriptor;
               break;
            }
         }

         // If the descriptor was not found, then make it
         if (descriptor == null) {
            descriptor = tagManager.makeTagType(name);
         }
      }
      return descriptor;
   }

   /**
    * @return Returns the name.
    */
   public String getName() {
      return name;
   }
}
