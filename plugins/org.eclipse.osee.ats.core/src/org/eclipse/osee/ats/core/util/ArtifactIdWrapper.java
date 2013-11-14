/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.util;

import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.orcs.data.ArtifactId;

/**
 * @author Donald G Dunne
 */
public class ArtifactIdWrapper implements ArtifactId {

   private final IAtsObject atsObject;

   public ArtifactIdWrapper(IAtsObject atsObject) {
      this.atsObject = atsObject;
   }

   @Override
   public String getGuid() {
      return atsObject.getGuid();
   }

   @Override
   public boolean matches(Identity<?>... identities) {
      return atsObject.matches(identities);
   }

   @Override
   public String getName() {
      return atsObject.getName();
   }

}
