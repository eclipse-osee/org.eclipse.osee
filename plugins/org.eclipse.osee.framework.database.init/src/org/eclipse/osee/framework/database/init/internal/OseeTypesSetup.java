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
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.data.OrcsTypeSheet;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.orcs.rest.client.OseeClient;
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

   public void execute(Collection<String> uniqueIdsToImport) {
      List<OrcsTypeSheet> sheets = getOseeTypeExtensionsById(uniqueIdsToImport);

      StringBuilder model = new StringBuilder(10000);
      for (OrcsTypeSheet sheet : sheets) {
         String oseeTypeFragment = sheet.getTypesSheet();
         oseeTypeFragment = oseeTypeFragment.replaceAll("import\\s+\"", "// import \"");
         model.append("\n");
         model.append("//////////////     ");
         model.append(sheet.getName());
         model.append("\n\n");
         model.append(oseeTypeFragment);
      }
      OsgiUtil.getService(getClass(), OseeClient.class).getTypesEndpoint().setTypes(model.toString());
      OsgiUtil.getService(getClass(), IOseeCachingService.class).reloadTypes();
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

   private List<OrcsTypeSheet> getOseeTypeExtensionsById(Collection<String> uniqueIdsToImport) {
      List<OrcsTypeSheet> items = new LinkedList<>();
      for (OrcsTypeSheet sheet : getOseeTypeExtensions()) {
         if (uniqueIdsToImport.contains(sheet.getName())) {
            items.add(sheet);
         }
      }
      OseeLog.logf(Activator.class, Level.INFO, "Importing:\n\t%s", items.toString().replaceAll(",", ",\n\t"));
      return items;
   }
}