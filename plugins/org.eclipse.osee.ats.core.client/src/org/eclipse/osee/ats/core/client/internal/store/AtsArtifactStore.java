/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.internal.store;

import java.util.Map;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.config.IAtsCache;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.client.internal.IAtsArtifactReader;
import org.eclipse.osee.ats.core.client.internal.IAtsArtifactStore;
import org.eclipse.osee.ats.core.client.internal.IAtsArtifactWriter;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class AtsArtifactStore implements IAtsArtifactStore {

   private final Map<IArtifactType, IAtsArtifactReader<? extends IAtsConfigObject>> readers;
   private final Map<Class<? extends IAtsConfigObject>, IAtsArtifactWriter<? extends IAtsConfigObject>> writers;

   public AtsArtifactStore(Map<IArtifactType, IAtsArtifactReader<? extends IAtsConfigObject>> readers, Map<Class<? extends IAtsConfigObject>, IAtsArtifactWriter<? extends IAtsConfigObject>> writers) {
      this.readers = readers;
      this.writers = writers;
   }

   @Override
   public <T extends IAtsConfigObject> Artifact store(IAtsCache cache, T configObject, IAtsChangeSet changes) throws OseeCoreException {
      Conditions.checkNotNull(cache, "cache");
      Conditions.checkNotNull(configObject, "configObject");
      Conditions.checkNotNull(changes, "transaction");

      IAtsArtifactWriter<T> writer = getWriter(configObject.getClass());
      Conditions.checkNotNull(writer, "writer");

      Artifact art = writer.store(configObject, cache, changes);
      configObject.setStoreObject(art);
      return art;
   }

   @Override
   public <T extends IAtsConfigObject> T load(IAtsCache cache, Artifact artifact) throws OseeCoreException {
      Conditions.checkNotNull(cache, "cache");
      Conditions.checkNotNull(artifact, "artifact");

      IAtsArtifactReader<T> reader = getReader(artifact.getArtifactTypeToken());
      Conditions.checkNotNull(reader, "reader");

      return reader.load(cache, artifact);
   }

   @SuppressWarnings("unchecked")
   private <T extends IAtsConfigObject> IAtsArtifactWriter<T> getWriter(Class<? extends IAtsConfigObject> clazz) {
      for (Class<? extends IAtsConfigObject> writerClazz : writers.keySet()) {
         if (writerClazz.isAssignableFrom(clazz)) {
            return (IAtsArtifactWriter<T>) writers.get(writerClazz);
         }
      }
      return null;
   }

   @SuppressWarnings("unchecked")
   private <T extends IAtsConfigObject> IAtsArtifactReader<T> getReader(IArtifactType artifactType) {
      return (IAtsArtifactReader<T>) readers.get(artifactType);
   }

}
