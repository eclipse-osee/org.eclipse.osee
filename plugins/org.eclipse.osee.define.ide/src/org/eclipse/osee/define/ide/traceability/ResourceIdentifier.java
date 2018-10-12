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

   public ResourceIdentifier(String name) {
      this.name = name;

   }

   public String getName() {
      return name;
   }

}
