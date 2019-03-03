/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
