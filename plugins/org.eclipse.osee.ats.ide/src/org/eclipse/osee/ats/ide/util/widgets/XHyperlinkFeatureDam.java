/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Branches;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.StringNameComparator;
import org.eclipse.osee.framework.ui.skynet.widgets.AttributeWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelValueSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Donald G. Dunne
 */
public class XHyperlinkFeatureDam extends XHyperlinkLabelValueSelection implements AttributeWidget {

   private IAtsTeamWorkflow teamWf;
   private AttributeTypeToken attributeTypeToken;
   private final AtsApi atsApi;
   private String value = Widgets.NOT_SET;
   private Collection<Object> values;

   public XHyperlinkFeatureDam() {
      this("Feature");
   }

   public XHyperlinkFeatureDam(String label) {
      super(label);
      atsApi = AtsApiService.get();
   }

   @Override
   public String getCurrentValue() {
      return value;
   }

   @Override
   public Artifact getArtifact() {
      return (Artifact) teamWf.getStoreObject();
   }

   @Override
   public void saveToArtifact() {
      // do nothing
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeTypeToken) {
      this.attributeTypeToken = attributeTypeToken;
      if (artifact instanceof IAtsTeamWorkflow) {
         teamWf = (IAtsTeamWorkflow) artifact;
      }
   }

   @Override
   public boolean handleSelection() {
      try {
         FilteredCheckboxTreeDialog<FeatureDefinition> dialog = new FilteredCheckboxTreeDialog<FeatureDefinition>(
            "Select Feature(s) Impacted", "Select Feature(s) Impacted", new ArrayTreeContentProvider(),
            new StringLabelProvider(), new StringNameComparator());
         dialog.setInput(getFeatureDefinitions());
         Collection<FeatureDefinition> selectedFeatures = getSelectedFeatures();
         if (!selectedFeatures.isEmpty()) {
            dialog.setInitialSelections(selectedFeatures);
         }
         dialog.setShowSelectButtons(true);
         if (dialog.open() == Window.OK) {
            List<Object> values = new ArrayList<>();
            Collection<FeatureDefinition> checked = dialog.getChecked();
            for (FeatureDefinition featureDef : checked) {
               values.add(ArtifactId.valueOf(featureDef.getId()));
            }
            IAtsChangeSet changes = atsApi.createChangeSet(getLabel());
            changes.setAttributeValues(teamWf, attributeTypeToken, values);
            changes.execute();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   private Collection<FeatureDefinition> getSelectedFeatures() {
      List<FeatureDefinition> selected = new ArrayList<>();
      values = atsApi.getAttributeResolver().getAttributeValues(teamWf, attributeTypeToken);
      if (!values.isEmpty()) {
         for (Object obj : values) {
            ArtifactId featureId = (ArtifactId) obj;
            for (FeatureDefinition fDef : getFeatureDefinitions()) {
               if (fDef.getId().equals(featureId.getId())) {
                  selected.add(fDef);
                  continue;
               }
            }
         }
      }
      return selected;
   }

   @Override
   public void refresh() {
      values = atsApi.getAttributeResolver().getAttributeValues(teamWf, attributeTypeToken);
      if (values.isEmpty()) {
         value = Widgets.NOT_SET;
         super.refresh();
      } else {
         List<IOperation> ops = new ArrayList<>();
         final XHyperlinkFeatureDam fHyperlinkFeatureDam = this;
         IOperation op = new AbstractOperation("Load Features", Activator.PLUGIN_ID) {
            @Override
            protected void doWork(IProgressMonitor monitor) throws Exception {
               List<String> valueStrs = new ArrayList<>();
               for (Object obj : values) {
                  ArtifactId featureId = (ArtifactId) obj;
                  for (FeatureDefinition fDef : fHyperlinkFeatureDam.getFeatureDefinitions()) {
                     if (featureId.getId().equals(fDef.getId())) {
                        valueStrs.add(fDef.getName());
                        continue;
                     }
                  }
               }
               Collections.sort(valueStrs);
               value = org.eclipse.osee.framework.jdk.core.util.Collections.toString(";  ", valueStrs);
            }
         };
         ops.add(op);
         IOperation operation = Operations.createBuilder("Load Feature Widget").addAll(ops).build();
         Operations.executeAsJob(operation, false, Job.LONG, new ReloadJobChangeAdapter(this));
      }
   }

   public void refreshSuper() {
      super.refresh();
   }

   private final class ReloadJobChangeAdapter extends JobChangeAdapter {

      private final XHyperlinkFeatureDam xHyperlinkFeatureDam;

      private ReloadJobChangeAdapter(XHyperlinkFeatureDam xHyperlinkFeatureDam) {
         this.xHyperlinkFeatureDam = xHyperlinkFeatureDam;
      }

      @Override
      public void done(IJobChangeEvent event) {
         super.done(event);
         Job job = new UIJob("Load Selected Feature(s)") {

            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
               xHyperlinkFeatureDam.refreshSuper();
               return Status.OK_STATUS;
            }
         };
         Operations.scheduleJob(job, false, Job.SHORT, null);
      }
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeTypeToken;
   }

   private List<FeatureDefinition> getFeatureDefinitions() {
      BranchToken branch = getBranch();
      if (Branches.isValid(branch)) {
         ApplicabilityEndpoint applicEndpoint = AtsApiService.get().getOseeClient().getApplicabilityEndpoint(branch);
         return applicEndpoint.getFeatureDefinitionData();
      }
      return Collections.emptyList();
   }

   private BranchToken getBranch() {
      if (teamWf != null) {
         BranchToken branch = atsApi.getBranchService().getWorkingBranch(teamWf);
         if (Branches.isValid(branch)) {
            return branch;
         }
         Collection<BranchToken> branches = atsApi.getBranchService().getBranchesCommittedTo(teamWf);
         if (Branches.isNotEmpty(branches)) {
            return branches.iterator().next();
         }
         IAtsTeamDefinition teamDef = teamWf.getTeamDefinition();
         BranchId branch2 = atsApi.getBranchService().getBranch(teamDef);
         if (Branches.isValid(branch2)) {
            return atsApi.getBranchService().getBranch(branch2);
         }
         IAtsTeamDefinition mainTeamDef = atsApi.getTeamDefinitionService().getTeamDefHoldingVersions(teamDef);
         if (mainTeamDef != null) {
            BranchId branch3 = atsApi.getBranchService().getBranch(mainTeamDef);
            if (Branches.isValid(branch3)) {
               return atsApi.getBranchService().getBranch(branch3);
            }
            for (IAtsVersion ver : atsApi.getVersionService().getVersions(mainTeamDef)) {
               BranchId branch4 = atsApi.getBranchService().getBranch(ver);
               if (Branches.isValid(branch4)) {
                  return atsApi.getBranchService().getBranch(branch4);
               }
            }
         }
      }
      return BranchToken.SENTINEL;
   }
}
