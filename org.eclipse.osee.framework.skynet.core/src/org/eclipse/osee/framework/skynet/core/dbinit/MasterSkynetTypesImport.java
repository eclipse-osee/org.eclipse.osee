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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.importing.SkynetTypesImporter;
import org.osgi.framework.Bundle;
import org.xml.sax.SAXException;

/**
 * This class provides necessary functionality for branches to be loaded with SkynetDbTypes through their extension
 * points. Creation, adding skynet types and initializing a new branch should be done through
 * BranchPersistenceManager.createRootBranch.
 * 
 * @author Andrew M. Finkbeiner
 * @author Donald G. Dunne
 * @see BranchPersistenceManager#createRootBranch(String, String, String, Collection, boolean)
 */
public class MasterSkynetTypesImport {
   private static final String skynetDbTypesExtensionPointId = "org.eclipse.osee.framework.skynet.core.SkynetDbTypes";
   private Logger logger = ConfigUtil.getConfigFactory().getLogger(MasterSkynetTypesImport.class);
   private static MasterSkynetTypesImport instance;

   private MasterSkynetTypesImport() {
   }

   public static MasterSkynetTypesImport getInstance() {
      if (instance == null) instance = new MasterSkynetTypesImport();
      return instance;
   }

   private List<IExtension> getExtensionsById(Collection<String> skynetTypesImportExtensionsIds) {
      List<IExtension> skynetDbTypesExtensions = new ArrayList<IExtension>();
      for (String pointId : skynetTypesImportExtensionsIds) {
         IExtension extension = Platform.getExtensionRegistry().getExtension(pointId);
         if (extension == null) {
            logger.log(Level.SEVERE, "Unable to locate extension [" + pointId + "]");
         } else {
            String extsionPointId = extension.getExtensionPointUniqueIdentifier();
            if (skynetDbTypesExtensionPointId.equals(extsionPointId)) {
               skynetDbTypesExtensions.add(extension);
            } else {
               logger.log(Level.SEVERE,
                     "Unknown extension id [" + extsionPointId + "] from extension [" + pointId + "]");
            }
         }
      }
      return skynetDbTypesExtensions;
   }

   /**
    * Imports the given skynetDbTypes to the given branch. This should not be called seprately, but instead should be
    * handled through BranchPersistenceManager.createRootBranch
    * 
    * @param connection
    * @param skynetTypesImportExtensionsUniqueIds
    * @param branch
    * @throws Exception
    * @see BranchPersistenceManager#createRootBranch(String, String, String, Collection, boolean)
    */
   public void importSkynetDbTypes(Connection connection, Collection<String> skynetTypesImportExtensionsUniqueIds, Branch branch) throws Exception {
      runSkynetDbTypesImport(connection, ExtensionPoints.getExtensionsByUniqueId(skynetDbTypesExtensionPointId,
            skynetTypesImportExtensionsUniqueIds), branch);
   }

   /**
    * Import the skynetdbtypes for the given extensions
    * 
    * @param skynetDbTypesExtensions
    * @param branch
    * @param connection
    * @throws SQLException
    * @throws SAXException
    * @throws IOException
    * @throws CoreException
    */
   private void runSkynetDbTypesImport(Connection connection, List<IExtension> skynetDbTypesExtensions, Branch branch) throws SQLException, SAXException, IOException, CoreException {

      SkynetTypesImporter importer = new SkynetTypesImporter(branch);
      logger.log(Level.INFO, "Importing into [" + branch.getBranchName() + "]");
      for (IExtension extension : skynetDbTypesExtensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("ExcelFile")) {
               String file = el.getAttribute("file");
               Bundle bundle = Platform.getBundle(el.getContributor().getName());
               URL url = bundle.getEntry(file);
               logger.log(Level.INFO, "Importing [" + url.getPath() + "]");
               importer.extractTypesFromSheet(url.openStream());
            }
         }
      }
      importer.finish();
      logger.log(Level.INFO, "Completed import into [" + branch.getBranchName() + "]");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.database.initialize.tasks.IDbInitializationTask#canRun()
    */
   public boolean canRun() {
      return true;
   }
}
