/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.api.util;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Donald G. Dunne
 */
public interface IArtifactResolver {

   ArtifactId get(IAtsObject atsObject);

   <A extends ArtifactId> A get(IAtsWorkItem workItem, Class<?> clazz);

   <A extends ArtifactId> List<A> get(Collection<? extends IAtsWorkItem> workItems, Class<?> clazz);

   ArtifactTypeToken getArtifactType(IAtsWorkItem workItem);

}