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
package org.eclipse.osee.ats.report;

import java.util.Collection;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ATSBranchMetrics;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class GenerateAtsBranchMetricsCacheReport extends XNavigateItemAction {

   /**
    * @param parent
    */
   public GenerateAtsBranchMetricsCacheReport(XNavigateItem parent) {
      super(parent, "Generate AtsBranchMetrics Cache Report");
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) return;

      reportAtsBranchMetrics();
   }

   private void reportAtsBranchMetrics() throws OseeCoreException {
      XResultData rd = new XResultData();
      String title = "AtsBranchMetrics as of " + XDate.getDateNow(XDate.MMDDYYHHMM);
      rd.log(title);
      rd.addRaw(AHTML.beginMultiColumnTable(95, 1));
      String[] columnHeaders = new String[] {"Team", "PCR", "Working", "Mod", "Del", "NonRelOnly"};
      rd.addRaw(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
      Collection<Artifact> arts =
            ArtifactQuery.getArtifactsFromAttributeType(ATSAttributes.BRANCH_METRICS_ATTRIBUTE.getStoreName(),
                  AtsPlugin.getAtsBranch());
      for (Artifact art : arts) {
         TeamWorkFlowArtifact team = (TeamWorkFlowArtifact) art;
         ATSBranchMetrics metrics = team.getSmaMgr().getBranchMgr().getAtsBranchMetrics(false);
         if (metrics.isCached()) {
            rd.addRaw(AHTML.addRowMultiColumnTable(team.getHumanReadableId(), team.getSoleAttributeValue(
                  ATSAttributes.LEGACY_PCR_ID_ATTRIBUTE.getStoreName(), ""),
                  String.valueOf(metrics.getNumModifiedArtifacts()), String.valueOf(metrics.getNumDeletedArtifacts()),
                  String.valueOf(metrics.getNumNonRelationModifiedArtifacts())));
         }
      }
      rd.addRaw(AHTML.endMultiColumnTable());
      rd.report(title);
   }

}
