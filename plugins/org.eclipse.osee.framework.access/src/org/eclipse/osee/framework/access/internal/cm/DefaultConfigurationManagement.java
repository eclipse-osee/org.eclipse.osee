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
package org.eclipse.osee.framework.access.internal.cm;

import org.eclipse.osee.framework.core.data.AccessContextId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.services.ConfigurationManagement;

/**
 * @author Roberto E. Escobar
 */
public final class DefaultConfigurationManagement implements ConfigurationManagement {

   private static final AccessContextId DEFAULT_SYSTEM_CONTEXT = null;

   @Override
   public boolean isApplicable(IBasicArtifact<?> userArtifact, Object object) {
      return true;
   }

   @Override
   public AccessContextId getContextId(IBasicArtifact<?> userArtifact, Object itemToCheck) throws OseeCoreException {
      return DEFAULT_SYSTEM_CONTEXT;
   }
}