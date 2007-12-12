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
package org.eclipse.osee.framework.skynet.core.tagging;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.osgi.framework.Bundle;

/**
 * @author Ryan D. Brooks
 */
public class TaggerManager {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(TaggerManager.class);
   private static final TaggerManager instance = new TaggerManager();
   private final ArrayList<Tagger> taggers;

   private TaggerManager() {
      taggers = new ArrayList<Tagger>(20);
      registerTaggersFromExtensionPoints();
      registerSystemTaggers();
   }

   private void registerSystemTaggers() {
      taggers.add(new BaseTagger());
   }

   private void registerTaggersFromExtensionPoints() {
      IExtensionPoint point =
            Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.framework.skynet.core.Tagger");
      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         String classname = null;
         String bundleName = null;
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("Tagger")) {
               classname = el.getAttribute("classname");
               bundleName = el.getContributor().getName();
            }
         }
         if (classname != null && bundleName != null) {
            Bundle bundle = Platform.getBundle(bundleName);
            try {
               Class<?> taggerClass = bundle.loadClass(classname);
               Object obj = taggerClass.newInstance();
               Tagger tagger = (Tagger) obj;
               taggers.add(tagger);
               logger.log(Level.INFO, "Added : " + tagger + " to the list of taggers from an extension point.");
            } catch (Exception ex) {
               logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            } catch (NoClassDefFoundError er) {
               logger.log(Level.WARNING,
                     "Failed to find a class definition for " + classname + ", registered from bundle " + bundleName,
                     er);
            }
         }
      }
   }

   public static TaggerManager getInstance() {
      return instance;
   }

   public Tagger getBestTagger(Artifact artifact) {
      for (Tagger tagger : taggers) {
         if (tagger.isValidFor(artifact)) {
            return tagger;
         }
      }
      throw new IllegalStateException("At least the default tagger should have been found.");
   }
}
