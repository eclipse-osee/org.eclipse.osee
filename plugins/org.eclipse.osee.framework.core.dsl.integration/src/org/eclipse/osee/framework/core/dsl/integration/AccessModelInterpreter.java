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
package org.eclipse.osee.framework.core.dsl.integration;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.AccessContextId;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.access.AccessDetail;

/**
 * @author Roberto E. Escobar
 */
public interface AccessModelInterpreter {

   public static interface AccessDetailCollector {
      void collect(AccessDetail<?> accessDetail) throws OseeCoreException;
   }

   AccessContext getContext(Collection<AccessContext> contexts, AccessContextId contextId) throws OseeCoreException;

   void computeAccessDetails(AccessDetailCollector collector, AccessContext context, Object objectToCheck) throws OseeCoreException;

}
