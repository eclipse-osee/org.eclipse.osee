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
package org.eclipse.osee.ats.operation;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact.VersionReleaseType;
import org.eclipse.osee.ats.util.widgets.XHyperlabelTeamDefinitionSelection;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.search.TeamWorldNewSearchItem;
import org.eclipse.osee.ats.world.search.TeamWorldNewSearchItem.ReleasedOption;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class EditTeamWorkflows extends AbstractBlam {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.VariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(final VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      monitor.beginTask("Search / Edit Team Workflows", IProgressMonitor.UNKNOWN);

      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            try {
               boolean selected = false;
               StringBuffer sb = new StringBuffer();
               Collection<TeamDefinitionArtifact> teamDefs = getSelectedTeamDefinitions();
               if (teamDefs.size() > 0) {
                  sb.append("Team Definitions(s): " + org.eclipse.osee.framework.jdk.core.util.Collections.toString(
                        ",", teamDefs) + " - ");
                  selected = true;
               }
               VersionArtifact verArt = getSelectedVersionArtifact();
               if (verArt != null) {
                  sb.append("Version: " + verArt + " - ");
                  selected = true;
               }
               ReleasedOption releaseOption = getSelectedReleased();
               if (releaseOption != null && releaseOption != ReleasedOption.Both) {
                  sb.append("ReleasedOption: " + releaseOption + " - ");
               }
               User user = variableMap.getUser("Assignee");
               if (user != null) {
                  sb.append("Assignee: " + user + " - ");
                  selected = true;
               }
               boolean includeCompleted = variableMap.getBoolean("Include Completed/Cancelled");
               if (includeCompleted) {
                  sb.append("Include Completed/Cancelled");
               }
               if (!selected) {
                  AWorkbench.popup("ERROR", "You must select at least Team, Version or Assignee.");
                  return;
               }
               if (user != null && includeCompleted) {
                  AWorkbench.popup("ERROR", "Assignee and Include Completed are not compatible selections.");
                  return;
               }
               if (user != null && includeCompleted && verArt == null && teamDefs.size() == 0) {
                  AWorkbench.popup("ERROR", "You must select at least Team or Version with Include Completed.");
                  return;
               }
               WorldEditor.open(new TeamWorldNewSearchItem("Team Workflows", teamDefs, includeCompleted, false, false,
                     verArt, user, releaseOption), SearchType.Search, null, TableLoadOption.NoUI);

            } catch (Exception ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         }
      });
      monitor.done();
   }

   private VersionArtifact getSelectedVersionArtifact() throws OseeCoreException {
      String versionStr = versionCombo.get();
      if (versionStr == null || versionStr.equals("")) return null;
      Collection<TeamDefinitionArtifact> teamDefs = getSelectedTeamDefinitions();
      if (teamDefs.size() > 0) {
         TeamDefinitionArtifact teamDefHoldingVersions = teamDefs.iterator().next().getTeamDefinitionHoldingVersions();
         if (teamDefHoldingVersions == null) return null;
         for (VersionArtifact versionArtifact : teamDefHoldingVersions.getVersionsArtifacts(VersionReleaseType.Both)) {
            if (versionArtifact.getDescriptiveName().equals(versionStr)) {
               return versionArtifact;
            }
         }
      }
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   @Override
   public String getXWidgetsXml() {
      String widgetXml =
            "<xWidgets>" +
            //
            "<XWidget xwidgetType=\"XHyperlabelTeamDefinitionSelection\" displayName=\"Team Definitions(s)\" horizontalLabel=\"true\"/>" +
            //
            "<XWidget xwidgetType=\"XCombo()\" displayName=\"Version\" horizontalLabel=\"true\"/>" +
            //
            "<XWidget xwidgetType=\"XCombo(Both,Released,UnReleased)\" displayName=\"Released\" horizontalLabel=\"true\"/>" +
            //
            "<XWidget xwidgetType=\"XMembersCombo\" displayName=\"Assignee\" horizontalLabel=\"true\"/>" +
            //
            "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Include Completed/Cancelled\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>";
      widgetXml += "</xWidgets>";
      return widgetXml;
   }

   private XHyperlabelTeamDefinitionSelection teamCombo = null;
   private XCombo releasedCombo = null;
   private XCombo versionCombo = null;

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#widgetCreated(org.eclipse.osee.framework.ui.skynet.widgets.XWidget, org.eclipse.ui.forms.widgets.FormToolkit, org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout, org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener, boolean)
    */
   @Override
   public void widgetCreated(XWidget widget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
      super.widgetCreated(widget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (widget.getLabel().equals("Team Definitions(s)")) {
         teamCombo = (XHyperlabelTeamDefinitionSelection) widget;
         teamCombo.addXModifiedListener(new XModifiedListener() {
            @Override
            public void widgetModified(XWidget widget) {
               if (versionCombo != null) {
                  try {
                     Collection<TeamDefinitionArtifact> teamDefArts = getSelectedTeamDefinitions();
                     if (teamDefArts.size() == 0) {
                        versionCombo.setDataStrings(new String[] {});
                        return;
                     }
                     TeamDefinitionArtifact teamDefHoldingVersions =
                           teamDefArts.iterator().next().getTeamDefinitionHoldingVersions();
                     if (teamDefHoldingVersions == null) {
                        versionCombo.setDataStrings(new String[] {});
                        return;
                     }
                     Collection<String> names =
                           Artifacts.artNames(teamDefHoldingVersions.getVersionsArtifacts(VersionReleaseType.Both));
                     if (names.size() == 0) {
                        versionCombo.setDataStrings(new String[] {});
                        return;
                     }
                     versionCombo.setDataStrings(names.toArray(new String[names.size()]));
                  } catch (Exception ex) {
                     OSEELog.logException(AtsPlugin.class, ex, true);
                  }
               }
            }
         });
      }
      if (widget.getLabel().equals("Version")) {
         versionCombo = (XCombo) widget;
         versionCombo.getComboBox().setVisibleItemCount(25);
         widget.getLabelWidget().setToolTipText("Select Team to populate Version list");
      }
      if (widget.getLabel().equals("Released")) {
         releasedCombo = (XCombo) widget;
      }
   }

   private Collection<TeamDefinitionArtifact> getSelectedTeamDefinitions() throws OseeCoreException {
      return teamCombo.getSelectedTeamDefintions();
   }

   private ReleasedOption getSelectedReleased() throws OseeCoreException {
      if (releasedCombo.get() == null || releasedCombo.get().equals("")) {
         return ReleasedOption.Released;
      }
      return ReleasedOption.valueOf(releasedCombo.get());
   }

}