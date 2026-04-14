/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.ats.rest.internal.report;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.report.BuildMemoRequest;
import org.eclipse.osee.ats.api.report.BuildMemoRequest.BuildMemoCommit;
import org.eclipse.osee.ats.api.report.BuildMemoRequest.BuildMemoRepository;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollectionSet;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

public class BuildMemoOperation {

   private static final String DEFAULT_ID = "NO_ID";
   private static final String CSS = loadResource("html/build_memo.css");
   private static final String JS = loadResource("html/build_memo.js");

   private final AtsApi atsApi;

   public BuildMemoOperation(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public String generateBuildMemo(BuildMemoRequest request) {
      Set<CommitType> allowedTypes = new HashSet<>();
      if (request.getAllowedCommitTypes() != null && !request.getAllowedCommitTypes().isEmpty()) {
         for (String type : request.getAllowedCommitTypes()) {
            allowedTypes.add(CommitType.fromString(type));
         }
      }

      Map<String, Integer> repoCommitCounts = new LinkedHashMap<>();
      int filteredCount = 0;
      CompositeKeyHashMap<CommitType, CmOrigin, HashCollectionSet<String, Commit>> commitMap =
         new CompositeKeyHashMap<>();
      for (BuildMemoCommit bc : request.getCommits()) {
         Commit commit = Commit.createCommit(bc.getType(),
            bc.getCmOrigin() != null ? bc.getCmOrigin() : CmOrigin.UNKNOWN.name(),
            bc.getId() != null ? bc.getId() : DEFAULT_ID, bc.getTitle(), bc.getMessage(), bc.getBody());

         String repo = Strings.isValid(bc.getRepository()) ? bc.getRepository() : "Unknown";
         repoCommitCounts.merge(repo, 1, Integer::sum);

         if (!allowedTypes.isEmpty() && !allowedTypes.contains(commit.getType())) {
            filteredCount++;
            continue;
         }

         HashCollectionSet<String, Commit> data = commitMap.get(commit.getType(), commit.getCM());
         if (data == null) {
            data = new HashCollectionSet<>(HashSet::new);
            commitMap.put(commit.getType(), commit.getCM(), data);
         }
         data.put(commit.getId(), commit);
      }

      int totalCommits = request.getCommits() != null ? request.getCommits().size() : 0;
      int includedCount = totalCommits - filteredCount;

      StringBuilder sb = new StringBuilder();
      writePageHeader(sb, request.getTitle());
      writeRepoInfo(sb, request, repoCommitCounts, totalCommits, includedCount, filteredCount);
      writeCommits(sb, commitMap);
      writePageFooter(sb);
      return sb.toString();
   }

   private void writePageHeader(StringBuilder sb, String title) {
      String safeTitle = title != null ? title : "Build Memo";
      sb.append("""
         <!DOCTYPE html><html><head><meta charset="UTF-8">
         <title>%s</title>
         <style>%s</style>
         <script>%s</script>
         </head><body>
         <div class="memo-title">%s</div>
         """.formatted(safeTitle, CSS, JS, safeTitle));
   }

   private void writePageFooter(StringBuilder sb) {
      sb.append("</body></html>");
   }

   private void writeRepoInfo(StringBuilder sb, BuildMemoRequest request, Map<String, Integer> repoCommitCounts,
      int totalCommits, int includedCount, int filteredCount) {
      sb.append("""
         <div class="section">
         <div class="section-header">Git Repositories</div>
         <table class="repo-table">
         <tr><th>Repository</th><th>From Tag</th><th>To Tag</th><th style="width:80px">Commits</th></tr>
         """);

      if (request.getRepositories() != null && !request.getRepositories().isEmpty()) {
         for (BuildMemoRepository repo : request.getRepositories()) {
            String name = Strings.isValid(repo.getName()) ? repo.getName() : "Unknown";
            String from = Strings.isValid(repo.getFromTag()) ? repo.getFromTag() : request.getFromTag();
            String to = Strings.isValid(repo.getToTag()) ? repo.getToTag() : request.getToTag();
            int count = repoCommitCounts.getOrDefault(name, 0);
            sb.append("<tr><td>%s</td><td>%s</td><td>%s</td><td>%d</td></tr>\n".formatted(
               name, nvl(from), nvl(to), count));
         }
      } else {
         sb.append("<tr><td>All</td><td>%s</td><td>%s</td><td>%d</td></tr>\n".formatted(
            nvl(request.getFromTag()), nvl(request.getToTag()), totalCommits));
      }

      sb.append("""
         </table>
         <div class="stats">
         <div class="stat-item">Total: <span class="stat-value">&nbsp;%d</span></div>
         <div class="stat-item">Included: <span class="stat-value">&nbsp;%d</span></div>
         <div class="stat-item">Filtered: <span class="stat-value">&nbsp;%d</span></div>
         </div>
         </div>
         """.formatted(totalCommits, includedCount, filteredCount));
   }

   private void writeCommits(StringBuilder mainSb,
      CompositeKeyHashMap<CommitType, CmOrigin, HashCollectionSet<String, Commit>> commitMap) {
      for (CommitType commitType : CommitType.values()) {
         List<String[]> rows = new ArrayList<>();
         for (CmOrigin cm : CmOrigin.values()) {
            HashCollectionSet<String, Commit> items = commitMap.get(commitType, cm);
            if (items != null) {
               Map<String, IAtsWorkItem> workItems = fetchWorkItems(cm, items.keySet());
               for (Entry<String, Set<Commit>> entry : items.entrySet()) {
                  String id = entry.getKey();
                  IAtsWorkItem workItem = workItems.get(id);
                  String cmName = workItem != null ? workItem.getName() : null;
                  String cmDescription =
                     workItem != null && Strings.isValid(workItem.getDescription()) ? workItem.getDescription().trim() : null;
                  String identifier = isIdValid(id) ? id : "";
                  Collection<Collection<Commit>> commitRows = normalizeCommits(id, entry.getValue());
                  for (Collection<Commit> commits : commitRows) {
                     String cellHtml = createCellHtml(cmName, cmDescription, commits);
                     rows.add(new String[] {identifier, cellHtml});
                  }
               }
            }
         }
         if (!rows.isEmpty()) {
            writeSectionForType(mainSb, commitType, rows);
         }
      }
   }

   private void writeSectionForType(StringBuilder sb, CommitType commitType, List<String[]> rows) {
      String badgeClass = "badge-" + commitType.name().toLowerCase();
      String typeName = commitType.name().substring(0, 1) + commitType.name().substring(1).toLowerCase();

      sb.append("""
         <div class="section">
         <div class="section-header">
         <span class="badge %s">%s</span>
         <span class="badge-count">%d</span>
         <button class="expand-btn" onclick="toggleAll(this)">Expand All</button>
         </div>
         <table><tr><th class="col-id">Id</th><th class="col-desc">Details</th></tr>
         """.formatted(badgeClass, typeName, rows.size()));

      for (String[] row : rows) {
         sb.append("""
            <tr><td class="col-id">%s</td><td class="col-desc">%s</td></tr>
            """.formatted(row[0], row[1]));
      }
      sb.append("</table></div>\n");
   }

   private boolean isIdValid(String id) {
      return Strings.isValid(id) && !DEFAULT_ID.equals(id);
   }

   private Map<String, IAtsWorkItem> fetchWorkItems(CmOrigin cm, Set<String> ids) {
      if (cm == CmOrigin.ATS) {
         return atsApi.getWorkItemService().getWorkItemsByAtsId(ids);
      }
      return Collections.emptyMap();
   }

   private Collection<Collection<Commit>> normalizeCommits(String id, Collection<Commit> commitSet) {
      if (isIdValid(id)) {
         return Collections.singleton(commitSet);
      }
      List<Collection<Commit>> rows = new ArrayList<>();
      for (Commit commit : commitSet) {
         rows.add(Collections.singleton(commit));
      }
      return rows;
   }

   private String createCellHtml(String cmName, String cmDescription, Collection<Commit> commits) {
      StringBuilder cell = new StringBuilder();
      Iterator<Commit> iterator = commits.iterator();
      Commit firstCommit = iterator.next();

      String title = Strings.isValid(cmName) ? cmName : firstCommit.getTitle();
      String safeTitle = Strings.isValid(title) ? escapeHtml(title) : "(no title)";

      cell.append("""
         <details>
         <summary><span class="entry-title">%s</span></summary>
         <div class="detail-body">
         <span class="entry-label">Description</span>
         <div class="entry-desc">""".formatted(safeTitle));

      cell.append(buildDescription(cmDescription, firstCommit));
      cell.append("</div>");

      if (iterator.hasNext()) {
         cell.append("""
            <span class="entry-label">Commits</span>
            <ul class="commit-list">""");
         while (iterator.hasNext()) {
            String msg = iterator.next().getMessage();
            if (Strings.isValid(msg)) {
               cell.append("<li>");
               for (String line : msg.split("\n")) {
                  if (!line.startsWith("Signed-off-by")) {
                     cell.append(escapeHtml(line));
                  }
               }
               cell.append("</li>");
            }
         }
         cell.append("</ul>");
      }

      cell.append("</div></details>");
      return cell.toString();
   }

   private String buildDescription(String cmDescription, Commit firstCommit) {
      if (Strings.isValid(cmDescription)) {
         return escapeHtml(cmDescription.replaceAll("[ \n\r]+$", "")).replace("\n", "<br/>");
      }
      String msg = firstCommit.getMessage();
      if (!Strings.isValid(msg)) {
         return "";
      }
      msg = msg.substring(0, 1).toUpperCase() + msg.substring(1);
      StringBuilder desc = new StringBuilder();
      for (String line : msg.split("\n")) {
         line = line.replaceAll("[ \n\r]+$", "");
         if (!line.contains("Signed-off-by") && Strings.isValid(line)) {
            if (desc.length() > 0) {
               desc.append("<br/>");
            }
            desc.append(escapeHtml(line));
         }
      }
      return desc.toString();
   }

   private static String nvl(String value) {
      return value != null ? value : "";
   }

   private static String escapeHtml(String text) {
      if (text == null) {
         return "";
      }
      return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
   }

   private static String loadResource(String path) {
      try (InputStream is = BuildMemoOperation.class.getResourceAsStream(path)) {
         if (is == null) {
            throw new OseeCoreException("Build memo resource not found: %s", path);
         }
         try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
         }
      } catch (IOException ex) {
         throw new OseeCoreException("Failed to load build memo resource: %s", path, ex);
      }
   }
}
