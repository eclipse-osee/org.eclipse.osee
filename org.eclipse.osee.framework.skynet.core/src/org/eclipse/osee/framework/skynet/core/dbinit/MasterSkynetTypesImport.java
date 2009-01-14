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

package org.eclipse.osee.framework.skynet.core.dbinit;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.importing.DbOseeDataTypeProcessor;
import org.eclipse.osee.framework.skynet.core.importing.ExcelOseeTypeDataParser;
import org.osgi.framework.Bundle;
import org.xml.sax.SAXException;

/**
 * This class provides necessary functionality for branches to be loaded with SkynetDbTypes through their extension
 * points. Creation, adding skynet types and initializing a new branch should be done through
 * BranchManager.createRootBranch.
 * 
 * @author Andrew M. Finkbeiner
 * @author Donald G. Dunne
 * @see BranchManager#createRootBranch(String, String, String, Collection, boolean)
 */
public class MasterSkynetTypesImport {
   private static final String skynetDbTypesExtensionPointId = "org.eclipse.osee.framework.skynet.core.OseeTypes";

   private MasterSkynetTypesImport() {
   }

   /**
    * Imports the given skynetDbTypes to the given branch. This should not be called seprately, but instead should be
    * handled through BranchManager.createRootBranch
    * 
    * @param connection
    * @param skynetTypesImportExtensionsUniqueIds
    * @param branch
    * @throws Exception
    * @see BranchManager#createRootBranch(String, String, String, Collection, boolean)
    */
   public static void importSkynetDbTypes(Collection<String> skynetTypesImportExtensionsUniqueIds, Branch branch) throws OseeCoreException {
      try {
         runSkynetDbTypesImport(ExtensionPoints.getExtensionsByUniqueId(skynetDbTypesExtensionPointId,
               skynetTypesImportExtensionsUniqueIds), branch);
      } catch (IOException ex) {
         throw new OseeCoreException(ex);
      } catch (SAXException ex) {
         throw new OseeCoreException(ex);
      }
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
   private static void runSkynetDbTypesImport(List<IExtension> skynetDbTypesExtensions, Branch branch) throws IOException, SAXException, OseeCoreException {
      ExcelOseeTypeDataParser importer = new ExcelOseeTypeDataParser(new DbOseeDataTypeProcessor());
      OseeLog.log(SkynetActivator.class, Level.INFO, "Importing into [" + branch.getBranchName() + "]");
      for (IExtension extension : skynetDbTypesExtensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("ExcelFile")) {
               String file = el.getAttribute("file");
               Bundle bundle = Platform.getBundle(el.getContributor().getName());
               URL url = bundle.getEntry(file);
               OseeLog.log(SkynetActivator.class, Level.INFO, "Importing [" + url.getPath() + "]");
               importer.extractTypesFromSheet(url.openStream());
            }
         }
      }
      importer.finish();
      OseeLog.log(SkynetActivator.class, Level.INFO, "Completed import into [" + branch.getBranchName() + "]");
   }
}
