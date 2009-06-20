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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 */
public interface BlamOperation {
   public static final String emptyXWidgetsXml = "<xWidgets/>";
   public static final String branchXWidgetXml =
         "<xWidgets><XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Branch\" /></xWidgets>";

   public abstract void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception;

   public abstract String getXWidgetsXml();

   public abstract String getDescriptionUsage();

   public abstract String getName();

   public abstract void setOutput(Appendable output);

   public abstract void print(String value);

   public abstract void println(String value);

   /**
    * Return collection of categories that blam belongs to eg: ATS, ATS.Admin, ATS.Report. These will be used to create
    * categories that blams are put into in UI navigators. BLAM can belong in multiple categories.
    * 
    * @return categories
    */
   public abstract Collection<String> getCategories();
}