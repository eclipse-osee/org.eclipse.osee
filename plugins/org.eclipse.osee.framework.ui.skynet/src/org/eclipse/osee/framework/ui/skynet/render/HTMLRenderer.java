/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.framework.ui.skynet.render;

import static org.eclipse.osee.framework.core.enums.PresentationType.PREVIEW;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CommandGroup;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.Streams;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.CompressedContentAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.OutlineNumberAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;
import org.eclipse.osee.framework.skynet.core.utility.NormalizeHtml;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.program.Program;

/*
 * @author Marc Potter
 */

public class HTMLRenderer extends FileSystemRenderer {

   private final IComparator comparator;

   /**
    * The likely file system extension for files that hold the same type of data as is stored in main content
    * {@link Attribute} of the most common {@link Artifact} type processed by this renderer.
    */

   private static final String DEFAULT_ASSOCIATED_FILE_EXTENSION = "htm";

   /**
    * A short description of the type of documents processed by the renderer.
    */

   private static final String RENDERER_DOCUMENT_TYPE_DESCRIPTION = "Web HTML";

   /**
    * The renderer identifier used for publishing template selection.
    */

   private static final String RENDERER_IDENTIFIER = HTMLRenderer.class.getCanonicalName();

   /**
    * The {@link IRenderer} implementation's name.
    */

   private static final String RENDERER_NAME = "HTML Renderer";

   public HTMLRenderer(Map<RendererOption, Object> rendererOptions) {
      super(rendererOptions);
      this.comparator = new HTMLDiffRenderer();
   }

   public HTMLRenderer() {
      this(new HashMap<RendererOption, Object>());
   }

   @Override
   public void addMenuCommandDefinitions(ArrayList<MenuCmdDef> commands, Artifact artifact) {
      ImageDescriptor icon = ImageManager.getProgramImageDescriptor("htm");
      commands.add(new MenuCmdDef(CommandGroup.PREVIEW, PREVIEW, "HTML Preview", icon));
   }

   @Override
   public InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> artifacts) {
      InputStream stream = null;
      StringBuilder content = new StringBuilder("");
      try {
         if (artifacts.size() == 1) {
            Artifact artifact = artifacts.iterator().next();
            renderHtmlArtifact(content, artifact);
         } else {
            for (Artifact artifact : artifacts) {
               renderHtmlArtifact(content, artifact);
               content.append("<br /><br /><br />");
            }
         }
         stream = Streams.convertStringToInputStream(
            NormalizeHtml.wrapAndNormalizeHTML(content.toString(), true, true, true), "UTF-8");
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return stream;
   }

   @Override
   public Program getAssociatedProgram(Artifact artifact) {
      Program program = Program.findProgram("htm");
      if (program == null) {
         throw new OseeArgumentException("No program associated with the extension *.htm found on your local machine.");
      }
      return program;
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact, Map<RendererOption, Object> rendererOptions) {
      int toReturn = NO_MATCH;
      if (artifact.isAttributeTypeValid(CoreAttributeTypes.HtmlContent)) {
         if (presentationType.matches(PresentationType.PREVIEW, PresentationType.DIFF)) {
            toReturn = PRESENTATION_SUBTYPE_MATCH;
         }
      }
      return toReturn;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getDefaultAssociatedExtension() {
      return HTMLRenderer.DEFAULT_ASSOCIATED_FILE_EXTENSION;
   }

   @Override
   protected IOperation getUpdateOperation(File file, List<Artifact> artifacts, BranchId branch, PresentationType presentationType) {
      return new FileToAttributeUpdateOperation(file, artifacts.get(0), CoreAttributeTypes.PlainTextContent);
   }

   @Override
   public HTMLRenderer newInstance() {
      return new HTMLRenderer();
   }

   @Override
   public HTMLRenderer newInstance(Map<RendererOption, Object> rendererOptions) {
      return new HTMLRenderer(rendererOptions);
   }

   @Override
   public IComparator getComparator() {
      return comparator;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getDocumentTypeDescription() {
      return HTMLRenderer.RENDERER_DOCUMENT_TYPE_DESCRIPTION;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getIdentifier() {
      return HTMLRenderer.RENDERER_IDENTIFIER;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getName() {
      return HTMLRenderer.RENDERER_NAME;
   }

   private void renderHtmlArtifact(StringBuilder output, Artifact artifact) throws Exception {
      Map<String, String> fileNameReplace = new HashMap<>();
      String htmlContent = "";
      String blankLine = "<br /><br />";
      String underline = "<span style=\"text-decoration: underline;\">";
      String underlineEnd = "</span>";
      Collection<AttributeTypeToken> types = artifact.getAttributeTypesUsed();
      BranchToken branchToken = artifact.getBranchToken();
      String branchName = branchToken.getShortName();
      for (AttributeTypeToken type : types) {
         if (type.equals(CoreAttributeTypes.HtmlContent)) {
            htmlContent = artifact.getAttributesToString(type);
         } else if (type.equals(CoreAttributeTypes.ImageContent)) {
            List<Attribute<Object>> attrs = artifact.getAttributes((AttributeTypeId) type);
            for (Object a : attrs) {

               if (a instanceof CompressedContentAttribute) {

                  CompressedContentAttribute c = (CompressedContentAttribute) a;
                  InputStream stream = c.getValue();
                  String attrId = c.getIdString();
                  //@formatter:off
                  String workingFileString =
                     RenderingUtil
                        .getRenderFile
                           (
                              this,
                              PresentationType.PREVIEW,
                              null,
                              attrId,
                              branchName
                           )
                        .flatMap( RenderingUtil::getOsString )
                        .orElseThrow
                           (
                              () -> new OseeCoreException
                                           (
                                              new Message()
                                                     .title( "HTMLRenderer::renderHtmlArtifact, Failed to locate renderer file." )
                                                     .indentInc()
                                                     .segment( "Renderer",             this.getName()            )
                                                     .segment( "Presentation Type",    PresentationType.PREVIEW  )
                                                     .segment( "Branch Identifier",    branchToken.getIdString() )
                                                     .segment( "Attribute Identifier", attrId                    )
                                                     .toString()
                                           )
                           );
                  //@formatter:on
                  fileNameReplace.put(attrId, workingFileString);
                  File test = new File(workingFileString);
                  Lib.inputStreamToFile(stream, test);
               }
            }
         } else {
            List<Attribute<Object>> attrs = artifact.getAttributes((AttributeTypeId) type);
            for (Object o : attrs) {
               if (o instanceof StringAttribute || o instanceof OutlineNumberAttribute) {
                  String attributeValue = artifact.getAttributesToString(type);
                  if (Strings.isValid(attributeValue)) {
                     output.append(blankLine);
                     output.append(underline);
                     output.append(type.getName());
                     output.append(underlineEnd);
                     output.append(":    ");
                     output.append(attributeValue);
                     break;
                  }
               }
            }
         }
      }
      // add the HTML attribute  -- want it always on the end
      output.append(blankLine);
      if (fileNameReplace.size() > 0) {
         Object[] keys = fileNameReplace.keySet().toArray();
         for (Object key : keys) {
            String file = "file://" + File.separator;
            file += fileNameReplace.get(key);
            file = file.replaceAll("\\\\", "\\\\\\\\");
            htmlContent = htmlContent.replaceAll((String) key, file);
         }
      }
      output.append(htmlContent);
   }

}
