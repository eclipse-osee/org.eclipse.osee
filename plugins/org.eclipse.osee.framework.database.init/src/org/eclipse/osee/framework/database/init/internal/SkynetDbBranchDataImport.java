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

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeNotFoundException;
import org.eclipse.osee.framework.database.init.IDbInitializationTask;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.exportImport.HttpBranchExchange;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public class SkynetDbBranchDataImport implements IDbInitializationTask {
   private static final String ELEMENT_NAME = "OseeDbImportData";
   private static final String EXTENSION_POINT = DatabaseInitActivator.PLUGIN_ID + "." + ELEMENT_NAME;
   private static final String BRANCH_NAME = "branchName";
   private static final String BRANCH_DATA = "branchData";
   private static final String BRANCHES_TO_IMPORT = "BranchesToImport";

   @Override
   public void run() {
      if (OseeClientProperties.isOseeImportAllowed()) {
         // Clean up and delete all branches except Common
         for (BranchId branch : BranchManager.getBranches(BranchArchivedState.UNARCHIVED, BranchType.WORKING,
            BranchType.BASELINE)) {
            if (branch.notEqual(CoreBranches.COMMON)) {
               BranchManager.purgeBranch(branch);
            }
         }

         Collection<ImportData> importDatas = loadDataFromExtensions();
         for (ImportData importData : importDatas) {
            OseeLog.logf(DatabaseInitActivator.class, Level.INFO, "Import Branch Data: [%s]", importData);
            try {
               File importFile = importData.getExchangeFile();
               //TODO not yet supported               importData.getSelectedBranches();
               HttpBranchExchange.importBranches(importFile.toURI().toASCIIString(), true, true);
            } catch (OseeDataStoreException ex) {
               OseeLog.logf(DatabaseInitActivator.class, Level.SEVERE, ex, "Exception while importing branch: [%s]",
                  importData);
               throw ex;
            }
         }
      }
   }

   private Collection<ImportData> loadDataFromExtensions() {
      List<ImportData> toReturn = new ArrayList<>();
      Map<String, String> selectedBranches = new HashMap<>();
      List<IConfigurationElement> elements = ExtensionPoints.getExtensionElements(EXTENSION_POINT, ELEMENT_NAME);
      for (IConfigurationElement element : elements) {
         String bundleName = element.getContributor().getName();
         String branchData = element.getAttribute(BRANCH_DATA);

         if (Strings.isValid(bundleName) && Strings.isValid(branchData)) {
            File exchangeFile;
            try {
               exchangeFile = getExchangeFile(bundleName, branchData);
            } catch (Exception ex) {
               throw OseeCoreException.wrap(ex);
            }
            ImportData importData = new ImportData(exchangeFile);
            for (IConfigurationElement innerElement : element.getChildren(BRANCHES_TO_IMPORT)) {
               String branchName = innerElement.getAttribute(BRANCH_NAME);
               if (Strings.isValid(branchName)) {
                  importData.addSelectedBranch(branchName);
                  if (!selectedBranches.containsKey(branchName.toLowerCase())) {
                     selectedBranches.put(branchName.toLowerCase(),
                        element.getDeclaringExtension().getUniqueIdentifier());
                  } else {
                     throw new OseeDataStoreException(
                        "Branch import error - cannot import twice into a branch - [%s] was already specified by [%s] ",
                        branchName, selectedBranches.get(branchName.toLowerCase()));
                  }
               }
            }
            toReturn.add(importData);
         } else {
            throw new OseeDataStoreException(String.format("Branch import error: [%s] attributes were empty.",
               element.getDeclaringExtension().getExtensionPointUniqueIdentifier()));
         }
      }
      return toReturn;
   }

   private File getExchangeFile(String bundleName, String exchangeFile) {
      if (exchangeFile.endsWith("zip") != true) {
         throw new OseeArgumentException(String.format("Branch data file is invalid [%s] ", exchangeFile));
      }
      Bundle bundle = Platform.getBundle(bundleName);
      URL url = bundle.getResource(exchangeFile);
      try {
         url = FileLocator.toFileURL(url);
         String urlValue = url.toString();
         URI uri = new URI(urlValue.replaceAll(" ", "%20"));
         File toReturn = new File(uri);
         if (!toReturn.exists()) {
            throw new OseeNotFoundException(String.format("Branch data file cannot be found [%s]", exchangeFile));
         }
         return toReturn;
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   private static final class ImportData {
      private final File exchangeFile;
      private final Set<String> selectedBranches;

      public ImportData(File exchangeFile) {
         super();
         this.exchangeFile = exchangeFile;
         this.selectedBranches = new HashSet<>();
      }

      public void addSelectedBranch(String branchName) {
         this.selectedBranches.add(branchName);
      }

      @Override
      public String toString() {
         return String.format("%s - %s", exchangeFile, selectedBranches);
      }

      public File getExchangeFile() {
         return exchangeFile;
      }
   }
}