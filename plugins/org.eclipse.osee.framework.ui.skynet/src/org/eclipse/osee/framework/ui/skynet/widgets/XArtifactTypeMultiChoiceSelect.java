/*
 * Created on Dec 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactNameSorter;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.MinMaxOSEECheckedFilteredTreeDialog;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.SimpleCheckFilteredTreeDialog;

/**
 * Multi selection of artifact types with checkbox dialog and filtering
 * 
 * @author Donald G. Dunne
 */
public class XArtifactTypeMultiChoiceSelect extends XSelectFromDialog<ArtifactType> {

   public static final String WIDGET_ID = XArtifactTypeMultiChoiceSelect.class.getSimpleName();

   public XArtifactTypeMultiChoiceSelect() {
      super("Select Artifact Type(s)");
      try {
         setSelectableItems(ArtifactTypeManager.getAllTypes());
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public MinMaxOSEECheckedFilteredTreeDialog createDialog() {
      SimpleCheckFilteredTreeDialog dialog =
         new SimpleCheckFilteredTreeDialog(getLabel(), "Select from the items below", new ArrayTreeContentProvider(),
            new LabelProvider(), new ArtifactNameSorter(), 1, 1000);
      return dialog;
   }

}
