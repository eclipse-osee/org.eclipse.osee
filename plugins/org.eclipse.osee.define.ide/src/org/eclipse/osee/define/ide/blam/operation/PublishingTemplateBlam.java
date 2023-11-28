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
import java.util.List;
import java.util.Objects;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.builder.XWidgetBuilder;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
import org.eclipse.swt.program.Program;

/**
 * BLAM to find a publishing template using the server side template manager. The BLAM generates and displays a text
 * report describing the found publishing template.
 *
 * @author Loren K. Ashley
 */

public class PublishingTemplateBlam extends AbstractBlam {

   /**
    * Description string for the BLAM
    */

   private static String blamDescription =
      "Finds a Publishing Template using the server side Publishing Template Manager.";

   /**
    * Name string for the BLAM
    */

   private static String blamName = "Publishing Template BLAM";

   /**
    * BLAM XWidget variable name and title for the template selection parameter &quot;Renderer Id&quot;.
    */

   private static String variableRendererId = "Renderer Id";

   /**
    * BLAM XWidget variable name and title for the template selection parameter &quot;Artifact Type Name&quot;.
    */

   private static String variableArtifactTypeName = "Artifact Type Name";

   /**
    * BLAM XWidget variable name and title for the template selection parameter &quot;Presentation Type&quot;.
    */

   private static String variablePresentationType = "Presentation Type";

   /**
    * BLAM XWidget variable name and title for the template selection parameter &quot;Option&quot;.
    */

   private static String variableOption = "Option";

   /**
    * Creates a new {@link PublishingTemplateBlam} instance for finding publishing templates with the server side
    * template manager.
    */

   public PublishingTemplateBlam() {
      super(PublishingTemplateBlam.blamName, PublishingTemplateBlam.blamDescription, null);
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
                .andWidget( PublishingTemplateBlam.variableRendererId,       "XText" ).endWidget()
                .andWidget( PublishingTemplateBlam.variableArtifactTypeName, "XText" ).endWidget()
                .andWidget( PublishingTemplateBlam.variablePresentationType, "XText" ).endWidget()
                .andWidget( PublishingTemplateBlam.variableOption,           "XText" ).endWidget()
                .getItems();
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {

      try {
         //@formatter:off
         var rendererId       = variableMap.getString( PublishingTemplateBlam.variableRendererId       );
         var artifactTypeName = variableMap.getString( PublishingTemplateBlam.variableArtifactTypeName );
         var presentationType = variableMap.getString( PublishingTemplateBlam.variablePresentationType );
         var option           = variableMap.getString( PublishingTemplateBlam.variableOption           );
         //@formatter:on

         var oseeClient = ServiceUtil.getOseeClient();
         var templateManagerEndpoint = oseeClient.getTemplateManagerEndpoint();

         var publishingTemplateRequest =
            new PublishingTemplateRequest(rendererId, artifactTypeName, presentationType, option);

         var publishingTemplate = templateManagerEndpoint.getPublishingTemplate(publishingTemplateRequest);

         var fileName =
            "PUBLISHING_TEMPLATE_" + publishingTemplate.getIdentifier() + "_" + Lib.getDateTimeString() + ".txt";
         var file = OseeData.getFile(fileName);

         try (var fileWriter = new FileWriter(file)) {
            fileWriter.write(publishingTemplate.toString());
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

}

/* EOF */
