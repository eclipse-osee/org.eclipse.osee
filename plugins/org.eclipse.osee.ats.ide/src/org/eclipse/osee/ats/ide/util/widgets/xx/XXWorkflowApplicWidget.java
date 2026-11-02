/*********************************************************************
 * Copyright (c) 2016 Boeing
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
package org.eclipse.osee.ats.ide.util.widgets.xx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.agile.ApplicabilityColumnUI;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.util.XXWorkItemData;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.data.ApplicabilityData;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXAbstractBaseXWidget;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXWorkflowApplicWidget extends XXAbstractBaseXWidget<ApplicabilityToken> {

   public static WidgetId ID = WidgetIdAts.XXWorkflowApplicWidget;

   protected XXWorkItemData xxWid = XXWorkItemData.SENTINEL;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXWorkflowApplicWidget() {
      super(ID, "Applicability");
      set(XOption.VAL_SINGLE_SEL);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public Collection<ApplicabilityToken> getSelectable() {
      return getStoredApplicabilities();
   }

   @Override
   public boolean handleSelection() {
      boolean changed = false;
      if (xxWid.isTeamWf()) {
         IAtsTeamWorkflow teamWf = xxWid.getTeamWf();
         changed = ApplicabilityColumnUI.promptChangeApplicability(
            Arrays.asList((AbstractWorkflowArtifact) teamWf.getStoreObject()));
         if (changed) {
            refresh();
         }
      }
      return changed;
   }

   @Override
   protected TransactionToken handleSelectedPersist() {
      List<ApplicabilityData> appDatas = new LinkedList<>();
      ApplicabilityData data = new ApplicabilityData();
      data.setArtifact(ArtifactId.create(xxWid.getTeamWf().getStoreObject()));
      List<ApplicabilityToken> toks = new ArrayList<>();
      toks.addAll(getSelected());
      data.setApplIds(toks);
      appDatas.add(data);
      return AtsApiService.get().getOseeClient().getApplicabilityEndpoint(
         AtsApiService.get().getAtsBranch()).setApplicabilityReference(appDatas);
   }

   @Override
   public ApplicabilityToken getSentinel() {
      return ApplicabilityToken.SENTINEL;
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

   //////////////////////////////////////////
   // Refresh / Lifecycle
   //////////////////////////////////////////

   @Override
   public void refresh() {
      super.refresh();
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   @Override
   public Object getData() {
      return getSelected();
   }

   private List<ApplicabilityToken> getStoredApplicabilities() {
      List<ApplicabilityToken> tokens = new ArrayList<ApplicabilityToken>();
      try {
         BranchToken branch = AtsApiService.get().getAtsBranch();
         ApplicabilityEndpoint applicEndpoint = AtsApiService.get().getOseeClient().getApplicabilityEndpoint(branch);
         List<ApplicabilityToken> applicTokens =
            applicEndpoint.getApplicabilityReferenceTokens(xxWid.getTeamWf().getArtifactId());
         for (ApplicabilityToken tok : applicTokens) {
            if (tok.isValid()) {
               tokens.add(tok);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(getClass(), Level.SEVERE,
            String.format("Exception getting applicability for workItem %s", getArtifact().toStringWithId()), ex);
      }
      return tokens;
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   public void setArtifact(Artifact artifact) {
      xxWid = XXWorkItemData.get(artifact);
      super.setArtifact(artifact);
   }

   @Override
   protected void initializeSelected() {
      ApplicabilityEndpoint applicEp =
         AtsApiService.get().getOseeClient().getApplicabilityEndpoint(CoreBranches.COMMON);
      ApplicabilityToken tok = applicEp.getApplicabilityToken(xxWid.getTeamWf().getArtifactId());
      if (tok == null || tok.isInvalid()) {
         clear(false);
      } else {
         setSelected(tok, false);
      }
   }

}
