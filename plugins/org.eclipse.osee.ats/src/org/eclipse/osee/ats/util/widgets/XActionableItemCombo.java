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
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemManager;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class XActionableItemCombo extends XComboViewer {
   public static final String WIDGET_ID = XActionableItemCombo.class.getSimpleName();
   private Artifact selectedAi = null;

   public XActionableItemCombo() {
      super("Actionable Item", SWT.READ_ONLY);
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);

      try {
         Collection<ActionableItemArtifact> teamDefs = ActionableItemManager.getActionableItems(Active.Active);
         List<ActionableItemArtifact> sortedAiArts = new ArrayList<ActionableItemArtifact>();
         sortedAiArts.addAll(teamDefs);
         Collections.sort(sortedAiArts);
         getComboViewer().setInput(sortedAiArts);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      ArrayList<Object> defaultSelection = new ArrayList<Object>();
      defaultSelection.add("--select--");
      setSelected(defaultSelection);
      addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            selectedAi = (Artifact) getSelected();
         }
      });
   }

   public Artifact getSelectedAi() {
      return selectedAi;
   }

}
