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

import java.io.File;
import java.io.InputStream;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.JaxBurndownExcel;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XButton;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.program.Program;

/**
 * @author Donald G. Dunne
 */
public class XOpenSprintBurndownButton extends XButton implements IArtifactWidget {

   private IAgileSprint sprint;
   private final boolean editable = false;
   public static final String WIDGET_ID = XOpenSprintBurndownButton.class.getSimpleName();

   public XOpenSprintBurndownButton() {
      super("Open Sprint Burndown/up");
      setImage(ImageManager.getImage(AtsImage.REPORT));
      setToolTip("Click to run Open Report");
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
            createAndOpenBurndown();
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      };
   };

   private void createAndOpenBurndown() {
      try {
         Response response = AtsClientService.getAgileEndpoint().getSprintBurndownExcel(sprint.getTeamUuid(), sprint.getId());
         if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            JaxBurndownExcel report = response.readEntity(JaxBurndownExcel.class);
            Artifact excelArt = AtsClientService.get().getArtifact(report.getExcelSheetUuid());
            File excelFile = new File("C:/UserData/OSEE_Sprint_Burndown.xls");
            InputStream inputStream = excelArt.getSoleAttributeValue(CoreAttributeTypes.NativeContent, null);
            Lib.inputStreamToFile(inputStream, excelFile);
            File queryFile = new File("C:/UserData/OSEE_Sprint_Burndown.iqy");
            Artifact queryArt = AtsClientService.get().getArtifact(report.getExcelQueryUuid());
            Lib.writeStringToFile(queryArt.getSoleAttributeValueAsString(CoreAttributeTypes.NativeContent, ""),
               queryFile);
            Program.launch(excelFile.getAbsolutePath());
         } else {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Error getting/generating report.");
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
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
