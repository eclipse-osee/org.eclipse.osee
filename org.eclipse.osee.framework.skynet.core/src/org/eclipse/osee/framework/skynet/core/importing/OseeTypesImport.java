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

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.osgi.framework.Bundle;
import org.xml.sax.SAXException;

/**
 * This class provides necessary functionality for branches to be loaded with SkynetDbTypes through their extension
 * points. Creation, adding skynet types and initializing a new branch should be done through
 * BranchManager.createRootBranch.
 * 
 * @author Andrew M. Finkbeiner
 * @author Donald G. Dunne
 * @see BranchManager#createTopLevelBranch(String, String, String, Collection, boolean)
 */
public class OseeTypesImport {
   private static final String OSEE_TYPES_ELEMENT = "OseeTypes";
   private static final String OSEE_TYPES_EXTENSION_ID = Activator.PLUGIN_ID + "." + OSEE_TYPES_ELEMENT;

   private static final String OSEE_TYPES_HANDLER = "OseeTypesHandler";
   private static final String OSEE_TYPES_HANDLER_EXTENSION_ID = Activator.PLUGIN_ID + "." + OSEE_TYPES_HANDLER;

   private static final OseeTypesImport instance = new OseeTypesImport();

   private final ExtensionDefinedObjects<IOseeTypesHandler> extensionObjects;

   private OseeTypesImport() {
      extensionObjects =
            new ExtensionDefinedObjects<IOseeTypesHandler>(OSEE_TYPES_HANDLER_EXTENSION_ID, OSEE_TYPES_HANDLER,
                  "classname");
   }

   public static void execute(Collection<String> uniqueIdsToImport) throws OseeCoreException {
      try {
         instance.executeTypesImport(ExtensionPoints.getExtensionsByUniqueId(OSEE_TYPES_EXTENSION_ID, uniqueIdsToImport));
      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      } catch (SAXException ex) {
         throw new OseeWrappedException(ex);
      }
   }

   private void executeTypesImport(List<IExtension> extensionIds) throws IOException, SAXException, OseeCoreException {
      //      ExcelOseeTypeDataParser importer = new ExcelOseeTypeDataParser(new DbOseeDataTypeProcessor(), false);
      for (IExtension extension : extensionIds) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("OseeTypes")) {
               String resource = el.getAttribute("resource");
               URL url = getResource(el.getContributor().getName(), resource);
               OseeLog.log(Activator.class, Level.INFO, String.format("Importing [%s] from [%s]", resource,
                     url != null ? url.getPath() : "url was null"));
               IOseeTypesHandler handler = getHandler(resource, url);
               if (handler != null) {
                  handler.execute(new NullProgressMonitor(), url);
               } else {
                  OseeLog.log(Activator.class, Level.SEVERE,
                        String.format("Unable to find handler for [%s] - handlers - %s", resource,
                              this.extensionObjects.getObjects()));
               }
            }
            //            else if (el.getName().equals("ExcelFile")) {
            //               String resource = el.getAttribute("file");
            //               URL url = getResource(el.getContributor().getName(), resource);
            //               OseeLog.log(Activator.class, Level.INFO, String.format("Importing [%s] from [%s]", resource,
            //                     url != null ? url.getPath() : "url was null"));
            //               if (url != null) {
            //                  InputStream inputStream = null;
            //                  try {
            //                     inputStream = url.openStream();
            //                     //                     importer.extractTypesFromSheet(url.getPath(), inputStream);
            //                  } finally {
            //                     if (inputStream != null) {
            //                        inputStream.close();
            //                     }
            //                  }
            //               }
            //            }
         }
      }
      //      importer.finish();
   }

   private IOseeTypesHandler getHandler(String resource, URL url) {
      IOseeTypesHandler toReturn = null;
      for (IOseeTypesHandler handler : extensionObjects.getObjects()) {
         if (handler.isApplicable(resource, url)) {
            toReturn = handler;
            break;
         }
      }
      return toReturn;
   }

   private URL getResource(String bundleName, String resource) {
      Bundle bundle = Platform.getBundle(bundleName);
      URL url = bundle.getEntry(resource);
      return url;
   }
}
