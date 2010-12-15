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
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.widgets.Composite;

/**
 * Single select of attribute type via combo box
 * 
 * @author Donald G. Dunne
 */
public class XAttributeTypeComboViewer extends XComboViewer {
   public static final String WIDGET_ID = XAttributeTypeComboViewer.class.getSimpleName();
   private AttributeType selectedAttributeType = null;

   public XAttributeTypeComboViewer() {
      super("AttributeType Type");
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);

      try {
         Collection<AttributeType> AttributeTypes = AttributeTypeManager.getAllTypes();
         List<AttributeType> sortedArtifatTypes = new ArrayList<AttributeType>();
         sortedArtifatTypes.addAll(AttributeTypes);
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
            selectedAttributeType = (AttributeType) getSelected();
         }
      });
   }

   public AttributeType getSelectedTeamDef() {
      return selectedAttributeType;
   }

   @Override
   public Object getData() {
      return Arrays.asList(selectedAttributeType);
   }

}
