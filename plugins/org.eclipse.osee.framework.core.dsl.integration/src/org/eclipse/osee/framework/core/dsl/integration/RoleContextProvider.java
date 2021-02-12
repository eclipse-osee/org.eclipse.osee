/*********************************************************************
 * Copyright (c) 2012 Boeing
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
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AccessContextToken;

/**
 * @author John R. Misinco
 */
public interface RoleContextProvider {

   public Collection<? extends AccessContextToken> getContextId(ArtifactToken user);

   Map<String, Long> getContextGuidToIdMap();

}
