/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.database.init.internal;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.data.OrcsTypeSheet;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.osgi.framework.Bundle;

/**
 * This class provides necessary functionality for database initialization to load types through their extension points.
 *
 * @author Andrew M. Finkbeiner
 * @author Donald G. Dunne
 */
public class OseeTypesSetup {
   private static final String DECLARING_PLUGIN_ID = "org.eclipse.osee.framework.skynet.core";
   private static final String OSEE_TYPES_ELEMENT = "OseeTypes";
   private static final String OSEE_TYPES_EXTENSION_ID = DECLARING_PLUGIN_ID + "." + OSEE_TYPES_ELEMENT;

   public static List<OrcsTypeSheet> getOseeTypeExtensions() {
      List<OrcsTypeSheet> oseeTypes = new LinkedList<>();
      for (IConfigurationElement element : ExtensionPoints.getExtensionElements(OSEE_TYPES_EXTENSION_ID, "OseeTypes")) {
         OrcsTypeSheet sheet = new OrcsTypeSheet();
         String resourceName = element.getAttribute("resource");
         Bundle bundle = Platform.getBundle(element.getContributor().getName());
         URL url = bundle.getEntry(resourceName);
         sheet.setName(element.getDeclaringExtension().getUniqueIdentifier());
         sheet.setId(element.getAttribute("id"));
         try {
            if (url != null) {
               sheet.setTypesSheet(Lib.inputStreamToString(new BufferedInputStream(url.openStream())));
               oseeTypes.add(sheet);
            }
         } catch (IOException ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
      }
      return oseeTypes;
   }

   public static String getOseeTypeModelByExtensions(Collection<String> uniqueIdsToImport) {
      StringBuilder unifiedModel = new StringBuilder(10000);
      for (OrcsTypeSheet sheet : getOseeTypeExtensions()) {
         if (uniqueIdsToImport.contains(sheet.getName())) {
            unifiedModel.append("//////////////     ");
            unifiedModel.append(sheet.getName());
            unifiedModel.append("\n\n");
            unifiedModel.append(sheet.getTypesSheet().replaceAll("import\\s+\"", "// import \""));
         }
      }
      return unifiedModel.toString();
   }
}