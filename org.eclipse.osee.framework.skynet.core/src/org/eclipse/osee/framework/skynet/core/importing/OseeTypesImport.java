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
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
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
   private static final String skynetDbTypesExtensionPointId = "org.eclipse.osee.framework.skynet.core.OseeTypes";

   private OseeTypesImport() {
   }

   /**
    * Imports the given skynetDbTypes to the given branch. This should not be called seprately, but instead should be
    * handled through BranchManager.createRootBranch
    * 
    * @param connection
    * @param skynetTypesImportExtensionsUniqueIds
    * @param branch
    * @throws Exception
    * @see BranchManager#createTopLevelBranch(String, String, String, Collection, boolean)
    */
   public static void importSkynetDbTypes(Collection<String> skynetTypesImportExtensionsUniqueIds) throws OseeCoreException {
      try {
         runSkynetDbTypesImport(ExtensionPoints.getExtensionsByUniqueId(skynetDbTypesExtensionPointId,
               skynetTypesImportExtensionsUniqueIds));
      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      } catch (SAXException ex) {
         throw new OseeWrappedException(ex);
      }
      StringWriter writer = new StringWriter();
      ArtifactTypeManager.printInheritanceTree(writer);
      System.out.println(writer.toString());
   }

   /**
    * Import the skynetdbtypes for the given extensions
    * 
    * @param skynetDbTypesExtensions
    * @param branch
    * @throws SAXException
    * @throws IOException
    * @throws OseeCoreException
    */
   private static void runSkynetDbTypesImport(List<IExtension> skynetDbTypesExtensions) throws IOException, SAXException, OseeCoreException {
      ExcelOseeTypeDataParser importer = new ExcelOseeTypeDataParser(new DbOseeDataTypeProcessor());
      for (IExtension extension : skynetDbTypesExtensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("ExcelFile")) {
               String file = el.getAttribute("file");
               Bundle bundle = Platform.getBundle(el.getContributor().getName());
               URL url = bundle.getEntry(file);
               OseeLog.log(Activator.class, Level.INFO, String.format("Importing [%s] from [%s]", file,
                     url != null ? url.getPath() : "url was null"));
               if (url != null) {
                  InputStream inputStream = null;
                  try {
                     inputStream = url.openStream();
                     importer.extractTypesFromSheet(url.getPath(), inputStream);
                  } finally {
                     if (inputStream != null) {
                        inputStream.close();
                     }
                  }
               }
            }
         }
      }
      importer.finish();
   }
}
