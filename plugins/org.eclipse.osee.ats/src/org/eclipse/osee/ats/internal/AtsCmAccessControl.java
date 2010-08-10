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
package org.eclipse.osee.ats.internal;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.AccessContextId;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.access.AccessModel;
import org.eclipse.osee.framework.core.model.access.HasAccessModel;
import org.eclipse.osee.framework.core.services.CmAccessControl;

/**
 * @author Roberto E. Escobar
 */
public class AtsCmAccessControl implements CmAccessControl, HasAccessModel {

   private final AccessModel accessModel;

   public AtsCmAccessControl(AccessModel accessModel) {
      this.accessModel = accessModel;
   }

   @Override
   public boolean isApplicable(IBasicArtifact<?> user, Object object) {
      return false;
   }

   @Override
   public Collection<AccessContextId> getContextId(IBasicArtifact<?> user, Object object) {
      return null;
   }

   @Override
   public AccessModel getAccessModel() {
      return accessModel;
   }
}
