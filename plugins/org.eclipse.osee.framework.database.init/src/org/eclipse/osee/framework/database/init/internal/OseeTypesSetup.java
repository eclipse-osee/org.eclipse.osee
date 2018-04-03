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

package org.eclipse.osee.framework.database.init.internal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.data.OrcsTypeSheet;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
      extensionObjects = new ExtensionDefinedObjects<IOseeTypesHandler>(OSEE_TYPES_HANDLER_EXTENSION_ID,
         OSEE_TYPES_HANDLER, "classname");
   }

   public void execute(Collection<String> uniqueIdsToImport) {
      File combinedFile = null;
      try {
         List<OrcsTypeSheet> itemsToProcess = getOseeTypeExtensionsById(uniqueIdsToImport);
         combinedFile = createCombinedFile(itemsToProcess);
         processOseeTypeData(combinedFile.toURI());
         // Only delete file if no problems processing
         combinedFile.delete();

      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   public List<OrcsTypeSheet> getOseeTypeExtensions() {
      List<OrcsTypeSheet> oseeTypes = new LinkedList<>();
      for (IConfigurationElement element : ExtensionPoints.getExtensionElements(OSEE_TYPES_EXTENSION_ID, "OseeTypes")) {
         OrcsTypeSheet sheet = new OrcsTypeSheet();
         String resourceName = element.getAttribute("resource");
         Bundle bundle = Platform.getBundle(element.getContributor().getName());
         URL url = bundle.getEntry(resourceName);
         sheet.setName(element.getDeclaringExtension().getUniqueIdentifier());
         sheet.setGuid(element.getAttribute("guid"));
         sheet.setId(element.getAttribute("id"));
         try {
            sheet.setTypesSheet(Lib.inputStreamToString(new BufferedInputStream(url.openStream())));
            oseeTypes.add(sheet);
         } catch (IOException ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
      }
      return oseeTypes;
   }

   public File createCombinedFile(List<OrcsTypeSheet> sheets) throws IOException {
      String userHome = System.getProperty("user.home");
      File file = new File(userHome, "osee.types." + Lib.getDateTimeString() + ".osee");
      Writer writer = null;
      try {
         writer = new FileWriter(file);
         for (OrcsTypeSheet sheet : sheets) {
            String oseeTypeFragment = sheet.getTypesSheet();
            oseeTypeFragment = oseeTypeFragment.replaceAll("import\\s+\"", "// import \"");
            writer.write("\n");
            writer.write("//////////////     ");
            writer.write(sheet.getName());
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

   private List<OrcsTypeSheet> getOseeTypeExtensionsById(Collection<String> uniqueIdsToImport) {
      List<OrcsTypeSheet> items = new LinkedList<>();
      for (OrcsTypeSheet sheet : getOseeTypeExtensions()) {
         if (uniqueIdsToImport.contains(sheet.getName())) {
            items.add(sheet);
         }
      }
      OseeLog.logf(Activator.class, Level.INFO, "Importing:\n\t%s",
         items.toString().replaceAll(",", ",\n\t"));
      return items;
   }

   public void processOseeTypeData(URI uri) {
      IOseeTypesHandler handler = getHandler(uri);
      if (handler != null) {
         handler.execute(new NullProgressMonitor(), uri);
      } else {
         OseeLog.logf(Activator.class, Level.SEVERE, "Unable to find handler for [%s] - handlers - %s",
            uri.toASCIIString(), this.extensionObjects.getObjects());
      }
   }

   private IOseeTypesHandler getHandler(URI uri) {
      IOseeTypesHandler toReturn = null;
      String urlString = uri.toASCIIString();
      for (IOseeTypesHandler handler : extensionObjects.getObjects()) {
         if (handler.isApplicable(urlString)) {
            toReturn = handler;
            break;
         }
      }
      return toReturn;
   }

}
