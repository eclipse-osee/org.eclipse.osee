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
import org.eclipse.osee.ats.core.client.internal.IAtsArtifactReader;
import org.eclipse.osee.ats.core.client.internal.IAtsArtifactStore;
import org.eclipse.osee.ats.core.client.internal.IAtsArtifactWriter;
import org.eclipse.osee.ats.core.client.internal.config.AtsArtifactConfigCache;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

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
   public <T extends IAtsConfigObject> Artifact store(AtsArtifactConfigCache cache, T configObject, SkynetTransaction transaction) throws OseeCoreException {
      Conditions.checkNotNull(cache, "cache");
      Conditions.checkNotNull(configObject, "configObject");
      Conditions.checkNotNull(transaction, "transaction");

      IAtsArtifactWriter<T> writer = getWriter(configObject.getClass());
      Conditions.checkNotNull(writer, "writer");

      return writer.store(configObject, cache, transaction);
   }

   @Override
   public <T extends IAtsConfigObject> T load(AtsArtifactConfigCache cache, Artifact artifact) throws OseeCoreException {
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
