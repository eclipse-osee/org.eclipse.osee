/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.config;

import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IArtifactToken;

/**
 * @author Donald G. Dunne
 */
public interface IAtsCache {

   public <T extends IAtsObject> T getAtsObject(Long uuid);

   public <T extends IAtsObject> T getAtsObjectByGuid(String guid);

   public <T extends IAtsObject> T getAtsObjectByTag(String tag);

   public <T extends IAtsObject> T getAtsObjectByTag(String tag, Class<T> clazz);

   public void cacheAtsObjectByTag(String tag, IAtsObject atsObject);

   public <T extends ArtifactId> T getArtifact(Long uuid);

   public <T extends ArtifactId> T getArtifactByGuid(String guid);

   public <T extends ArtifactId> T getArtifactByTag(String tag);

   public void cacheArtifactByTag(String tag, ArtifactId artifact);

   public <T extends IAtsObject> T getByUuid(Long uuid, Class<T> clazz);

   public void cacheAtsObject(IAtsObject atsObject);

   public void cacheArtifact(ArtifactId artifact);

   public void invalidate();

   public void deCacheAtsObject(IAtsObject atsObject);

   public ArtifactId getArtifact(IAtsObject atsObject);

   public <T extends IAtsObject> T getAtsObjectByToken(IArtifactToken token, Class<T> clazz);

}
