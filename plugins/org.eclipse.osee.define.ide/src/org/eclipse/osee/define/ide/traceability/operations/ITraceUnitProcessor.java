/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.define.ide.traceability.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.ide.traceability.data.TraceUnit;

/**
 * @author Roberto E. Escobar
 */
public interface ITraceUnitProcessor {

   public void initialize(IProgressMonitor monitor);

   public void onComplete(IProgressMonitor monitor);

   public void clear();

   public void process(IProgressMonitor monitor, TraceUnit testUnit);
}
