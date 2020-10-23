/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.dsl.integration;

import java.util.Collection;
import org.eclipse.osee.framework.core.access.AccessDetailCollector;
import org.eclipse.osee.framework.core.data.IAccessContextId;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext;

/**
 * @author Roberto E. Escobar
 */
public interface AccessModelInterpreter {

   AccessContext getContext(Collection<AccessContext> contexts, IAccessContextId contextId);

   void computeAccessDetails(AccessDetailCollector collector, AccessContext context, Object objectToCheck);

}
