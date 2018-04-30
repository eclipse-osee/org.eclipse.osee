/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.ide.traceability;

/**
 * @author John R. Misinco
 */
public class ResourceIdentifier {

   private final String name;
   private final String guid;

   public ResourceIdentifier(String name, String guid) {
      this.name = name;
      this.guid = guid;
   }

   public String getName() {
      return name;
   }

   public String getGuid() {
      return guid;
   }

}
