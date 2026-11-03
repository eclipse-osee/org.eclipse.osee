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

import java.util.ArrayList;
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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.builder.XWidgetBuilder;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetPage;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRendererListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Ryan D. Brooks
 */
public abstract class AbstractBlam implements XWidgetSwtRendererListener {

   public static final String DEFAULT_DESCRIPTION =
      "Select parameters below and click the play button at the top right.";
   private final Pattern capitalLetter = Pattern.compile("[A-Z]+[a-z]*");

   /**
    * Produce a short product label from a BLAM's package name, for display in test names/reports. Core OSEE
    * packages ("org.eclipse.osee...") show as "OSEE"; other products show the segment immediately after "osee",
    * uppercased (e.g. "xyz.abc.osee.www" -&gt; "WWW"). Falls back to the first segment uppercased if "osee" is not
    * present.
    */
   public static String getPackageLabel(AbstractBlam blam) {
      return getPackageLabel(blam.getClass().getPackageName());
   }

   /**
    * @see #getPackageLabel(AbstractBlam)
    */
   public static String getPackageLabel(String packageName) {
      if (packageName == null || packageName.isEmpty()) {
         return "";
      }
      if (packageName.startsWith("org.eclipse.osee")) {
         return "OSEE";
      }
      String[] segments = packageName.split("\\.");
      for (int i = 0; i < segments.length - 1; i++) {
         if (segments[i].equals("osee")) {
            return segments[i + 1].toUpperCase();
         }
      }
      return segments[0].toUpperCase();
   }

   protected Set<ArtifactId> excludedArtifactIdMap = new HashSet<>();
   protected Map<Long, String> branchViews;
   protected ArtifactToken viewId = ArtifactToken.SENTINEL;
   protected boolean inDebug = false;
   private OperationLogger logger;
   private final String description = DEFAULT_DESCRIPTION;
   private String name;
   protected VariableMap variableMap = new VariableMap();
   private BlamEditor editor;
   protected XWidgetBuilder wb;
   private OseeImage image = CoreImage.BLAM;
   private boolean pend = false;
   protected Collection<IUserGroupArtifactToken> groups;

   public AbstractBlam() {
      this("", CoreImage.BLAM);
      this.name = generateNameFromClass();
   }

   public AbstractBlam(String name) {
      this(name, CoreImage.BLAM);
   }

   public AbstractBlam(String name, OseeImage image) {
      this.name = name;
      this.image = image;
   }

   protected XWidgetBuilder createWidgetBuilder() {
      wb = new XWidgetBuilder();
      return wb;
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

   public List<XWidgetData> getXWidgetItems() {
      if (wb != null) {
         return wb.getXWidgetDatas();
      }
      return Collections.emptyList();
   }

   public String getDescriptionUsage() {
      return this.description;
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

   public Collection<XWidget> getBlamWidgets() {
      return editor.getBlamXWidgets();
   }

   public String getRunText() {
      return Strings.truncate(String.format("Run [%s]", getName()), 60, true);
   }

   public String getTitle() {
      return getName();
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

   public void setEditor(BlamEditor editor) {
      this.editor = editor;
   }

   public BlamEditor getEditor() {
      return editor;
   }

   protected void logException(Exception ex) {
      log(Lib.exceptionToString(ex));
   }

   protected boolean showTopPlayButton() {
      return true;
   }

   public boolean isInDebug() {
      return inDebug;
   }

   public void setInDebug(boolean inDebug) {
      this.inDebug = inDebug;
   }

   protected void handleInDebugAfterExecution() {
      // for subclass implementation
   }

   /**
    * @return true if blam supports a debug mode. Used to capture and report results (eg: performance metrics) and
    * process them in implemented handleInDebugAfterExecution.
    */
   public boolean isDebugRunAvailable() {
      return false;
   }

   public XWidget getXWidget(WidgetId widgetId) {
      return editor.getXWidget(widgetId);
   }

   public String getName() {
      return name;
   }

   public OseeImage getImage() {
      return image;
   }

   public VariableMap getVariableMap() {
      return variableMap;
   }

   public boolean isPend() {
      return pend;
   }

   public void setPend(boolean pend) {
      this.pend = pend;
   }

   // Limit usage to UserGroups
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      if (groups == null) {
         groups = new ArrayList<>();
      }
      return groups;
   }

   @Override
   public String toString() {
      return getName();
   }
}
