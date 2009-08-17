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
package org.eclipse.osee.framework.skynet.core.importing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactImportContributionManager {

   private final ExtensionDefinedObjects<ArtifactExtractor> definedObjects;
   private List<IWordOutlineContentHandler> contentHandlers;

   public ArtifactImportContributionManager() {
      definedObjects =
            new ExtensionDefinedObjects<ArtifactExtractor>("org.eclipse.osee.framework.ui.skynet.ArtifactExtractor",
                  "ArtifactExtractor", "class");
   }

   public List<ArtifactExtractor> getArtifactSourceParser() {
      return definedObjects.getObjects();
   }

   public List<IWordOutlineContentHandler> getHandler(String value) {
      List<IWordOutlineContentHandler> handlers = null;
      if ("Word Outline".equals(value)) {
         checkHandlersInitialized();
         handlers = contentHandlers;
      } else {
         handlers = Collections.emptyList();
      }
      return handlers;
   }

   private synchronized void checkHandlersInitialized() {
      if (contentHandlers == null) {
         contentHandlers = new ArrayList<IWordOutlineContentHandler>();
         IExtensionPoint point =
               Platform.getExtensionRegistry().getExtensionPoint(
                     "org.eclipse.osee.framework.ui.skynet.WordOutlineContentHandler");
         IExtension[] extensions = point.getExtensions();
         for (IExtension extension : extensions) {
            IConfigurationElement[] elements = extension.getConfigurationElements();
            for (IConfigurationElement element : elements) {
               if (element.getName().equals("Handler")) {
                  try {
                     contentHandlers.add((IWordOutlineContentHandler) element.createExecutableExtension("class"));
                  } catch (Exception ex) {
                     OseeLog.log(Activator.class, Level.SEVERE, ex);
                  }
               }
            }
         }
         Collections.sort(contentHandlers, new OutlineHandlerComparator());
      }
   }

   private static final class OutlineHandlerComparator implements Comparator<IWordOutlineContentHandler> {

      public int compare(IWordOutlineContentHandler o1, IWordOutlineContentHandler o2) {
         return o1.getName().compareToIgnoreCase(o2.getName());
      }
   };
}
