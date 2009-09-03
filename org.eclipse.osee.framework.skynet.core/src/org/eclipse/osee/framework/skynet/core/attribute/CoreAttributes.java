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
package org.eclipse.osee.framework.skynet.core.attribute;

import org.eclipse.osee.framework.skynet.core.IOseeType;

/**
 * @author Roberto E. Escobar
 */
public enum CoreAttributes implements IOseeType {

   NATIVE_CONTENT("Native Content"),
   NATIVE_EXTENSION("Extension"),
   WHOLE_WORD_CONTENT("Whole Word Content"),
   WORD_TEMPLATE_CONTENT("Word Template Content");

   private final String name;

   private CoreAttributes(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }
}