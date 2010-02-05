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
import org.eclipse.osee.framework.core.data.NamedIdentity;

/**
 * @author Ryan D. Brooks
 */
public class AtsAttributeTypes extends NamedIdentity implements IAttributeType {
   public static final AtsAttributeTypes LegacyPCRId =
         new AtsAttributeTypes("AAMFEd3TakphMtQX1zgA", "ats.Legacy PCR Id");

   public static final AtsAttributeTypes Active = new AtsAttributeTypes("AAMFEclQOVmzkIvzyWwA", "ats.Active");

   private AtsAttributeTypes(String guid, String name) {
      super(guid, name);
   }
}