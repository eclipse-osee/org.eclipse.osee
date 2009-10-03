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
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Strings;
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
import org.xml.sax.SAXException;

/**
 * @author Ryan D. Brooks
 */
public abstract class AbstractBlam implements IDynamicWidgetLayoutListener {
   private Appendable output;
   public static final String branchXWidgetXml =
         "<xWidgets><XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Branch\" /></xWidgets>";
   public static final String emptyXWidgetsXml = "<xWidgets/>";

   public abstract void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception;

   /**
    * Return collection of categories that blam belongs to eg: ATS, ATS.Admin, ATS.Report. These will be used to create
    * categories that blams are put into in UI navigators. BLAM can belong in multiple categories.
    * 
    * @return categories
    */
   public abstract Collection<String> getCategories();

   public String getXWidgetsXml() {
      return AbstractBlam.branchXWidgetXml;
   }

   public String getDescriptionUsage() {
      return "Select parameters below and click the play button at the top right.";
   }

   public abstract String getName();

   public void setOutput(Appendable output) {
      this.output = output;
   }

   protected Appendable getOutput() {
      return output;
   }

   public void print(String value) {
      if (this.output != null && value != null) {
         try {
            this.output.append(value);
         } catch (IOException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }
   }

   public void println(String value) {
      if (Strings.isValid(value)) {
         print(value + "\n");
      }
   }

   public void execute(String jobName, Appendable appendable, VariableMap variableMap, JobChangeAdapter jobChangeAdapter) {
      try {
         IOperation blamOperation = new ExecuteBlamOperation(jobName, appendable, variableMap, this);
         Operations.executeAsJob(blamOperation, true, Job.LONG, jobChangeAdapter);
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public List<DynamicXWidgetLayoutData> getLayoutDatas() throws IllegalArgumentException, OseeCoreException, ParserConfigurationException, SAXException, IOException, CoreException {
      return XWidgetParser.extractWorkAttributes(new DynamicXWidgetLayout(), getXWidgetsXml());
   }

   @Override
   public void createXWidgetLayoutData(DynamicXWidgetLayoutData layoutData, XWidget xWidget, FormToolkit toolkit, Artifact art, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
   }

}