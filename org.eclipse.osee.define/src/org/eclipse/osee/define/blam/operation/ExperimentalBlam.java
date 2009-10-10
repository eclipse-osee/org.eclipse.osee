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
package org.eclipse.osee.define.blam.operation;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 */
public class ExperimentalBlam extends AbstractBlam {

   @Override
   public String getName() {
      return "Experimental Blam";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      String basePath = "L:\\root\\lba_oseex\\osee_server_data\\datastore_lba8_fs\\attr";

      populateAttributeData(basePath);
      //      ConnectionHandler.runBatchUpdate("update osee_attribute set uri = ? where gamma_id = ?", renameData);

   }

   public void populateAttributeData(String basePath) throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery("select attr.gamma_id, attr.uri, art.human_readable_id, art.guid from osee_attribute attr, osee_artifact art where uri is not null and attr.art_id = art.art_id");
         while (chStmt.next()) {
            String guid = chStmt.getString("GUID");
            int gammaId = chStmt.getInt("GAMMA_ID");
            String uri = chStmt.getString("URI");
            String hrid = chStmt.getString("HUMAN_READABLE_ID");
            if (Strings.isValid(uri)) {
               handleFileFix(basePath, uri, guid, hrid);
            } else {
               //OseeLog.log(DefinePlugin.class, Level.SEVERE, "Empty uri detected");
            }
         }
      } finally {
         chStmt.close();
      }
   }

   private void handleFileFix(String basePath, String uri, String guid, String hrid) {
      String path = uri.replace("attr://", "");
      if (!path.endsWith(".zip")) {
         System.err.println("Did not end with zip: " + path);
         if (path.endsWith(".")) {
            path = path + "zip";
         } else {
            path = path + ".zip";
         }
      }
      File expectedFile = new File(basePath, path);
      if (!expectedFile.exists()) {
         String oldName = path.replace(guid, hrid);
         File unconvertedFile = new File(basePath, oldName);
         if (unconvertedFile.exists()) {
            boolean result = unconvertedFile.renameTo(expectedFile);
            //                     expectedFile.renameTo(unconvertedFile);
            if (!result) {
               System.err.println("Error renaming: " + oldName + " to " + expectedFile);
            }
         } else {
            System.err.println("Could not find: " + oldName);
         }
      }
   }

   //   private String fixPath(){
   //      //
   //      (uri.endsWith("xml.zip") || uri.endsWith("vsd.zip") || uri.endsWith("txt.zip") || //
   //      uri.endsWith("vue.zip") || uri.endsWith("nb.zip") || uri.endsWith("xls.zip") || //
   //      uri.endsWith("ppt.zip") || uri.endsWith("pdf.zip")
   //      String newURI = uri;
   //      newURI = newURI.replace("xml.", "");
   //      newURI = newURI.replace("vsd.", "");
   //      newURI = newURI.replace("vue.", "");
   //      newURI = newURI.replace("ppt.", "");
   //      newURI = newURI.replace("txt.", "");
   //      newURI = newURI.replace("nb.", "");
   //      newURI = newURI.replace("xls.", "");
   //      newURI = newURI.replace("pdf.", "");
   //   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }
}