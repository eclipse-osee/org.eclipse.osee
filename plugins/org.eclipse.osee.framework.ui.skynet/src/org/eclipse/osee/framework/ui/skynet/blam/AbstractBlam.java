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

package org.eclipse.osee.framework.ui.skynet.blam;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
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
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.XWidgetParser;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IDynamicWidgetLayoutListener;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetPage;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;
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
   protected ArtifactToken viewId = ArtifactToken.SENTINEL;

   /**
    * Where Blam XML UI comes from
    */
   public enum BlamUiSource {
      DEFAULT, // specified by Blam's getXWidgetsXml() method
      FILE; // stored in OSEE-INF/blamUI/<blam-name>UI.xml file
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
   protected VariableMap variableMap;

   public AbstractBlam() {
      this(null, DEFAULT_DESCRIPTION, BlamUiSource.DEFAULT);
   }

   public AbstractBlam(String name, String usageDescription, BlamUiSource source) {
      this.name = name;
      this.description = Strings.isValid(usageDescription) ? usageDescription : DEFAULT_DESCRIPTION;
      this.source = source != null ? source : BlamUiSource.DEFAULT;
   }

   /**
    * Override to limit view by user group(s)
    */
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return Collections.emptyList();
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
      this.variableMap = variableMap;
      throw new OseeStateException(
         "either runOperation or createOperation but be overriden by subclesses of AbstractBlam");
   }

   public IOperation createOperation(VariableMap variableMap, OperationLogger logger) throws Exception {
      return new ExecuteBlamOperation(this, variableMap, logger);
   }

   /**
    * Use WidgetBuilder
    */
   @Deprecated
   public String getXWidgetsXml() {
      switch (source) {
         case FILE:
            return getXWidgetsXmlFromUiFile(getClass().getSimpleName(), getClass());
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

   public List<XWidgetRendererItem> getXWidgetItems() {
      return Collections.emptyList();
   }

   /**
    * Expects the {@code <className>} of blam. Gets {@code /bundleName/ui/<className>Ui.xml } and returns its contents.
    *
    * @param className class name of blam
    * @param nameOfBundle name of bundle i.e. org.eclipse.rcp.xyz
    * @return contents of the {@code /bundleName/ui/<className>Ui.xml } usually {@link IOException} or
    * {@link NullPointerException} wrapped in {@link OseeCoreException}
    */
   public String getXWidgetsXmlFromUiFile(String className, Class<?> clazz) {
      String uiFileName = String.format("blamUi/%sUi.xml", className);
      return OseeInf.getResourceContents(uiFileName, clazz);
   }

   public String getDescriptionUsage() {
      return this.description;
   }

   public String getName() {
      return Strings.isValid(name) ? name : generateNameFromClass();
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

   protected void isValidEntry(XResultData rd) {
      // For subclass validation of widgets prior to execution.  rd.error to log error that will be shown and stop execution.
   }

   /**
    * Execution of blam after isValidEntry is called and comes back successful
    *
    * @param variableMap is populated from widget label/name and getData() calls to widgets.
    */
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
      List<XWidgetRendererItem> xWidgetItems = getXWidgetItems();
      if (xWidgetItems.isEmpty()) {
         return XWidgetParser.extractWorkAttributes(new SwtXWidgetRenderer(), getXWidgetsXml());
      } else {
         return xWidgetItems;
      }
   }

   public String getRunText() {
      return "Run BLAM";
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
         if (excludedArtifactIdMap.contains(ArtifactId.create(artifact))) {
            iter.remove();
         }
      }
   }

   public void setViewId(Object view) {
      if (branchViews != null) {
         for (Entry<Long, String> entry : branchViews.entrySet()) {
            if (entry.getValue().equals(view)) {
               viewId = ArtifactToken.valueOf(entry.getKey(), entry.getValue());
            }
         }
      }
   }

   public void resetViewId() {
      viewId = ArtifactToken.SENTINEL;
   }

   public String getOutputMessage() {
      return "BLAM has not yet run";
   }

   /**
    * Checks if access is overridden. Currently ONLY used for automated placement in BLAMs section. Override user groups
    * check; eg: for runtime or demo use.
    */
   public boolean isOverrideAccess() {
      return false;
   }

   /**
    * Provided for BLAMs to add widgets after the defined widgets are drawn
    */
   public void createWidgets(Composite parent, IManagedForm iManagedForm, Section section, XWidgetPage widgetPage) {
      // do nothing
   }

   public abstract Collection<XNavItemCat> getCategories();

   /**
    * Override to provide other calculations as to whether this BLAM should show
    */
   public boolean isApplicable() {
      return true;
   }

   public void inputSectionCreated(XWidgetPage widgetPage) {
      // do nothing
   }

}