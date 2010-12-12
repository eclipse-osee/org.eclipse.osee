/*
 * Created on Nov 29, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class XArtifactTypeComboViewer extends XComboViewer {
   public static final String WIDGET_ID = XArtifactTypeComboViewer.class.getSimpleName();
   private ArtifactType selectedArtifactType = null;

   public XArtifactTypeComboViewer() {
      super("Artifact Type");
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);

      try {
         Collection<ArtifactType> artifactTypes = ArtifactTypeManager.getAllTypes();
         List<ArtifactType> sortedArtifatTypes = new ArrayList<ArtifactType>();
         sortedArtifatTypes.addAll(artifactTypes);
         Collections.sort(sortedArtifatTypes);
         getComboViewer().setInput(sortedArtifatTypes);
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      ArrayList<Object> defaultSelection = new ArrayList<Object>();
      defaultSelection.add("--select--");
      setSelected(defaultSelection);
      addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            selectedArtifactType = (ArtifactType) getSelected();
         }
      });
   }

   public ArtifactType getSelectedTeamDef() {
      return selectedArtifactType;
   }

   @Override
   public Object getData() {
      return Arrays.asList(selectedArtifactType);
   }

}
