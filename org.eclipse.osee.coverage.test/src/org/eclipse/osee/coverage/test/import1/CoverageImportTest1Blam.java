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
package org.eclipse.osee.coverage.test.import1;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.coverage.blam.AbstractCoverageBlam;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.XWidgetParser;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class CoverageImportTest1Blam extends AbstractCoverageBlam {

   public static String COVERAGE_IMPORT_DIR = "Coverage Import Directory";

   @Override
   public String getName() {
      return "Test Import 1";
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
   }

   @Override
   public String getXWidgetsXml() {
      return XWidgetParser.EMPTY_WIDGETS;
   }

   @Override
   public String getDescriptionUsage() {
      return "Import from test area.";
   }

   @Override
   public void runOperation(final VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            setCoverageImport(new CoverageImportTest1NavigateItem().run());
         };
      });
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("ATS");
   }

}