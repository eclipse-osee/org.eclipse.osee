/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.agile;

import java.util.Date;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XButton;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class XRunSprintSummaryButton extends XButton implements IArtifactWidget {

   private IAgileSprint sprint;
   private final boolean editable = false;
   public static final String WIDGET_ID = XRunSprintSummaryButton.class.getSimpleName();

   public XRunSprintSummaryButton() {
      super("Run Sprint Summary");
      setImage(ImageManager.getImage(AtsImage.REPORT));
      setToolTip("Click to run Sprint Report");
      addXModifiedListener(listener);
   }

   @Override
   public Artifact getArtifact() {
      return (Artifact) sprint.getStoreObject();
   }

   XModifiedListener listener = new XModifiedListener() {
      @Override
      public void widgetModified(org.eclipse.osee.framework.ui.skynet.widgets.XWidget widget) {
         try {
            runReport();
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      };
   };

   private void runReport() {
      Date startDate = ((Artifact) sprint.getStoreObject()).getSoleAttributeValue(AtsAttributeTypes.StartDate, null);
      Date endDate = ((Artifact) sprint.getStoreObject()).getSoleAttributeValue(AtsAttributeTypes.EndDate, null);

      if (startDate == null || endDate == null) {
         AWorkbench.popup("Sprint must have start and end dates specified.");
         return;
      }

      Response response = AtsClientService.getAgileEndpoint().getSprintSummary(sprint.getTeamUuid(), sprint.getId());
      String reportHtml = response.readEntity(String.class);
      String appServer = System.getProperty(OseeClient.OSEE_APPLICATION_SERVER);
      if (Strings.isValid(appServer)) {
         reportHtml = reportHtml.replaceFirst("\\/ajax", appServer + "/ajax");
      }
      ResultsEditor.open("Report", "Sprint Summary Report", reportHtml);
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public void saveToArtifact() {
      // do nothing
   }

   @Override
   public void setArtifact(Artifact artifact) throws OseeCoreException {
      if (artifact.isOfType(AtsArtifactTypes.AgileSprint)) {
         this.sprint = (IAgileSprint) artifact;
      }
   }

   @Override
   public boolean isEditable() {
      return editable;
   }

}
