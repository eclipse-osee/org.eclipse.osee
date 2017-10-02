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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.XWidgetParser;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IDynamicWidgetLayoutListener;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.osgi.framework.Bundle;
import org.xml.sax.SAXException;

/**
 * @author Ryan D. Brooks
 */
public abstract class AbstractBlam implements IDynamicWidgetLayoutListener {

   private static final String DEFAULT_DESCRIPTION =
      "Select parameters below and click the play button at the top right.";
   private final static String titleEnd = " BLAM";
   private final Pattern capitalLetter = Pattern.compile("[A-Z]+[a-z]*");

   protected Set<ArtifactId> excludedArtifactIdMap = new HashSet<>();
   protected Map<Long, String> branchViews;
   protected ArtifactId viewId = ArtifactId.SENTINEL;

   public enum BlamUiSource {
      DEFAULT,
      FILE
   }

   public static final String BRANCH_VIEW = "Branch View";
   public static final String branchXWidgetXml =
      "<xWidgets><XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Branch\" /></xWidgets>";
   public static final String BRANCH_VIEW_WIDGET =
      "<XWidget xwidgetType=\"XCombo()\" displayName=\"Branch View\" horizontalLabel=\"true\"/>";
   public static final String emptyXWidgetsXml = "<xWidgets/>";
   private OperationLogger logger;

   private final String description;
   private final BlamUiSource source;
   private final String name;

   public AbstractBlam() {
      this(null, DEFAULT_DESCRIPTION, BlamUiSource.DEFAULT);
   }

   public AbstractBlam(String name, String usageDescription, BlamUiSource source) {
      this.name = Strings.isValid(name) ? name : generateNameFromClass();
      this.description = Strings.isValid(usageDescription) ? usageDescription : DEFAULT_DESCRIPTION;
      this.source = source != null ? source : BlamUiSource.DEFAULT;
   }

   private String generateNameFromClass() {
      String className = getClass().getSimpleName();
      StringBuilder generatedName = new StringBuilder(className.length() + 7);

      Matcher capMatch = capitalLetter.matcher(className);
      for (boolean found = capMatch.find(); found || !capMatch.hitEnd(); found = capMatch.find()) {
         generatedName.append(capMatch.start() > 0 ? " " + capMatch.group() : capMatch.group());
      }
      return generatedName.toString();
   }

   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      throw new OseeStateException(
         "either runOperation or createOperation but be overriden by subclesses of AbstractBlam");
   }

   public IOperation createOperation(VariableMap variableMap, OperationLogger logger) throws Exception {
      return new ExecuteBlamOperation(this, variableMap, logger);
   }

   /**
    * Return collection of categories that blam belongs to. These will be used to create categories that blams are put
    * into in UI navigators. BLAM can belong in multiple categories.
    */
   public abstract Collection<String> getCategories();

   public String getXWidgetsXml()  {
      switch (source) {
         case FILE:
            return getXWidgetsXmlFromUiFile(getClass().getSimpleName(), Activator.PLUGIN_ID);
         case DEFAULT:
         default:
            StringBuilder sb = new StringBuilder();
            sb.append("<xWidgets>");
            sb.append(branchXWidgetXml);
            sb.append(BRANCH_VIEW_WIDGET);
            sb.append("</xWidgets>");
            return sb.toString();
      }
   }

   /**
    * Expects the {@code <className>} of blam. Gets {@code /bundleName/ui/<className>Ui.xml } and returns its contents.
    *
    * @param className class name of blam
    * @param nameOfBundle name of bundle i.e. org.eclipse.rcp.xyz
    * @return contents of the {@code /bundleName/ui/<className>Ui.xml }
    *  usually {@link IOException} or {@link NullPointerException} wrapped in
    * {@link OseeCoreException}
    */
   public String getXWidgetsXmlFromUiFile(String className, String nameOfBundle)  {
      String file = String.format("ui/%sUi.xml", className);
      Bundle bundle = Platform.getBundle(nameOfBundle);

      String contents = null;
      InputStream inStream = null;
      try {
         inStream = bundle.getEntry(file).openStream();
         contents = Lib.inputStreamToString(inStream);
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      } finally {
         Lib.close(inStream);
      }

      return contents;
   }

   public String getDescriptionUsage() {
      return this.description;
   }

   public String getName() {
      return name;
   }

   public void log(String... row) {
      if (logger != null) {
         logger.log(row);
      }
   }

   public void log(Throwable th) {
      if (logger != null) {
         logger.log(th);
      }
   }

   public void logf(String format, Object... args) {
      if (logger != null) {
         logger.logf(format, args);
      }
   }

   public void execute(OperationLogger logger, VariableMap variableMap, IJobChangeListener jobChangeListener) {
      try {
         this.logger = logger;
         IOperation blamOperation = createOperation(variableMap, logger);
         Operations.executeAsJob(blamOperation, true, Job.LONG, jobChangeListener);
      } catch (Exception ex) {
         log(ex);
      }
   }

   @SuppressWarnings("unused")
   public List<XWidgetRendererItem> getLayoutDatas() throws IllegalArgumentException, ParserConfigurationException, SAXException, IOException, CoreException {
      return XWidgetParser.extractWorkAttributes(new SwtXWidgetRenderer(), getXWidgetsXml());
   }

   public String getRunText() {
      return "Run BLAM in Job";
   }

   public Image getImage() {
      return ImageManager.getImage(FrameworkImage.BLAM);
   }

   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.BLAM);
   }

   public String getTitle() {
      return getName().toLowerCase().contains(titleEnd.toLowerCase().trim()) ? getName() : getName() + titleEnd;
   }

   public void addWidgets(IManagedForm managedForm, FormEditor editor, Composite sectionBody)  {
      // provided for subclass implementation
   }

   public boolean showUsageSection() {
      return true;
   }

   public boolean showExecuteSection() {
      return true;
   }

   public String getTabTitle() {
      return "BLAM Workflow";
   }

   public void excludeArtifacts(Iterator<Artifact> iter) {
      while (iter.hasNext()) {
         Artifact artifact = iter.next();
         if (excludedArtifactIdMap.contains(ArtifactId.valueOf(artifact.getId()))) {
            iter.remove();
         }
      }
   }

   public void setViewId(Object view) {
      if (branchViews != null) {
         for (Entry<Long, String> entry : branchViews.entrySet()) {
            if (entry.getValue().equals(view)) {
               viewId = ArtifactId.valueOf(entry.getKey());
            }
         }
      }
   }

   public void resetViewId() {
      viewId = ArtifactId.SENTINEL;
   }

}