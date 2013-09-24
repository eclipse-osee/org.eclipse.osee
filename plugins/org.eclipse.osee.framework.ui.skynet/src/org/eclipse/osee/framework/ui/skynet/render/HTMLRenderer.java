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

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.commands.Command;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.Streams;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.OutlineNumberAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.skynet.core.utility.NormalizeHtml;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.program.Program;

/* 
 * @author Marc Potter
 */

public class HTMLRenderer extends FileSystemRenderer {

   private final IComparator comparator;

   public HTMLRenderer() {
      this.comparator = new HTMLDiffRenderer();
   }

   @Override
   public List<String> getCommandIds(CommandGroup commandGroup) {
      ArrayList<String> commandIds = new ArrayList<String>(1);

      if (commandGroup.isPreview()) {
         commandIds.add("org.eclipse.osee.framework.ui.skynet.htmlprevieweditor.command");
      }

      if (commandGroup.isEdit()) {
         // currently unsupported
      }

      return commandIds;
   }

   @Override
   public ImageDescriptor getCommandImageDescriptor(Command command, Artifact artifact) {
      return ImageManager.getProgramImageDescriptor("htm");
   }

   @Override
   public InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> artifacts) throws OseeCoreException {
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
         stream =
            Streams.convertStringToInputStream(
               NormalizeHtml.wrapAndNormalizeHTML(content.toString(), true, true, true), "UTF-8");
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
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
   public int getApplicabilityRating(PresentationType presentationType, IArtifact artifact) throws OseeCoreException {
      Artifact aArtifact = artifact.getFullArtifact();
      int toReturn = NO_MATCH;
      if (aArtifact.isAttributeTypeValid(CoreAttributeTypes.HTMLContent)) {
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
   protected IOperation getUpdateOperation(File file, List<Artifact> artifacts, IOseeBranch branch, PresentationType presentationType) {
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
   public IComparator getComparator() {
      return comparator;
   }

   private void renderHtmlArtifact(StringBuilder output, Artifact artifact) throws Exception {
      String htmlContent = "";
      String blankLine = "<br /><br />";
      String underline = "<span style=\"text-decoration: underline;\">";
      String underlineEnd = "</span>";
      Collection<AttributeType> types = artifact.getAttributeTypesUsed();
      for (AttributeType type : types) {
         if (type.equals(CoreAttributeTypes.HTMLContent)) {
            htmlContent = artifact.getAttributesToString(type);
         } else if (type.equals(CoreAttributeTypes.ImageContent)) {
            // do nothing
         } else {
            List<Attribute<Object>> attrs = artifact.getAttributes((IAttributeType) type);
            for (Object o : attrs) {
               if ((o instanceof StringAttribute) || (o instanceof OutlineNumberAttribute)) {
                  String attributeValue = artifact.getAttributesToString(type);
                  if (Strings.isValid(attributeValue)) {
                     output.append(blankLine);
                     output.append(underline);
                     output.append(type.getName());
                     output.append(underlineEnd);
                     output.append(":    ");
                     output.append(attributeValue);
                  }
               }
            }
         }
      }
      // add the HTML attribute  -- want it always on the end
      output.append(blankLine);
      output.append(htmlContent);
   }
}
