/*
 * Created on Nov 29, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.widgets.Composite;

public class XTeamDefinitionCombo extends XComboViewer {
   public static final String WIDGET_ID = XTeamDefinitionCombo.class.getSimpleName();
   private Artifact selectedTeamDef = null;

   public XTeamDefinitionCombo() {
      super("Team Definition");
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);

      try {
         Collection<TeamDefinitionArtifact> teamDefs = TeamDefinitionArtifact.getTeamDefinitions(Active.Active);
         List<TeamDefinitionArtifact> sortedTeamDefs = new ArrayList<TeamDefinitionArtifact>();
         sortedTeamDefs.addAll(teamDefs);
         Collections.sort(sortedTeamDefs);
         getComboViewer().setInput(sortedTeamDefs);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      ArrayList<Object> defaultSelection = new ArrayList<Object>();
      defaultSelection.add("--select--");
      setSelected(defaultSelection);
      addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            selectedTeamDef = (Artifact) getSelected();
         }
      });
   }

   public Artifact getSelectedTeamDef() {
      return selectedTeamDef;
   }

}
