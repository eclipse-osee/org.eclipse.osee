/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.define.ide.blam.operation;

import java.io.FileWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.TemplateManagerEndpoint;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.publishing.FormatIndicator;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.DoubleHashMap;
import org.eclipse.osee.framework.jdk.core.util.DoubleMap;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.MessageType;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXCheckBoxWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXTextWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.service.component.annotations.Component;

/**
 * BLAM to find a publishing template using the server side template manager. The BLAM generates and displays a text
 * report describing the found publishing template.
 *
 * @author Loren K. Ashley
 */

@Component(service = AbstractBlam.class, immediate = true)
public class PublishingTemplateBlam extends AbstractBlam {

   private static final String BLAM_NAME = "Publishing Template";
   private static final String BLAM_DESC =
      "Finds a Publishing Template using the server side Publishing Template Manager.";
   static final String VARIABLE_ARTIFACT_TYPE_NAME = "Artifact Type Name";
   static final String VARIABLE_BY_OPTIONS = "Request Publishing Template By Options";
   static final String VARIABLE_FORMAT = "Format";
   static final String VARIABLE_GET_AS = "Get As";
   static final String VARIABLE_IDENTIFIER = "Identifier";
   static final String VARIABLE_OPTION = "Option";
   static final String VARIABLE_PRESENTATION_TYPE = "Presentation Type";
   static final String VARIABLE_RENDERER_ID = "Renderer Id";
   private static final String X_WIDGET_GROUP_BY_IDENTIFIER = "byIdentifier";
   private static final String X_WIDGET_GROUP_BY_OPTIONS = "byOptions";
   private static final Map<String, String> XWIDGET_GROUPS = Map.of(VARIABLE_RENDERER_ID, X_WIDGET_GROUP_BY_OPTIONS,
      VARIABLE_ARTIFACT_TYPE_NAME, X_WIDGET_GROUP_BY_OPTIONS, VARIABLE_PRESENTATION_TYPE, X_WIDGET_GROUP_BY_OPTIONS,
      VARIABLE_OPTION, X_WIDGET_GROUP_BY_OPTIONS, VARIABLE_IDENTIFIER, X_WIDGET_GROUP_BY_IDENTIFIER);

   private final DoubleMap<String, Boolean, Color> xWidgetBackgroundColors;
   private final Map<String, XWidget> xWidgets;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public PublishingTemplateBlam() {
      super(BLAM_NAME, CoreImage.PUBLISH);
      this.xWidgets = new HashMap<>();
      this.xWidgetBackgroundColors = new DoubleHashMap<>();
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andStringsSelWidget(VARIABLE_GET_AS,
         Arrays.stream(GetAs.values()).map(GetAs::getSelectionString).collect(Collectors.toList()));
      wb.andStringsSelWidget(VARIABLE_FORMAT,
         Arrays.stream(FormatIndicator.values()).map(FormatIndicator::getFormatName).collect(Collectors.toList()));
      wb.andCheckBoxWidget(VARIABLE_BY_OPTIONS).andDefault(true);

      wb.andLabelWidget("By Option Parameters:");
      wb.andTextWidget(VARIABLE_RENDERER_ID).and(XOption.FILL_VERT).andHeight(22);
      wb.andTextWidget(VARIABLE_ARTIFACT_TYPE_NAME).and(XOption.FILL_VERT).andHeight(22);
      wb.andTextWidget(VARIABLE_PRESENTATION_TYPE).and(XOption.FILL_VERT).andHeight(22);
      wb.andTextWidget(VARIABLE_OPTION).and(XOption.FILL_VERT).andHeight(22);

      wb.andLabelWidget("By Identifier Parameters:");
      wb.andTextWidget(VARIABLE_IDENTIFIER).and(XOption.FILL_VERT).andHeight(22);

      return wb.getXWidgetDatas();
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetSwtRenderer swtXWidgetRenderer,
      XModifiedListener modListener, boolean isEditable) {

      super.widgetCreated(xWidget, toolkit, art, swtXWidgetRenderer, modListener, isEditable);

      String xWidgetLabel = xWidget.getLabel();

      this.xWidgets.put(xWidgetLabel, xWidget);

      if (xWidget instanceof XXTextWidget) {

         XXTextWidget xText = (XXTextWidget) xWidget;
         Color color = xText.getBackground();

         Device device = color.getDevice();
         int red = color.getRed();
         int blue = color.getBlue();
         int green = color.getGreen();

         red = red > 128 ? red / 2 : red * 2;
         blue = blue > 128 ? blue / 2 : blue * 2;
         green = green > 128 ? green / 2 : green * 2;

         Color disabledColor = new Color(device, red, blue, green);

         this.xWidgetBackgroundColors.put(xWidgetLabel, true, color);
         this.xWidgetBackgroundColors.put(xWidgetLabel, false, disabledColor);
      }

      if (VARIABLE_BY_OPTIONS.equals(xWidgetLabel)) {

         final XXCheckBoxWidget byOptionsCheckBox = (XXCheckBoxWidget) xWidget;

         byOptionsCheckBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
               super.widgetSelected(selectionEvent);
               boolean isChecked = byOptionsCheckBox.isChecked();
               PublishingTemplateBlam.this.xWidgets.keySet().forEach(
                  (label) -> PublishingTemplateBlam.this.enableDisableXWidget(label, isChecked));
            }

         });
      }

      this.enableDisableXWidget(xWidgetLabel, true);

   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {

      String getAsSelection = variableMap.getString(VARIABLE_GET_AS);

      String formatName = variableMap.getString(VARIABLE_FORMAT);

      boolean byOptions = variableMap.getBoolean(VARIABLE_BY_OPTIONS);

      GetAs getAs = GetAs.ofSelectionString(getAsSelection).orElse(null);

      if (Objects.isNull(getAs)) {
         throw new OseeArgumentException("Must make a selection for \"Get As\".");
      }

      FormatIndicator formatIndicator = FormatIndicator.ofFormatName(formatName).orElse(null);

      if (Objects.isNull(formatIndicator)) {

         if (getAs.isPublishingTemplate()) {
            throw new OseeArgumentException("Must select a format.");
         } else {
            formatIndicator = FormatIndicator.WORD_ML;
         }

      }

      if (byOptions) {
         this.runByOptions(variableMap, getAs, formatIndicator);
      } else {
         this.runByIdentifier(variableMap, getAs, formatIndicator);
      }
   }

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   /**
    * Enumeration for the "Get As" pull down selector. The BLAM can make the publishing template request for the
    * publishing template or the publishing template status report.
    */

   public enum GetAs {

      PUBLISHING_TEMPLATE("Publishing Template"),

      PUBLISHING_TEMPLATE_STATUS("Publishing Template Status");

      private static final Map<String, GetAs> selectionStrings;

      static {
         selectionStrings = new HashMap<>();
         for (GetAs getAs : GetAs.values()) {
            selectionStrings.put(getAs.selectionString, getAs);
         }
      }

      public static Optional<GetAs> ofSelectionString(String selectionString) {
         return Objects.nonNull(selectionString) ? Optional.ofNullable(
            GetAs.selectionStrings.get(selectionString)) : Optional.empty();
      }

      private @NonNull final String selectionString;

      private GetAs(@NonNull String selectionString) {
         this.selectionString = Conditions.requireNonNull(selectionString);
      }

      public @NonNull String getSelectionString() {
         return this.selectionString;
      }

      public boolean isPublishingTemplate() {
         return this == PUBLISHING_TEMPLATE;
      }
   }

   private void enableDisableXWidget(String xWidgetLabel, boolean byOptionsIsChecked) {

      XWidget xWidget = this.xWidgets.get(xWidgetLabel);
      String group = XWIDGET_GROUPS.get(xWidgetLabel);

      if (Objects.isNull(group)) {
         return;
      }

      boolean enabled = false;

      switch (group) {
         case X_WIDGET_GROUP_BY_OPTIONS:
            enabled = byOptionsIsChecked;
            break;
         case X_WIDGET_GROUP_BY_IDENTIFIER:
            enabled = !byOptionsIsChecked;
            break;
         default:
      }

      xWidget.setEditable(enabled);

      if (!(xWidget instanceof XXTextWidget)) {
         return;
      }

      XXTextWidget xText = (XXTextWidget) xWidget;

      this.xWidgetBackgroundColors.get(xWidgetLabel, enabled).ifPresent(xText::setBackground);
   }

   private void requestPublishingTemplate(GetAs getAs, PublishingTemplateRequest publishingTemplateRequest) {
      try {

         OseeClient oseeClient = ServiceUtil.getOseeClient();
         TemplateManagerEndpoint templateManagerEndpoint = oseeClient.getTemplateManagerEndpoint();
         String result = getAs.isPublishingTemplate() ? templateManagerEndpoint.getPublishingTemplate(
            publishingTemplateRequest).toString() : templateManagerEndpoint.getPublishingTemplateStatus(
               publishingTemplateRequest);

         String fileName = String.format("PUBLISHING_TEMPLATE_BLAM_%s.txt", Lib.getDateTimeString());

         java.io.File file = OseeData.getFile(fileName);

         try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(result);
         }

         Program.launch(file.getAbsolutePath());

      } catch (Exception e) {
         Message message = new Message().title("Publishing Template Request Failed").reasonFollows(e);

         Throwable cause = e.getCause();

         if (Objects.nonNull(cause)) {
            message.reasonFollows("Caused By", cause);
         }

         AWorkbench.popup(MessageType.Informational, "Publishing Template Blam Error",
            "Failed to obtain the requested publishing template.", message.toString(), null,
            AWorkbench.SCROLLING_TEXT_BOX);
      }
   }

   private void runByIdentifier(VariableMap variableMap, GetAs getAs, FormatIndicator formatIndicator)
      throws Exception {

      String identifier = variableMap.getString(VARIABLE_IDENTIFIER);
      if (identifier == null) {
         throw new OseeArgumentException("Must specify an identifier.");
      }
      PublishingTemplateRequest publishingTemplateRequest = new PublishingTemplateRequest(identifier, formatIndicator);
      this.requestPublishingTemplate(getAs, publishingTemplateRequest);
   }

   private void runByOptions(VariableMap variableMap, GetAs getAs, FormatIndicator formatIndicator) throws Exception {

      String rendererId = variableMap.getString(VARIABLE_RENDERER_ID);
      String artifactTypeName = variableMap.getString(VARIABLE_ARTIFACT_TYPE_NAME);
      String presentationType = variableMap.getString(VARIABLE_PRESENTATION_TYPE);
      String option = variableMap.getString(VARIABLE_OPTION);

      if (rendererId == null) {
         throw new OseeArgumentException("Must specify a renderer identifier.");
      }

      if (presentationType == null) {
         throw new OseeArgumentException("Must specify a presentation type.");
      }

      PublishingTemplateRequest publishingTemplateRequest =
         new PublishingTemplateRequest(rendererId, artifactTypeName, presentationType, option, formatIndicator);

      this.requestPublishingTemplate(getAs, publishingTemplateRequest);
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.DEFINE_HEALTH);
   }

   @Override
   public String getDescriptionUsage() {
      return BLAM_DESC;
   }

}
