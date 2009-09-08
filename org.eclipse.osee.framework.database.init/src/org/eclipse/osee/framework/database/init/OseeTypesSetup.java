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

package org.eclipse.osee.framework.database.init;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.database.init.internal.DatabaseInitActivator;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.importing.IOseeTypesHandler;
import org.osgi.framework.Bundle;

/**
 * This class provides necessary functionality for branches to be loaded with SkynetDbTypes through their extension
 * points. Creation, adding skynet types and initializing a new branch should be done through
 * BranchManager.createRootBranch.
 * 
 * @author Andrew M. Finkbeiner
 * @author Donald G. Dunne
 * @see BranchManager#createTopLevelBranch(String, String, String, Collection, boolean)
 */
public class OseeTypesSetup {
   private static final String DECLARING_PLUGIN_ID = "org.eclipse.osee.framework.skynet.core";
   private static final String OSEE_TYPES_ELEMENT = "OseeTypes";
   private static final String OSEE_TYPES_EXTENSION_ID = DECLARING_PLUGIN_ID + "." + OSEE_TYPES_ELEMENT;

   private static final String OSEE_TYPES_HANDLER = "OseeTypesHandler";
   private static final String OSEE_TYPES_HANDLER_EXTENSION_ID = DECLARING_PLUGIN_ID + "." + OSEE_TYPES_HANDLER;

   private final ExtensionDefinedObjects<IOseeTypesHandler> extensionObjects;

   public OseeTypesSetup() {
      extensionObjects =
            new ExtensionDefinedObjects<IOseeTypesHandler>(OSEE_TYPES_HANDLER_EXTENSION_ID, OSEE_TYPES_HANDLER,
                  "classname");
   }

   public void execute(Collection<String> uniqueIdsToImport) throws OseeCoreException {
      File combinedFile = null;
      try {
         Map<String, URL> itemsToProcess = getOseeTypeExtensionsById(uniqueIdsToImport);
         combinedFile = createCombinedFile(itemsToProcess);
         processOseeTypeData(combinedFile.toURI().toURL());
      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      } finally {
         combinedFile.delete();
      }
   }

   public Map<String, URL> getOseeTypeExtensions() {
      Map<String, URL> oseeTypes = new HashMap<String, URL>();
      for (IConfigurationElement element : ExtensionPoints.getExtensionElements(OSEE_TYPES_EXTENSION_ID, "OseeTypes")) {
         String resourceName = element.getAttribute("resource");
         Bundle bundle = Platform.getBundle(element.getContributor().getName());
         URL url = bundle.getEntry(resourceName);
         oseeTypes.put(element.getDeclaringExtension().getUniqueIdentifier(), url);
      }
      return oseeTypes;
   }

   public File createCombinedFile(Map<String, URL> urls) throws IOException {
      String userHome = System.getProperty("user.home");
      File file = new File(userHome, "osee.types." + Lib.getDateTimeString() + ".osee");
      Writer writer = null;
      try {
         writer = new FileWriter(file);
         for (Entry<String, URL> entry : urls.entrySet()) {
            URL url = entry.getValue();
            String oseeTypeFragment = Lib.inputStreamToString(url.openStream());
            oseeTypeFragment = oseeTypeFragment.replaceAll("import\\s+\"", "// import \"");
            writer.write("\n");
            writer.write("//////////////     ");
            writer.write(entry.getKey());
            writer.write("\n");
            writer.write("\n");
            writer.write(oseeTypeFragment);
         }
      } finally {
         if (writer != null) {
            writer.close();
         }
      }
      return file;
   }

   private Map<String, URL> getOseeTypeExtensionsById(Collection<String> uniqueIdsToImport) {
      Map<String, URL> items = new LinkedHashMap<String, URL>();
      Map<String, URL> extensions = getOseeTypeExtensions();
      for (String idsToImport : uniqueIdsToImport) {
         URL urlEntry = extensions.get(idsToImport);
         if (urlEntry == null) {
            OseeLog.log(DatabaseInitActivator.class, Level.SEVERE, String.format(
                  "ExtensionUniqueId [%s] was not found", idsToImport));
         } else {
            items.put(idsToImport, urlEntry);
         }
      }
      OseeLog.log(DatabaseInitActivator.class, Level.INFO, String.format("Importing:\n\t%s",
            items.keySet().toString().replaceAll(",", ",\n\t")));
      return items;
   }

   public void processOseeTypeData(URL url) throws OseeCoreException {
      IOseeTypesHandler handler = getHandler(url);
      if (handler != null) {
         handler.execute(new NullProgressMonitor(), null, url);
      } else {
         OseeLog.log(DatabaseInitActivator.class, Level.SEVERE, String.format(
               "Unable to find handler for [%s] - handlers - %s", url.toExternalForm(),
               this.extensionObjects.getObjects()));
      }
   }

   private IOseeTypesHandler getHandler(URL url) {
      IOseeTypesHandler toReturn = null;
      String urlString = url.toExternalForm();
      for (IOseeTypesHandler handler : extensionObjects.getObjects()) {
         if (handler.isApplicable(urlString)) {
            toReturn = handler;
            break;
         }
      }
      return toReturn;
   }

}
