/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.services;

import org.eclipse.osee.framework.core.data.AccessContextId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicArtifact;

/**
 * @author Roberto E. Escobar
 */
public interface ConfigurationManagement {

   boolean isApplicable(IBasicArtifact<?> userArtifact, Object object);

   AccessContextId getContextId(IBasicArtifact<?> userArtifact, Object itemToCheck) throws OseeCoreException;

}
