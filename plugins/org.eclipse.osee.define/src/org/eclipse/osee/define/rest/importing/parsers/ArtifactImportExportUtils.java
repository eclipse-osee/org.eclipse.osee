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
package org.eclipse.osee.define.rest.importing.parsers;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeReadable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.ShadowCoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Jaden W. Puckett
 */
public class ArtifactImportExportUtils {

   private final static String artifactExportResultsFilename = "artifact-export-results.md";

   public static byte[] exportArtifactRecordsAsZip(BranchId branchId, ArtifactId hierarchicalParentArtifactId,
      OrcsApi orcsApi) {

      List<Pair<ArtifactId, ArtifactId>> pairings = new ArrayList<>();
      List<ArtifactId> childArtIds = new ArrayList<>();
      StringBuilder log = new StringBuilder();

      Consumer<JdbcStatement> consumer = stmt -> {
         pairings.add(
            new Pair<>(ArtifactId.valueOf(stmt.getLong("b_art_id")), ArtifactId.valueOf(stmt.getLong("a_art_id"))));
         childArtIds.add(ArtifactId.valueOf(stmt.getLong("b_art_id")));
      };

      String query = "with " + orcsApi.getJdbcService().getClient().getDbType().getPostgresRecurse() //
         + " allRels (a_art_id, b_art_id, gamma_id, rel_type) as (select a_art_id, b_art_id, txs.gamma_id, rel_type " //
         + "from osee_txs txs, osee_relation rel " //
         + "where txs.branch_id = ? and txs.tx_current = 1 and txs.gamma_id = rel.gamma_id and rel.rel_type = ? " //
         + orcsApi.getJdbcService().getClient().getDbType().getCteRecursiveUnion() //
         + " select a_art_id, b_art_id, txs.gamma_id, rel_link_type_id rel_type " //
         + "from osee_txs txs, osee_relation_link rel " //
         + "where txs.branch_id = ? and txs.tx_current = 1 and txs.gamma_id = rel.gamma_id and rel.rel_link_type_id = ? ), " //
         + "cte_query (b_art_id, a_art_id, rel_type) as ( " //
         + "select b_art_id, a_art_id, rel_type " //
         + "from allRels " //
         + "where a_art_id = ? " //
         + orcsApi.getJdbcService().getClient().getDbType().getCteRecursiveUnion() //
         + " select e.b_art_id, e.a_art_id, e.rel_type " //
         + "from allRels e " //
         + "inner join cte_query c on c.b_art_id = e.a_art_id) " //
         + "select * " //
         + "from cte_query";

      orcsApi.getJdbcService().getClient().runQuery(consumer, query, branchId,
         ShadowCoreRelationTypes.DefaultHierarchicalRel.getId(), branchId,
         CoreRelationTypes.DefaultHierarchical.getId(), hierarchicalParentArtifactId);

      childArtIds.add(hierarchicalParentArtifactId);

      List<ArtifactReadable> artifacts =
         orcsApi.getQueryFactory().fromBranch(branchId).andIds(childArtIds).getResults().getList();

      ForkJoinPool forkJoinPool = new ForkJoinPool();
      List<ArtifactRecord> artifactRecords = null;
      try {
         artifactRecords = forkJoinPool.invoke(new ArtifactRecordTask(artifacts));
      } catch (Exception e) {
         log.append("## Error during ArtifactRecordTask\n").append("**Message:** ").append(e.getMessage()).append("\n");
         artifactRecords = Collections.emptyList();
      }

      ObjectMapper objectMapper = new ObjectMapper();

      try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ZipOutputStream zos = new ZipOutputStream(baos);) {

         for (ArtifactRecord record : artifactRecords) {
            ZipEntry entry = new ZipEntry(record.getArtifactId().toString() + ".json");
            zos.putNextEntry(entry);
            String json = objectMapper.writeValueAsString(record);
            zos.write(json.getBytes());
            zos.closeEntry();
         }

         //@formatter:off
         log.append("\n")
            .append("## Artifact Export Summary\n\n")
            .append("| Key | Value |\n")
            .append("| --- | ----- |\n")
            .append("| Number of ArtifactRecords | ")
            .append(artifactRecords != null ? artifactRecords.size() : 0)
            .append(" |\n")
            .append("| Exported | ")
            .append(LocalDateTime.now())
            .append(" |\n");
         //@formatter:on

         ZipEntry logEntry = new ZipEntry(artifactExportResultsFilename);
         zos.putNextEntry(logEntry);
         zos.write(log.toString().getBytes(StandardCharsets.UTF_8));
         zos.closeEntry();
         zos.finish();

         return baos.toByteArray();
      } catch (IOException e) {
         e.printStackTrace();
         return new byte[0];
      }
   }

   private static class ArtifactRecordTask extends RecursiveTask<List<ArtifactRecord>> {
      private static final long serialVersionUID = 1L;
      private static final int THRESHOLD = 10;
      private final List<ArtifactReadable> artifacts;

      public ArtifactRecordTask(List<ArtifactReadable> artifacts) {
         this.artifacts = artifacts;
      }

      @Override
      protected List<ArtifactRecord> compute() {
         if (artifacts.size() <= THRESHOLD) {
            return processArtifacts(artifacts);
         } else {
            int mid = artifacts.size() / 2;
            ArtifactRecordTask leftTask = new ArtifactRecordTask(artifacts.subList(0, mid));
            ArtifactRecordTask rightTask = new ArtifactRecordTask(artifacts.subList(mid, artifacts.size()));
            invokeAll(leftTask, rightTask);
            List<ArtifactRecord> leftResult = leftTask.join();
            List<ArtifactRecord> rightResult = rightTask.join();
            List<ArtifactRecord> result = new ArrayList<>();
            result.addAll(leftResult);
            result.addAll(rightResult);
            return result;
         }
      }

      private List<ArtifactRecord> processArtifacts(List<ArtifactReadable> artifacts) {
         List<ArtifactRecord> records = new ArrayList<>();
         for (ArtifactReadable artifact : artifacts) {
            String name = artifact.getName();
            List<AttributeReadable<Object>> wordTemplateAttrs = new ArrayList<>();
            for (AttributeReadable<Object> attr : artifact.getAttributes(CoreAttributeTypes.WordTemplateContent)) {
               wordTemplateAttrs.add(attr);
            }
            String wordTemplateContent =
               wordTemplateAttrs.stream().map(attr -> (String) attr.getValue()).findFirst().orElse(null);

            List<AttributeReadable<Object>> markdownAttrs = new ArrayList<>();
            for (AttributeReadable<Object> attr : artifact.getAttributes(CoreAttributeTypes.MarkdownContent)) {
               markdownAttrs.add(attr);
            }
            String markdownContent =
               markdownAttrs.stream().map(attr -> (String) attr.getValue()).findFirst().orElse(null);

            records.add(new ArtifactRecord(name, artifact.getArtifactId(), wordTemplateContent, markdownContent));
         }
         return records;
      }
   }

   public static class ArtifactRecord {
      private String name;
      private ArtifactId artifactId;
      private String wordTemplateContent;
      private String markdownContent;

      // No-arg constructor required by Jackson
      public ArtifactRecord() {
      }

      public ArtifactRecord(String name, ArtifactId artifactId, String wordTemplateContent, String markdownContent) {
         this.name = name;
         this.artifactId = artifactId;
         this.wordTemplateContent = wordTemplateContent;
         this.markdownContent = markdownContent;
      }

      public String getName() {
         return name;
      }

      public void setName(String name) {
         this.name = name;
      }

      public ArtifactId getArtifactId() {
         return artifactId;
      }

      public void setArtifactId(ArtifactId artifactId) {
         this.artifactId = artifactId;
      }

      public String getWordTemplateContent() {
         return wordTemplateContent;
      }

      public void setWordTemplateContent(String wordTemplateContent) {
         this.wordTemplateContent = wordTemplateContent;
      }

      public String getMarkdownContent() {
         return markdownContent;
      }

      public void setMarkdownContent(String markdownContent) {
         this.markdownContent = markdownContent;
      }
   }

   public static List<ArtifactRecord> readArtifactRecordsFromZip(byte[] zipBytes) {
      List<ArtifactRecord> artifactRecords = new ArrayList<>();
      ObjectMapper objectMapper = new ObjectMapper();

      try (ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);
         ZipInputStream zis = new ZipInputStream(bais)) {

         ZipEntry entry;
         byte[] buffer = new byte[8192];
         while ((entry = zis.getNextEntry()) != null) {
            String entryName = entry.getName();
            if (artifactExportResultsFilename.equals(entryName)) {
               zis.closeEntry();
               continue;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len;
            while ((len = zis.read(buffer)) > 0) {
               baos.write(buffer, 0, len);
            }
            byte[] entryBytes = baos.toByteArray();

            try {
               ArtifactRecord record = objectMapper.readValue(entryBytes, ArtifactRecord.class);
               artifactRecords.add(record);
            } catch (Exception e) {
               System.err.println("Failed to parse JSON in entry " + entryName + ": " + e.getMessage());
            }
            zis.closeEntry();
         }
      } catch (IOException e) {
         e.printStackTrace();
      }

      return artifactRecords;
   }
}
