/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CommandGroup;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
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
   public InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> artifacts) throws OseeCoreException {
      return getRenderInputStream(presentationType, null, artifacts);
   }

   @Override
   public InputStream getRenderInputStream(PresentationType presentationType, IOseeBranch branch, List<Artifact> artifacts) throws OseeCoreException {
      InputStream stream = null;
      StringBuilder content = new StringBuilder("");
      try {
         if (artifacts.size() == 1) {
            Artifact artifact = artifacts.iterator().next();
            renderHtmlArtifact(content, branch, artifact);
         } else {
            for (Artifact artifact : artifacts) {
               renderHtmlArtifact(content, branch, artifact);
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
   public Program getAssociatedProgram(Artifact artifact) throws OseeCoreException {
      Program program = Program.findProgram("htm");
      if (program == null) {
         throw new OseeArgumentException("No program associated with the extension *.htm found on your local machine.");
      }
      return program;
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact, Map<RendererOption, Object> rendererOptions) {
      int toReturn = NO_MATCH;
      if (artifact.isAttributeTypeValid(CoreAttributeTypes.HTMLContent)) {
         if (presentationType.matches(PresentationType.PREVIEW, PresentationType.DIFF)) {
            toReturn = PRESENTATION_SUBTYPE_MATCH;
         }
      }
      return toReturn;
   }

   @Override
   public String getAssociatedExtension(Artifact artifact) {
      return "htm";
   }

   @Override
   protected IOperation getUpdateOperation(File file, List<Artifact> artifacts, BranchId branch, PresentationType presentationType) {
      return new FileToAttributeUpdateOperation(file, artifacts.get(0), CoreAttributeTypes.PlainTextContent);
   }

   @Override
   public String getName() {
      return "HTML Renderer";
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

   private void renderHtmlArtifact(StringBuilder output, IOseeBranch branch, Artifact artifact) throws Exception {
      Map<String, String> fileNameReplace = new HashMap<>();
      String htmlContent = "";
      String blankLine = "<br /><br />";
      String underline = "<span style=\"text-decoration: underline;\">";
      String underlineEnd = "</span>";
      Collection<AttributeType> types = artifact.getAttributeTypesUsed();
      for (AttributeType type : types) {
         if (type.equals(CoreAttributeTypes.HTMLContent)) {
            htmlContent = artifact.getAttributesToString(type);
         } else if (type.equals(CoreAttributeTypes.ImageContent)) {
            List<Attribute<Object>> attrs = artifact.getAttributes((AttributeTypeId) type);
            for (Object a : attrs) {
               if (a instanceof CompressedContentAttribute) {
                  CompressedContentAttribute c = (CompressedContentAttribute) a;
                  InputStream stream = c.getValue();
                  String attrId = c.getIdString();
                  String workingFileString =
                     RenderingUtil.getRenderPath(this, branch, PresentationType.PREVIEW, "", "", "") + "-" + attrId;
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
