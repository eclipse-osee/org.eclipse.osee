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
package org.eclipse.osee.ats.artifact;

import org.eclipse.osee.framework.core.data.IAttributeType;

/**
 * @author Ryan D. Brooks
 */
public enum AtsAttributeTypes implements IAttributeType {
   LegacyPCRId("ats.Legacy PCR Id", "AAMFEd3TakphMtQX1zgA");

   private final String name;
   private final String guid;

   private AtsAttributeTypes(String name, String guid) {
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