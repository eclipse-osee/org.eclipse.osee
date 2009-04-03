/*
 * Created on Apr 1, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.define.traceability.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.traceability.TestUnit;

/**
 * @author Roberto E. Escobar
 */
public interface ITestUnitProcessor {
   public void initialize(IProgressMonitor monitor);

   public void onComplete(IProgressMonitor monitor);

   public void clear();

   public void process(IProgressMonitor monitor, TestUnit testUnit);
}
