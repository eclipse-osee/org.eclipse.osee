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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.osgi.framework.Bundle;

/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
public class ArtifactFactoryManager {
   private static List<ArtifactFactory> factories;

   public static ArtifactFactory getFactory(ArtifactType artifactType) throws OseeCoreException {
      return getFactory(artifactType.getName());
   }

   public static ArtifactFactory getFactory(String artifactTypeName) throws OseeCoreException {
      loadFactoryBundleMap();
      ArtifactFactory responsibleFactory = null;
      for (ArtifactFactory factory : factories) {
         if (factory.isResponsibleFor(artifactTypeName)) {
            if (responsibleFactory == null) {
               responsibleFactory = factory;
            } else {
               OseeLog.log(
                     Activator.class,
                     Level.SEVERE,
                     "Multiple ArtifactFactories [" + responsibleFactory + "][" + factory + "]responsible for same artifact type [" + artifactTypeName + "].  Defaulting to DefaultArtifactFactory.");
               return new DefaultArtifactFactory();
            }
         }
      }
      if (responsibleFactory != null) {
         return responsibleFactory;
      }
      return new DefaultArtifactFactory();
   }

   private synchronized static void loadFactoryBundleMap() {
      if (factories == null) {
         factories = new ArrayList<ArtifactFactory>();
         List<IConfigurationElement> elements =
               ExtensionPoints.getExtensionElements("org.eclipse.osee.framework.skynet.core.ArtifactFactory",
                     "ArtifactFactory");

         for (IConfigurationElement element : elements) {
            String factoryClassName = element.getAttribute("classname");
            try {
               String bundleSymbolicName = element.getContributor().getName();
               if (bundleSymbolicName == null) {
                  OseeLog.log(Activator.class, Level.WARNING,
                        "No bundle associated with the factory class: " + factoryClassName);
                  return;
               }

               Bundle bundle = Platform.getBundle(bundleSymbolicName);
               ArtifactFactory factory = (ArtifactFactory) bundle.loadClass(factoryClassName).newInstance();
               factories.add(factory);
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, "Unable to create factory: " + factoryClassName, ex);
            }
         }
      }
   }
}