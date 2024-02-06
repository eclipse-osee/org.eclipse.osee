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
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.publishing.FormatIndicator;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.DoubleHashMap;
import org.eclipse.osee.framework.jdk.core.util.DoubleMap;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.builder.XWidgetBuilder;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * BLAM to find a publishing template using the server side template manager. The BLAM generates and displays a text
 * report describing the found publishing template.
 *
 * @author Loren K. Ashley
 */

public class PublishingTemplateBlam extends AbstractBlam {

   /**
    * Enumeration for the "Get As" pull down selector. The BLAM can make the publishing template request for the
    * publishing template or the publishing template status report.
    */

   public enum GetAs {

      /**
       * Member to indicate the selection was made for a publishing template.
       */

      PUBLISHING_TEMPLATE("Publishing Template"),

      /**
       * Member to indicate the selection was made for a publishing template status report.
       */

      PUBLISHING_TEMPLATE_STATUS("Publishing Template Status");

      /**
       * Saves the selection strings used in the BLAM GUI.
       */

      private static final Map<String, GetAs> selectionStrings;

      static {
         selectionStrings = new HashMap<>();
         for (var getAs : GetAs.values()) {
            selectionStrings.put(getAs.selectionString, getAs);
         }
      }

      /**
       * Get the GetAs member associated with the provided GUI selection string. When the user has not made a selection
       * the <code>selectionString</code> will be "--select--" and this will result in an empty {@link Optional} being
       * returned.
       *
       * @param selectionString the selection from the "Get As" selector.
       * @return when the selection string matches a member an {@link Optional} with the matching {@link GetAs} member;
       * otherwise, an empty {@link Optional}.
       */

      public static Optional<GetAs> ofSelectionString(String selectionString) {
         //@formatter:off
         return
            Objects.nonNull( selectionString )
               ? Optional.ofNullable( GetAs.selectionStrings.get( selectionString ) )
               : Optional.empty();
         //@formatter:on
      }

      /**
       * Save the selection string displayed in the "Get As" pull down selector.
       */

      private @NonNull String selectionString;

      /**
       * Creates a new {@link GetAs} enumeration member.
       *
       * @param selectionString the associated GUI selection string.
       */

      private GetAs(@NonNull String selectionString) {
         this.selectionString = Conditions.requireNonNull(selectionString);
      }

      /**
       * Gets the associate GUI selection string for the member.
       *
       * @return the GUI selection string.
       */

      public @NonNull String getSelectionString() {
         return this.selectionString;
      }

      /**
       * Predicate to determine if the member is {@link #PUBLISHING_TEMPLATE}.
       *
       * @return <code>true</code> when the member is {@link #PUBLISHING_TEMPLATE}; otherwise, <code>false</code>.
       */

      public boolean isPublishingTemplate() {
         return this == PUBLISHING_TEMPLATE;
      }
   }

   /**
    * Description string for the BLAM
    */

   private static final String blamDescription =
      "Finds a Publishing Template using the server side Publishing Template Manager.";

   /**
    * Name string for the BLAM
    */

   private static final String blamName = "Publishing Template BLAM";

   /**
    * BLAM XWidget variable name and title for the template selection parameter &quot;Artifact Type Name&quot;.
    */

   private static final String variableArtifactTypeName = "Artifact Type Name";

   /**
    * BLAM XWidget variable name and title for a check box to select between using match criteria or an identifier to
    * find the publishing template.
    */

   private static final String variableByOptions = "Request Publishing Template By Options";

   /**
    * BLAM XWidget variable name and title for a combo box to select the desired format for the publishing template.
    */

   private static final String variableFormat = "Format";

   /**
    * BLAM XWidget variable name and title for the selection box to get the template or the template status.
    */

   private static final String variableGetAs = "Get As";

   /**
    * BLAM XWidget variable name and title for the template selection parameter &quot;Identifier&quot;.
    */

   private static final String variableIdentifier = "Identifier";

   /**
    * BLAM XWidget variable name and title for the template selection parameter &quot;Option&quot;.
    */

   private static final String variableOption = "Option";

   /**
    * BLAM XWidget variable name and title for the template selection parameter &quot;Presentation Type&quot;.
    */

   private static final String variablePresentationType = "Presentation Type";

   /**
    * BLAM XWidget variable name and title for the template selection parameter &quot;Renderer Id&quot;.
    */

   private static final String variableRendererId = "Renderer Id";

   /**
    * The group name for XWidgets that are enabled when the {@link PublishingTemplateBlam#variableByOptions
    * variableByOptions} check box is unchecked.
    */

   private static final String xWidgetGroupByIdentifier = "byIdentifier";

   /**
    * The group name for XWidgets that are enabled when the {@link PublishingTemplateBlam#variableByOptions
    * variableByOptions} check box is checked.
    */

   private static final String xWidgetGroupByOptions = "byOptions";

   /**
    * BLAM XWidgets that are enabled and disabled by the {@link PublishingTemplateBlam#variableByOptions
    * variableByOptions} check box are assigned to either the PublishingTemplateBlam.xWidgetGroupByOptions or the
    * "byIdentifier" groups.
    */

   //@formatter:off
   private static Map<String, String> xwidgetGroups =
      Map.of
         (
            PublishingTemplateBlam.variableRendererId,       PublishingTemplateBlam.xWidgetGroupByOptions,
            PublishingTemplateBlam.variableArtifactTypeName, PublishingTemplateBlam.xWidgetGroupByOptions,
            PublishingTemplateBlam.variablePresentationType, PublishingTemplateBlam.xWidgetGroupByOptions,
            PublishingTemplateBlam.variableOption,           PublishingTemplateBlam.xWidgetGroupByOptions,
            PublishingTemplateBlam.variableIdentifier,       PublishingTemplateBlam.xWidgetGroupByIdentifier
         );
   //@formatter:on

   /**
    * A map of the enabled XWidget background colors by the XWidget name and enabled/disabled status.
    */

   private final DoubleMap<String, Boolean, Color> xWidgetBackgroundColors;

   /**
    * A map of the BLAM's {@link XWidget}s by their names.
    */

   private final Map<String, XWidget> xWidgets;

   /**
    * Creates a new {@link PublishingTemplateBlam} instance for finding publishing templates with the server side
    * template manager.
    */

   public PublishingTemplateBlam() {
      super(PublishingTemplateBlam.blamName, PublishingTemplateBlam.blamDescription, null);
      this.xWidgets = new HashMap<>();
      this.xWidgetBackgroundColors = new DoubleHashMap<>();
   }

   /**
    * Enables or disables a BLAM {@link XWidget} based upon the "By Options" check box and the {@link XWidget}'s
    * assigned group.
    *
    * @param xWidgetLabel the name of the {@link XWidget} to enable or diable.
    * @param byOptionsIsChecked <code>true</code> when the "By Options" check box is checked; otherwise
    * <code>false</code>.
    */

   private void enableDisableXWidget(String xWidgetLabel, boolean byOptionsIsChecked) {

      var xWidget = this.xWidgets.get(xWidgetLabel);
      var group = PublishingTemplateBlam.xwidgetGroups.get(xWidgetLabel);

      if (Objects.isNull(group)) {
         return;
      }

      var enabled = false;

      switch (group) {
         case PublishingTemplateBlam.xWidgetGroupByOptions:
            enabled = byOptionsIsChecked;
            break;
         case PublishingTemplateBlam.xWidgetGroupByIdentifier:
            enabled = !byOptionsIsChecked;
            break;
         default:
      }

      xWidget.setEditable(enabled);

      if (!(xWidget instanceof XText)) {
         return;
      }

      var xText = (XText) xWidget;

      this.xWidgetBackgroundColors.get(xWidgetLabel, enabled).ifPresent(xText::setBackground);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.DEFINE_HEALTH);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public List<XWidgetRendererItem> getXWidgetItems() {
      //@formatter:off
      return
         new XWidgetBuilder()

                .andXCombo
                   (
                      PublishingTemplateBlam.variableGetAs,
                      Arrays.stream( GetAs.values() )
                         .map( GetAs::getSelectionString )
                         .collect( Collectors.toList() )
                   )
                .endWidget()

                .andXCombo
                   (
                      PublishingTemplateBlam.variableFormat,
                      Arrays.stream( FormatIndicator.values() )
                         .map( FormatIndicator::getFormatName )
                         .collect( Collectors.toList() )
                   )
                .endWidget()

                .andWidget( PublishingTemplateBlam.variableByOptions, "XCheckBox" )
                .andDefault(true)
                .endWidget()

                .andWidget( "By Option Parameters:", "XLabel" )
                .endWidget()

                .andWidget( PublishingTemplateBlam.variableRendererId, "XText" )
                .andFillVertically()
                .andHeight(22)
                .endWidget()

                .andWidget( PublishingTemplateBlam.variableArtifactTypeName, "XText" )
                .andFillVertically()
                .andHeight(22)
                .endWidget()

                .andWidget( PublishingTemplateBlam.variablePresentationType, "XText" )
                .andFillVertically()
                .andHeight(22)
                .endWidget()

                .andWidget( PublishingTemplateBlam.variableOption, "XText" )
                .andFillVertically()
                .andHeight(22)
                .endWidget()

                .andWidget( "By Identifier Parameters:", "XLabel" )
                .endWidget()

                .andWidget( PublishingTemplateBlam.variableIdentifier, "XText" )
                .andFillVertically()
                .andHeight(22)
                .endWidget()

                .getItems();
      //@formatter:on
   }

   /**
    * Requests the publishing template specified by the BLAM entries. When the publishing template fails to load, an
    * error pop-up is displayed with the error.
    *
    * @param GetAs indicates if a publishing template or publishing template status report is being requested.
    * @param publishingTemplateRequest the {@link PublishingTemplateRequest} built from the BLAM entries.
    */

   private void requestPublishingTemplate(GetAs getAs, PublishingTemplateRequest publishingTemplateRequest) {
      try {

         var oseeClient = ServiceUtil.getOseeClient();
         var templateManagerEndpoint = oseeClient.getTemplateManagerEndpoint();
         //@formatter:off
         var result =
            getAs.isPublishingTemplate()
               ? templateManagerEndpoint.getPublishingTemplate(publishingTemplateRequest).toString()
               : templateManagerEndpoint.getPublishingTemplateStatus(publishingTemplateRequest);

         var fileName =
            "PUBLISHING_TEMPLATE_BLAM_" + Lib.getDateTimeString() + ".txt";

         var file = OseeData.getFile(fileName);

         try (var fileWriter = new FileWriter(file)) {
            fileWriter.write(result);
         }

         Program.launch(file.getAbsolutePath());

      } catch (Exception e) {
         //@formatter:off
         var message =
            new Message()
            .title( "Publishing Template Request Failed" )
            .reasonFollows( e );
         //@formatter:on

         var cause = e.getCause();

         if (Objects.nonNull(cause)) {
            message.reasonFollows("Caused By", cause);
         }

         AWorkbench.popup(message.toString());
      }
   }

   /**
    * Builds the {@link PublishingTemplateRequest} for a request by identifier when the "By Options" check box is
    * unchecked.
    *
    * @param variableMap the {@link VariableMap} of the BLAM {@link XWidget} values.
    * @param GetAs indicates if a publishing template or publishing template status report is being requested.
    * @param formatIndicator the selected publishing format.
    */

   private void runByIdentifier(VariableMap variableMap, GetAs getAs, FormatIndicator formatIndicator) throws Exception {

      var identifier = variableMap.getString(PublishingTemplateBlam.variableIdentifier);
      if (identifier == null) {
         throw new OseeArgumentException("Must specify an identifier.");
      }
      var publishingTemplateRequest = new PublishingTemplateRequest(identifier, formatIndicator);
      this.requestPublishingTemplate(getAs, publishingTemplateRequest);
   }

   /**
    * Builds the {@link PublishingTemplateRequest} for a request by match criteria when the "By Options" check box is
    * checked.
    *
    * @param variableMap the {@link VariableMap} of the BLAM {@link XWidget} values.
    * @param GetAs indicates if a publishing template or publishing template status report is being requested.
    * @param formatIndicator the selected publishing format.
    */

   private void runByOptions(VariableMap variableMap, GetAs getAs, FormatIndicator formatIndicator) throws Exception {

      //@formatter:off
         var rendererId       = variableMap.getString( PublishingTemplateBlam.variableRendererId       );
         var artifactTypeName = variableMap.getString( PublishingTemplateBlam.variableArtifactTypeName );
         var presentationType = variableMap.getString( PublishingTemplateBlam.variablePresentationType );
         var option           = variableMap.getString( PublishingTemplateBlam.variableOption           );

         if( rendererId == null ) {
            throw new OseeArgumentException("Must specify a renderer identifier." );
         }

         if( presentationType == null ) {
            throw new OseeArgumentException( "Must specifiy a presentation type." );
         }

         var publishingTemplateRequest =
            new PublishingTemplateRequest
                   (
                      rendererId,
                      artifactTypeName,
                      presentationType,
                      option,
                      formatIndicator
                   );

         this.requestPublishingTemplate(getAs, publishingTemplateRequest);
         //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {

      var getAsSelection = variableMap.getString(PublishingTemplateBlam.variableGetAs);

      var formatName = variableMap.getString(PublishingTemplateBlam.variableFormat);

      var byOptions = variableMap.getBoolean(PublishingTemplateBlam.variableByOptions);

      var getAs = GetAs.ofSelectionString(getAsSelection).orElse(null);

      if (Objects.isNull(getAs)) {
         throw new OseeArgumentException("Must make a selection for \"Get As\".");
      }

      var formatIndicator = FormatIndicator.ofFormatName(formatName).orElse(null);

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

   /**
    * {@inheritDoc}
    */

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {

      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);

      var xWidgetLabel = xWidget.getLabel();

      this.xWidgets.put(xWidgetLabel, xWidget);

      if (xWidget instanceof XText) {

         var xText = (XText) xWidget;
         var color = xText.getBackground();

         var device = color.getDevice();
         var red = color.getRed();
         var blue = color.getBlue();
         var green = color.getGreen();

         red = red > 128 ? red / 2 : red * 2;
         blue = blue > 128 ? blue / 2 : blue * 2;
         green = green > 128 ? green / 2 : green * 2;

         var disabledColor = new Color(device, red, blue, green);

         this.xWidgetBackgroundColors.put(xWidgetLabel, true, color);
         this.xWidgetBackgroundColors.put(xWidgetLabel, false, disabledColor);
      }

      if (PublishingTemplateBlam.variableByOptions.equals(xWidgetLabel)) {

         final var byOptionsCheckBox = (XCheckBox) xWidget;

         byOptionsCheckBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
               super.widgetSelected(selectionEvent);
               boolean isChecked = byOptionsCheckBox.isChecked();
               PublishingTemplateBlam.this.xWidgets.keySet().forEach(
                  (xWidgetLabel) -> PublishingTemplateBlam.this.enableDisableXWidget(xWidgetLabel, isChecked));
            }

         });
      }

      this.enableDisableXWidget(xWidgetLabel, true);

   }
}

/* EOF */
