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

package org.eclipse.osee.framework.ui.skynet.markedit.html;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.markedit.model.ArtOmeData;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.program.Program;

/**
 * @author Donald G. Dunne
 */
public class OmeBrowserListener implements LocationListener {

   private final ArtOmeData omeData;

   public OmeBrowserListener(ArtOmeData omeData) {
      super();
      this.omeData = omeData;
   }

   @Override
   public void changing(LocationEvent event) {
      try {
         String location = event.location;
         if (location.contains("javascript:print") || location.contains("about:blank")) {
            return;
         }
         String cmdStr = location.replaceFirst("about:", "");
         if (Strings.isNumeric(cmdStr)) {
            Artifact artifact =
               ArtifactQuery.getArtifactFromId(ArtifactId.valueOf(cmdStr), omeData.getArtifact().getBranch());
            ArtifactEditor.editArtifact(artifact);
         } else {
            Program.launch(location);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Can't process hyperlink.", ex);
      }
      event.doit = false;
   }

   @Override
   public void changed(LocationEvent event) {
      // do nothing
   }

}
