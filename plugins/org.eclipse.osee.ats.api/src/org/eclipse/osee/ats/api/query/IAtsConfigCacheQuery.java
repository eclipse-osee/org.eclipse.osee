/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.api.query;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Donald G. Dunne
 */
public interface IAtsConfigCacheQuery {

   IAtsConfigCacheQuery andWorkType(WorkType workType, WorkType... workTypes);

   <T extends IAtsConfigObject> Collection<T> get(Class<T> clazz);

   IAtsConfigCacheQuery isOfType(ArtifactTypeToken... artifactType);

   IAtsConfigCacheQuery andActive(boolean active);

}
