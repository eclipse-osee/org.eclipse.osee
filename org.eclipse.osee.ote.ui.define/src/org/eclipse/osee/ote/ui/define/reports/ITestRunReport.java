/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
