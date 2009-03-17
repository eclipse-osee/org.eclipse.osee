/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.commandHandlers.merge;

import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.IParameterValues;
import org.eclipse.core.commands.ParameterValuesException;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchView;

/**
 * @author Jeff C. Phillips
 *
 */
public class BranchIdParameter implements IParameter {
   public String getId() {
      return BranchView.BRANCH_ID;
   }

   public String getName() {
      return "Branch Id";
   }

   public IParameterValues getValues() throws ParameterValuesException {
      throw new ParameterValuesException("Branch View has no parameters", null);
   }

   public boolean isOptional() {
      return false;
   }
}
