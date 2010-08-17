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
package org.eclipse.osee.framework.ui.skynet.panels;

import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

/**
 * @author Donald G. Dunne
 */
public class DetailsBrowserComposite extends BrowserComposite {

   /**
    * @param parent
    * @param style
    */
   public DetailsBrowserComposite(Artifact artifact, Composite parent, int style, ToolBar toolBar) {
      super(parent, style, toolBar);
      StringBuilder sb = new StringBuilder();
      addData(sb, "Name", artifact.getName());
      try {
         addData(sb, "GUID", artifact.getGuid());
         addData(sb, "HRID", artifact.getHumanReadableId());
         addData(sb, "Branch", artifact.getBranch().toString());
         addData(sb, "Branch Id", String.valueOf(artifact.getBranch().getId()));
         addData(sb, "Artifact Id", String.valueOf(artifact.getArtId()));
         addData(sb, "Artifact Type Name", artifact.getArtifactTypeName());
         addData(sb, "Artifact Type Id", String.valueOf(artifact.getArtTypeId()));
         addData(sb, "Gamma Id", String.valueOf(artifact.getGammaId()));
         addData(sb, "Historical", String.valueOf(artifact.isHistorical()));
         addData(sb, "Deleted", String.valueOf(artifact.isDeleted()));
         addData(sb, "Revision", String.valueOf(artifact.getTransactionNumber()));
         addData(sb, "Last Modified", String.valueOf(artifact.getLastModified()));
         addData(sb, "Last Modified By", String.valueOf(artifact.getLastModifiedBy()));
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         sb.append(AHTML.getLabelStr("Exception in rendering details: ", ex.getLocalizedMessage()));
      }
      setHtml(AHTML.simplePage(sb.toString()));
   }

   private void addData(StringBuilder buffer, String label, String value) {
      buffer.append(AHTML.getLabelValueStr(label, value));
      buffer.append(AHTML.newline());
   }
}
