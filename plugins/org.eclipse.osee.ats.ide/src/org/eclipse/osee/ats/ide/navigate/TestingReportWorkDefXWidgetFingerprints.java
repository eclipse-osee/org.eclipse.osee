/*********************************************************************
 * Copyright (c) 2026 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 **********************************************************************/

package org.eclipse.osee.ats.ide.navigate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.data.AtsUserGroups;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workdef.model.WorkDefArtifactTypeContext;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workdef.WorkDefXWidgetFingerprint;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;

/**
 * Produces resolved XWidgetData fingerprints for each deployed WorkDef/artifact-type context without creating controls.
 */
public class TestingReportWorkDefXWidgetFingerprints extends XNavigateItemAction {

   private static final String TITLE = "Testing - Report WorkDef XWidget Fingerprints";

   public TestingReportWorkDefXWidgetFingerprints() {
      super(TITLE, AtsImage.WORKFLOW, Arrays.asList(AtsUserGroups.AtsAdmin), AtsNavigateViewItems.ATS_ADMIN_TESTING);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      try {
         String json =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(getFingerprintData());
         Path report = OseeData.getPath().append("WorkDefXWidgetFingerprints.json").toFile().toPath();
         Files.writeString(report, json);
         ResultsEditor.open(TITLE, TITLE, "<pre>" + json + "</pre>");
      } catch (Exception ex) {
         XResultData results = new XResultData();
         results.error(ex.getMessage());
         XResultDataUI.report(results, TITLE);
      }
   }

   public static Map<String, Object> getFingerprintData() {
      List<Map<String, Object>> resolved = new java.util.ArrayList<>();
      for (WorkDefArtifactTypeContext context : AtsApiService.get().getServerEndpoints().getWorkDefEp().getArtifactTypeContexts()) {
         WorkDefinition workDef = AtsApiService.get().getWorkDefinitionService().getWorkDefinition(context.getWorkDefId());
         if (workDef != null) {
            ArtifactTypeToken artifactType = AtsApiService.get().tokenService().getArtifactType(context.getArtifactTypeId());
            Map<String, Object> fingerprint = WorkDefXWidgetFingerprint.createData(workDef, artifactType);
            fingerprint.put("artifactType", artifactType.toStringWithId());
            fingerprint.put("workflowCount", context.getWorkflowCount());
            resolved.add(fingerprint);
         }
      }
      Map<String, Object> report = new LinkedHashMap<>();
      report.put("generatedAt", Instant.now().toString());
      report.put("branch", AtsApiService.get().getAtsBranch().toString());
      report.put("workDefs", resolved);
      return report;
   }
}
