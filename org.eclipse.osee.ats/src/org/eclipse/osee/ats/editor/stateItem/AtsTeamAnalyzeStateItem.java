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
package org.eclipse.osee.ats.editor.stateItem;

import java.sql.SQLException;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.editor.AtsStateItem;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XFloatDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class AtsTeamAnalyzeStateItem extends AtsStateItem {

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#getId()
    */
   public String getId() {
      return "osee.ats.defaultTeam.Analyze";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#xWidgetCreated(org.eclipse.osee.framework.ui.skynet.widgets.XWidget,
    *      org.eclipse.ui.forms.widgets.FormToolkit, org.eclipse.osee.ats.workflow.AtsWorkPage,
    *      org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener, boolean)
    */
   public void xWidgetCreated(XWidget xWidget, FormToolkit toolkit, AtsWorkPage page, Artifact art, XModifiedListener xModListener, boolean isEditable) {
      if (xWidget.getLabel().contains(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getDisplayName())) {
         XFloatDam estimatedHrsFloat = (XFloatDam) xWidget;
         SMAManager smaMgr = new SMAManager((StateMachineArtifact) art);
         try {
            if (smaMgr.getSma().isMetricsFromTasks()) {
               estimatedHrsFloat.setEnabled(false);
            }
         } catch (SQLException ex) {
            OSEELog.logException(AtsPlugin.class, ex, true);
         }
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#xWidgetCreating(org.eclipse.osee.framework.ui.skynet.widgets.XWidget,
    *      org.eclipse.ui.forms.widgets.FormToolkit, org.eclipse.osee.ats.workflow.AtsWorkPage,
    *      org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener, boolean)
    */
   public Result xWidgetCreating(XWidget xWidget, FormToolkit toolkit, AtsWorkPage page, Artifact art, XModifiedListener xModListener, boolean isEditable) {
      if (xWidget.getLabel().equals(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getDisplayName())) {
         XFloatDam estimatedHrsFloat = (XFloatDam) xWidget;
         SMAManager smaMgr = new SMAManager((StateMachineArtifact) art);
         try {
            if (smaMgr.getSma().isMetricsFromTasks()) {
               estimatedHrsFloat.setLabel("Estimated Hours (Set from Tasks): " + smaMgr.getSma().getWorldViewEstimatedHoursStr());
               estimatedHrsFloat.setRequiredEntry(false);
            }
         } catch (SQLException ex) {
            OSEELog.logException(AtsPlugin.class, ex, true);
         }
      }
      return Result.TrueResult;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#getDescription()
    */
   public String getDescription() {
      return "AtsTeamAnalyzeStateItem - Modify hours widget based on MetricsFromTasks selection.";
   }

}
