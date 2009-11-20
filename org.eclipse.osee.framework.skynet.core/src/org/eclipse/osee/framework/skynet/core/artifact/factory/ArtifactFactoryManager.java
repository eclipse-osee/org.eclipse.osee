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
package org.eclipse.osee.framework.skynet.core.artifact.factory;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
public final class ArtifactFactoryManager {
   private static final String ARTIFACT_FACTORY_EXTENSION = "ArtifactFactory";
   private static final String EXTENSION_ID = Activator.PLUGIN_ID + "." + ARTIFACT_FACTORY_EXTENSION;
   private static final String CLASSNAME_ATTRIBUTE = "classname";

   private static final ExtensionDefinedObjects<ArtifactFactory> extensionDefinedObjects =
         new ExtensionDefinedObjects<ArtifactFactory>(EXTENSION_ID, ARTIFACT_FACTORY_EXTENSION, CLASSNAME_ATTRIBUTE);

   private static final DefaultArtifactFactory defaultArtifactFactory = new DefaultArtifactFactory();
   private static Set<ArtifactType> eternalArtifactTypes = null;

   public ArtifactFactoryManager() {
   }

   public ArtifactFactory getFactory(String artifactTypeName) throws OseeCoreException {
      ArtifactFactory responsibleFactory = null;
      for (ArtifactFactory factory : getFactories()) {
         if (factory.isResponsibleFor(artifactTypeName)) {
            if (responsibleFactory == null) {
               responsibleFactory = factory;
            } else {
               OseeLog.log(
                     Activator.class,
                     Level.SEVERE,
                     "Multiple ArtifactFactories [" + responsibleFactory + "][" + factory + "]responsible for same artifact type [" + artifactTypeName + "].  Defaulting to DefaultArtifactFactory.");
               return getDefaultArtifactFactory();
            }
         }
      }
      if (responsibleFactory != null) {
         return responsibleFactory;
      }
      return getDefaultArtifactFactory();
   }

   public static Collection<ArtifactType> getEternalArtifactTypes() throws OseeCoreException {
      if (eternalArtifactTypes == null) {
         eternalArtifactTypes = new HashSet<ArtifactType>();
         for (ArtifactFactory factory : getFactories()) {
            eternalArtifactTypes.addAll(factory.getEternalArtifactTypes());
         }
      }
      return eternalArtifactTypes;
   }

   private ArtifactFactory getDefaultArtifactFactory() {
      return defaultArtifactFactory;
   }

   private static synchronized List<ArtifactFactory> getFactories() {
      return extensionDefinedObjects.getObjects();
   }
}