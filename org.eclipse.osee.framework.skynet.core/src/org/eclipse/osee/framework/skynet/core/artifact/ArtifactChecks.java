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

package org.eclipse.osee.framework.skynet.core.artifact;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public class ArtifactChecks {

   public static List<IArtifactCheck> tasks;
   protected static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ArtifactChecks.class);
   public static String EXTENSION_POINT = "org.eclipse.osee.framework.skynet.core.ArtifactCheck";

   public static List<IArtifactCheck> getArtifactChecks() throws OseeCoreException, SQLException {
      if (tasks == null) {
         tasks = new ArrayList<IArtifactCheck>();
         List<IConfigurationElement> iExtensions =
               ExtensionPoints.getExtensionElements(EXTENSION_POINT, "ArtifactCheck");
         for (IConfigurationElement element : iExtensions) {
            String className = element.getAttribute("classname");
            String bundleName = element.getContributor().getName();
            try {
               if (className != null && bundleName != null) {
                  Bundle bundle = Platform.getBundle(bundleName);
                  Class<?> interfaceClass = bundle.loadClass(className);
                  IArtifactCheck check = (IArtifactCheck) interfaceClass.getConstructor().newInstance();
                  tasks.add(check);
               }
            } catch (Exception ex) {
               logger.log(Level.SEVERE, "Problem loading ArtifactCheck extension \"" + className + "\".  Ignorning.",
                     ex);
            }
         }
      }
      return tasks;
   }

}
