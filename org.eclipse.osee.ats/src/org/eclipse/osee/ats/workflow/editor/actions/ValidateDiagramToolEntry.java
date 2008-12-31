/*
 * Created on Dec 31, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.editor.actions;

import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.osee.ats.AtsPlugin;

/**
 * @author Donald G. Dunne
 */
public class ValidateDiagramToolEntry extends ToolEntry {

   /**
    * @param label
    * @param shortDesc
    * @param iconSmall
    * @param iconLarge
    */
   public ValidateDiagramToolEntry() {
      super("Validate Diagram", "Validate", AtsPlugin.getInstance().getImageDescriptor("check.gif"),
            AtsPlugin.getInstance().getImageDescriptor("check.gif"), ValidateDiagramTool.class);
   }

}
