package org.eclipse.osee.framework.ui.skynet.dbHealth;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Jeff C. Phillips
 */
public abstract class DatabaseHealthTask {

   public enum Operation {
      Verify, Fix;
   }

   public abstract String getVerifyTaskName();

   public abstract String getFixTaskName();

   public abstract void run(BlamVariableMap variableMap, IProgressMonitor monitor, Operation operation, StringBuilder builder, boolean showDetails) throws Exception;
}
