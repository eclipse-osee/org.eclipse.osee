/*********************************************************************
 * Copyright (c) 2023 Boeing
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.transaction.AddRelation;
import org.eclipse.osee.orcs.rest.model.transaction.CreateArtifact;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderData;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderDataFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.testscript.ScriptDefApi;
import org.eclipse.osee.testscript.TmoImportApi;

/**
 * @author Ryan T. Baldwin
 */
public class TmoImportApiImpl implements TmoImportApi {

   private final OrcsApi orcsApi;
   private final ScriptDefApi scriptDefApi;
   private int keyIndex = 0;

   public TmoImportApiImpl(OrcsApi orcsApi, ScriptDefApi scriptDefApi) {
      this.orcsApi = orcsApi;
      this.scriptDefApi = scriptDefApi;
   }

   @Override
   public ScriptDefToken getScriptDefinition(InputStream stream, ArtifactId ciSetId) {
      return new ImportTmoReader().getScriptDefinition(stream, ciSetId);
   }

   @Override
   public ScriptDefToken getScriptDefinition(File file, ArtifactId ciSetId) {
      return new ImportTmoReader().getScriptDefinition(file, ciSetId);
   }

   @Override
   public TransactionBuilderData getTxBuilderData(BranchId branch, ScriptDefToken scriptDef) {
      return this.getTxBuilderData(branch, scriptDef, true);
   }

   @Override
   public TransactionBuilderData getTxBuilderData(BranchId branch, ScriptDefToken scriptDef, boolean resetKey) {
      TransactionBuilderData data = new TransactionBuilderData();
      data.setBranch(branch.getIdString());
      data.setTxComment("TMO Import");
      data.setCreateArtifacts(new LinkedList<>());
      data.setAddRelations(new LinkedList<>());

      if (resetKey) {
         keyIndex = 0;
      }

      ////// Test Script Definition //////
      // If there is an existing definition for this script, do not create a new one.
      String scriptDefKey;
      ScriptDefToken existingDef = this.scriptDefApi.getAllByFilter(branch, scriptDef.getFullScriptName(),
         Arrays.asList(CoreAttributeTypes.ScriptName)).stream().filter(
            def -> def.getFullScriptName().equals(scriptDef.getFullScriptName())).findFirst().orElse(
               ScriptDefToken.SENTINEL);
      if (existingDef.isValid()) {
         scriptDefKey = existingDef.getArtifactId().getIdString();
      } else {
         scriptDefKey = getKey();
         CreateArtifact scriptDefArtifact = scriptDef.createArtifact(scriptDefKey);
         data.getCreateArtifacts().add(scriptDefArtifact);
      }

      ////// Test Script Result //////
      ScriptResultToken scriptResult = scriptDef.getScriptResults().get(0);
      CreateArtifact scriptResultArtifact = scriptResult.createArtifact(getKey());
      data.getCreateArtifacts().add(scriptResultArtifact);
      data.getAddRelations().add(createAddRelation(CoreRelationTypes.TestScriptDefToTestScriptResults, scriptDefKey,
         scriptResultArtifact.getKey()));

      return data;
   }

   @Override
   public TransactionResult importFile(InputStream stream, BranchId branch, ArtifactId ciSetId) {
      TransactionResult result = new TransactionResult();
      XResultData resultData = new XResultData();
      result.setResults(resultData);
      File file = getTempFile(ciSetId);

      try {
         Lib.inputStreamToFile(stream, file);
      } catch (IOException ex) {
         if (file != null && file.exists()) {
            file.delete();
         }
         resultData.error("Error reading uploaded file");
         return result;
      }

      ScriptDefToken scriptDef = this.getScriptDefinition(file, ciSetId);
      if (scriptDef.getScriptResults().isEmpty()) {
         if (file != null && file.exists()) {
            file.delete();
         }
         resultData.error("Error parsing TMO");
         return result;
      }

      ScriptResultToken scriptResult = scriptDef.getScriptResults().get(0);
      String zipPathString =
         getFolderPath(ciSetId) + scriptDef.getName() + "_" + scriptResult.getExecutionDate().getTime() + ".zip";
      scriptResult.setFileUrl(zipPathString);
      File zipPath = new File(zipPathString);
      if (zipPath.exists()) {
         if (file != null && file.exists()) {
            file.delete();
         }
         resultData.error(
            scriptDef.getName() + "_" + scriptResult.getExecutionDate().getTime() + ".zip" + " already exists in CI Set " + ciSetId + ". Did not create artifacts.");
         return result;
      }

      // Compress file
      try {
         moveFileToZip(file, zipPath, scriptDef);
      } catch (IOException ex) {
         resultData.error("Error creating zip file");
         if (file != null && file.exists()) {
            file.delete();
         }
         if (zipPath.exists()) {
            zipPath.delete();
         }

         keyIndex = 0;

         return result;
      }

      ObjectMapper mapper = new ObjectMapper();
      TransactionBuilderDataFactory txBdf = new TransactionBuilderDataFactory(orcsApi);
      TransactionBuilderData txData = this.getTxBuilderData(branch, scriptDef);

      try {
         TransactionBuilder tx = txBdf.loadFromJson(mapper.writeValueAsString(txData));
         TransactionToken token = tx.commit();
         result.setTx(token);
         resultData.setIds(
            tx.getTxDataReadables().stream().map(readable -> readable.getIdString()).collect(Collectors.toList()));
      } catch (JsonProcessingException ex) {
         resultData.error("Error processing tx json");
      }

      // If the tx failed, remove files.
      if (result.isFailed()) {
         if (zipPath.exists()) {
            zipPath.delete();
         }
      }

      return result;
   }

   @Override
   public TransactionResult importBatch(InputStream stream, BranchId branch, ArtifactId ciSetId) {
      String batchId = System.currentTimeMillis() + (int) (Math.random() * 100) + "";
      String testEnvBatchId = "";
      List<String> fileNames = new LinkedList<>();
      Date batchExecutionDate = new Date();
      String batchMachineName = "";
      SimpleDateFormat executionDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
      TransactionBuilderDataFactory txBdf = new TransactionBuilderDataFactory(orcsApi);
      TransactionBuilder tx = null;
      TransactionResult result = new TransactionResult();
      XResultData resultData = new XResultData();
      result.setResults(resultData);
      String batchFolderPath = getFolderPath(ciSetId, batchId);
      ObjectMapper mapper = new ObjectMapper();
      try (ZipInputStream zipStream = new ZipInputStream(stream)) {
         ZipEntry zipEntry = null;
         while ((zipEntry = zipStream.getNextEntry()) != null) {
            // OTE uses a runId.txt file to specify a batch ID. If this file exists,
            // assume this is a batched run and create a batch artifact and relations.
            if (zipEntry.getName().equals("runId.txt")) {
               testEnvBatchId = new String(zipStream.readAllBytes()).trim();
               continue;
            }
            // Skip any non-tmo files
            if (!zipEntry.getName().toLowerCase().endsWith(".tmo")) {
               continue;
            }
            File file = getTempFile(ciSetId);

            try {
               OutputStream outputStream = new FileOutputStream(file);
               Lib.inputStreamToOutputStream(zipStream, outputStream);
               outputStream.close();
            } catch (IOException ex) {
               if (file != null && file.exists()) {
                  file.delete();
               }
               resultData.addRaw("Error reading uploaded file");
               continue;
            }

            ScriptDefToken scriptDef = this.getScriptDefinition(file, ciSetId);
            if (scriptDef.getScriptResults().isEmpty()) {
               if (file != null && file.exists()) {
                  file.delete();
               }
               resultData.addRaw("Error parsing " + scriptDef.getName() + ".tmo");
               continue;
            }

            ScriptResultToken scriptResult = scriptDef.getScriptResults().get(0);
            File batchFolder = new File(batchFolderPath);
            if (!batchFolder.exists()) {
               batchFolder.mkdirs();
            }
            String zipPathString =
               batchFolderPath + scriptDef.getName() + "_" + scriptResult.getExecutionDate().getTime() + ".zip";
            scriptResult.setFileUrl(zipPathString);
            File zipPath = new File(zipPathString);
            if (zipPath.exists()) {
               if (file != null && file.exists()) {
                  file.delete();
               }
               resultData.addRaw(scriptDef.getName() + " TMO already exists. Did not create artifacts.");
               continue;
            }

            // Compress file
            try {
               moveFileToZip(file, zipPath, scriptDef);
            } catch (IOException ex) {
               if (file != null && file.exists()) {
                  file.delete();
               }
               if (zipPath.exists()) {
                  zipPath.delete();
               }
               resultData.addRaw("Error creating zip file for " + scriptDef.getName());
               continue;
            }
            fileNames.add(zipPathString);

            TransactionBuilderData txData = this.getTxBuilderData(branch, scriptDef, false);

            try {
               if (tx == null) {
                  tx = txBdf.loadFromJson(mapper.writeValueAsString(txData));
               } else {
                  tx = txBdf.loadFromJson(mapper.writeValueAsString(txData), tx);
               }
            } catch (JsonProcessingException ex) {
               resultData.error("Error processing tx json");
            }

            batchExecutionDate = scriptResult.getExecutionDate().before(
               batchExecutionDate) ? scriptResult.getExecutionDate() : batchExecutionDate;
            batchMachineName = scriptResult.getMachineName();
         }

         if (!testEnvBatchId.isEmpty() && tx != null) {
            TransactionBuilderData batchTxData = new TransactionBuilderData();
            batchTxData.setBranch(branch.getIdString());
            batchTxData.setTxComment("TMO Import");
            batchTxData.setCreateArtifacts(new LinkedList<>());
            batchTxData.setAddRelations(new LinkedList<>());
            ScriptBatchToken scriptBatch =
               new ScriptBatchToken(-1L, executionDateFormat.format(batchExecutionDate) + " - " + batchMachineName);
            scriptBatch.setExecutionDate(batchExecutionDate);
            scriptBatch.setMachineName(batchMachineName);
            scriptBatch.setBatchId(batchId);
            scriptBatch.setTestEnvBatchId(testEnvBatchId);
            scriptBatch.setFolderUrl(batchFolderPath);
            CreateArtifact batchArt = scriptBatch.createArtifact(getKey());
            batchTxData.getCreateArtifacts().add(batchArt);
            List<String> resultIds = tx.getTxDataReadables().stream().filter(
               art -> art.getArtifactType().equals(CoreArtifactTypes.TestScriptResults)).map(
                  art -> art.getIdString()).collect(Collectors.toList());
            for (String id : resultIds) {
               batchTxData.getAddRelations().add(
                  createAddRelation(CoreRelationTypes.ScriptBatchToTestScriptResult, batchArt.getKey(), id));
            }
            batchTxData.getAddRelations().add(
               createAddRelation(CoreRelationTypes.ScriptSetToScriptBatch, ciSetId.getIdString(), batchArt.getKey()));
            try {
               tx = txBdf.loadFromJson(mapper.writeValueAsString(batchTxData), tx);
            } catch (JsonProcessingException ex) {
               resultData.error("Error processing batch tx json");
            }
         }

         if (tx != null) {
            TransactionToken token = tx.commit();
            result.setTx(token);
            resultData.setIds(
               tx.getTxDataReadables().stream().map(readable -> readable.getIdString()).collect(Collectors.toList()));
         }

         // If the tx failed, remove files.
         if (result.isFailed()) {
            for (String fileName : fileNames) {
               File f = new File(fileName);
               if (f.exists()) {
                  f.delete();
               }
            }
         }

      } catch (IOException ex) {
         System.out.println(ex);
      }

      keyIndex = 0;

      return result;
   }

   private String getFolderPath(ArtifactId ciSetId) {
      String basePath = orcsApi.getSystemProperties().getValue(OseeClient.OSEE_APPLICATION_SERVER_DATA);
      return basePath + File.separator + "testscripts" + File.separator + ciSetId.getIdString() + File.separator;
   }

   private String getFolderPath(ArtifactId ciSetId, String batchId) {
      String basePath = orcsApi.getSystemProperties().getValue(OseeClient.OSEE_APPLICATION_SERVER_DATA);
      return basePath + File.separator + "testscripts" + File.separator + ciSetId.getIdString() + File.separator + batchId + File.separator;
   }

   /**
    * Get the temporary file to store tmo. Since we don't have the name of the file before reading it, use a randomly
    * generated name.
    *
    * @return
    */
   private File getTempFile(ArtifactId ciSetId) {
      String folderPath = getFolderPath(ciSetId);
      File ciSetFolder = new File(folderPath);
      if (!ciSetFolder.exists()) {
         ciSetFolder.mkdirs();
      }
      String fileName = System.currentTimeMillis() + (int) (Math.random() * 100) + ".tmo";
      File file = new File(folderPath + fileName);
      return file;
   }

   private void moveFileToZip(File file, File zipPath, ScriptDefToken scriptDef) throws IOException {
      Map<String, String> env = new HashMap<>();
      env.put("create", "true");
      URI uri = URI.create("jar:" + Paths.get(zipPath.getAbsolutePath()).toUri());
      try (FileSystem fs = FileSystems.newFileSystem(uri, env)) {
         Path nf = fs.getPath(scriptDef.getName() + ".tmo");
         Files.move(Paths.get(file.getAbsolutePath()), nf, StandardCopyOption.REPLACE_EXISTING);
      }
   }

   private String getKey() {
      keyIndex++;
      return "key" + keyIndex;
   }

   private AddRelation createAddRelation(RelationTypeToken relType, String artAId, String artBId) {
      AddRelation rel = new AddRelation();
      rel.setTypeId(relType.getIdString());
      rel.setaArtId(artAId);
      rel.setbArtId(artBId);
      return rel;
   }

}
