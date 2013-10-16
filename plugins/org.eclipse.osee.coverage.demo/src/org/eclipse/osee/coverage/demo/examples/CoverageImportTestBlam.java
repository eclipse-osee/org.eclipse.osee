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
package org.eclipse.osee.coverage.demo.examples;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.coverage.ICoverageImporter;
import org.eclipse.osee.coverage.blam.AbstractCoverageBlam;
import org.eclipse.osee.coverage.demo.CoverageExampleFactory;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.SimpleCoverageUnitFileContentsProvider;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.XWidgetParser;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class CoverageImportTestBlam extends AbstractCoverageBlam implements ICoverageImporter {

   public static String COVERAGE_IMPORT_DIR = "Coverage Import Directory";
   private final String name;
   private final List<String> fileList;

   public CoverageImportTestBlam(String name, List<String> fileList) {
      this.name = name;
      this.fileList = fileList;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
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
      setCoverageImport(run(monitor));
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("ATS");
   }

   @Override
   public CoverageImport run(IProgressMonitor progressMonitor) throws OseeCoreException {
      CoverageImport coverageImport = new CoverageImport(getName());
      coverageImport.setCoverageUnitFileContentsProvider(new SimpleCoverageUnitFileContentsProvider());

      for (String filename : fileList) {
         URL url = CoverageExampleFactory.getCoverageSource(getClass(), filename);
         CoverageUnit coverageUnit =
            SampleJavaFileParser.createCodeUnit(url, coverageImport.getCoverageUnitFileContentsProvider());
         String namespace = getNamespace(coverageUnit.getNamespace());
         coverageUnit.setNamespace(namespace);
         CoverageUnit parentCoverageUnit = coverageImport.getOrCreateParent(namespace);
         if (parentCoverageUnit != null) {
            parentCoverageUnit.addCoverageUnit(coverageUnit);
         } else {
            coverageImport.addCoverageUnit(coverageUnit);
         }
      }
      coverageImport.setLocation("c:\\");

      return coverageImport;
   }

   private String getNamespace(String namespace) {
      if (namespace.contains("com")) {
         return "com";
      } else if (namespace.contains("epu")) {
         return "epu";
      } else if (namespace.contains("apu")) {
         return "apu";
      } else if (namespace.contains("nav")) {
         return "nav";
      } else {
         return "none";
      }
   }

}