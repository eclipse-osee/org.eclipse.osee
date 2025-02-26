/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.testscript.internal;

import java.io.File;
import java.util.Date;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.testscript.ScriptResultToken;
import org.eclipse.osee.testscript.TmoFileApi;

public class TmoFileApiImpl implements TmoFileApi {

   private final String basePath;
   private final String folderName = "testscripts";

   public TmoFileApiImpl(OrcsApi orcsApi) {
      this.basePath = orcsApi.getSystemProperties().getValue(OseeClient.OSEE_APPLICATION_SERVER_DATA);
   }

   @Override
   public String getBasePath() {
      return basePath + File.separator + folderName + File.separator;
   }

   @Override
   public String getTmoPath(String fileName) {
      if (fileName.startsWith(basePath)) {
         return fileName;
      }
      return getBasePath() + fileName;
   }

   @Override
   public String getTmoPath(ScriptResultToken result) {
      String resultPath = result.getFileUrl();
      if (resultPath.startsWith(basePath)) {
         return resultPath;
      }
      return getBasePath() + resultPath;
   }

   @Override
   public String getBatchPath(ScriptBatchToken batch) {
      String batchPath = batch.getFolderUrl();
      if (batchPath.startsWith(basePath)) {
         return batchPath;
      }
      return getBasePath() + batchPath;
   }

   @Override
   public String createTmoFileName(String scriptName, Date executionDate, ArtifactId ciSetId) {
      return ciSetId.getIdString() + File.separator + scriptName + "_" + executionDate.getTime() + ".zip";
   }

   @Override
   public String createBatchFileName(String scriptName, Date executionDate, ArtifactId ciSetId, String batchId) {
      return ciSetId.getIdString() + File.separator + batchId + File.separator + scriptName + "_" + executionDate.getTime() + ".zip";
   }

   @Override
   public String getTmoFolderPath(ArtifactId ciSetId) {
      return getBasePath() + ciSetId.getIdString() + File.separator;
   }

   @Override
   public String getBatchFolderPath(ArtifactId ciSetId, String batchId) {
      return getBasePath() + ciSetId.getIdString() + File.separator + batchId + File.separator;
   }
}
