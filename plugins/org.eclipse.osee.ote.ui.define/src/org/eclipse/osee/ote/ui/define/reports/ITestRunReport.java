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

package org.eclipse.osee.ote.ui.define.reports;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ote.define.artifacts.TestRunOperator;

/**
 * @author Roberto E. Escobar
 */
public interface ITestRunReport {

   public static final String ONE_SPACE_STRING = " ";
   public static final String EMPTY_STRING = "";
   public static final String FLOAT_TYPE = "float";

   public void gatherData(IProgressMonitor monitor, TestRunOperator... artifacts) throws Exception;

   public String getTitle();

   public String[] getHeader();

   public String[][] getBody();

   public String getDescription();

   public void clear();
}
