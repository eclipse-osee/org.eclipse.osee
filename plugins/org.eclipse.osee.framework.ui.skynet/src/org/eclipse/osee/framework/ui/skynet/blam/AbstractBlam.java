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

package org.eclipse.osee.framework.ui.skynet.blam;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.XWidgetParser;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IDynamicWidgetLayoutListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.framework.Bundle;
import org.xml.sax.SAXException;

/**
 * @author Ryan D. Brooks
 */
public abstract class AbstractBlam implements IDynamicWidgetLayoutListener {
   public static final String branchXWidgetXml =
      "<xWidgets><XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Branch\" /></xWidgets>";
   public static final String emptyXWidgetsXml = "<xWidgets/>";
   protected IOseeDatabaseService databaseService;
   private OperationLogger logger;

   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      throw new OseeStateException(
         "either runOperation or createOperation but be overriden by subclesses of AbstractBlam");
   }

   public IOperation createOperation(VariableMap variableMap, OperationLogger logger) throws Exception {
      return new ExecuteBlamOperation(this, variableMap, logger);
   }

   /**
    * Return collection of categories that blam belongs to eg: ATS, ATS.Admin, ATS.Report. These will be used to create
    * categories that blams are put into in UI navigators. BLAM can belong in multiple categories.
    */
   public abstract Collection<String> getCategories();

   public String getXWidgetsXml() throws OseeCoreException {
      return AbstractBlam.branchXWidgetXml;
   }

   /**
    * Expects the {@code <className>} of blam. Gets {@code /bundleName/ui/<className>Ui.xml } and returns its contents.
    * 
    * @param className class name of blam
    * @param nameOfBundle name of bundle i.e. org.eclipse.rcp.xyz
    * @return contents of the {@code /bundleName/ui/<className>Ui.xml }
    * @throws OseeCoreException usually {@link IOException} or {@link NullPointerException} wrapped in
    * {@link OseeCoreException}
    */
   public String getXWidgetsXmlFromUiFile(String className, String nameOfBundle) throws OseeCoreException {
      String file = String.format("ui/%sUi.xml", className);
      Bundle bundle = Platform.getBundle(nameOfBundle);

      String contents = null;
      try {
         InputStream inStream = bundle.getEntry(file).openStream();
         contents = Lib.inputStreamToString(inStream);
      } catch (IOException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }

      return contents;
   }

   public String getDescriptionUsage() {
      return "Select parameters below and click the play button at the top right.";
   }

   public String getName() {
      return getClass().getSimpleName();
   }

   public void setOseeDatabaseService(IOseeDatabaseService service) {
      databaseService = service;
   }

   public void report(String... row) {
      logger.log(row);
   }

   public void execute(OperationLogger logger, VariableMap variableMap, IJobChangeListener jobChangeListener) {
      try {
         this.logger = logger;
         IOperation blamOperation = createOperation(variableMap, logger);
         Operations.executeAsJob(blamOperation, true, Job.LONG, jobChangeListener);
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @SuppressWarnings("unused")
   public List<DynamicXWidgetLayoutData> getLayoutDatas() throws IllegalArgumentException, ParserConfigurationException, SAXException, IOException, CoreException {
      return XWidgetParser.extractWorkAttributes(new DynamicXWidgetLayout(), getXWidgetsXml());
   }

   @SuppressWarnings("unused")
   @Override
   public void createXWidgetLayoutData(DynamicXWidgetLayoutData layoutData, XWidget xWidget, FormToolkit toolkit, Artifact art, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
      // provided for subclass implementation
   }

   @SuppressWarnings("unused")
   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
      // provided for subclass implementation
   }

   @SuppressWarnings("unused")
   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
      // provided for subclass implementation
   }

}