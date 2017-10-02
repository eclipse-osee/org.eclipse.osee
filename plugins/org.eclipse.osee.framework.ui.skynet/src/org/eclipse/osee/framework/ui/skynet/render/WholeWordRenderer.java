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
package org.eclipse.osee.framework.ui.skynet.render;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.WholeWordContent;
import static org.eclipse.osee.framework.core.enums.PresentationType.GENERALIZED_EDIT;
import static org.eclipse.osee.framework.core.enums.PresentationType.GENERAL_REQUESTED;
import static org.eclipse.osee.framework.core.enums.PresentationType.PREVIEW;
import static org.eclipse.osee.framework.core.enums.PresentationType.PRODUCE_ATTRIBUTE;
import static org.eclipse.osee.framework.core.enums.PresentationType.SPECIALIZED_EDIT;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CommandGroup;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.model.datarights.DataRightResult;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.util.PageOrientation;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.core.util.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.Streams;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.WordWholeDocumentAttribute;
import org.eclipse.osee.framework.skynet.core.linking.LinkType;
import org.eclipse.osee.framework.skynet.core.linking.WordMlLinkHandler;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;
import org.eclipse.osee.framework.ui.skynet.render.compare.WholeWordCompare;
import org.eclipse.osee.framework.ui.skynet.render.word.WordRendererUtil;
import org.eclipse.osee.framework.ui.skynet.util.WordUiUtil;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Jeff C. Phillips
 */
public class WholeWordRenderer extends WordRenderer {

   private static final String FTR_END_TAG = "</w:ftr>";
   private static final String FTR_START_TAG = "<w:ftr[^>]*>";
   private static final Pattern START_PATTERN = Pattern.compile(FTR_START_TAG);
   private static final Pattern END_PATTERN = Pattern.compile(FTR_END_TAG);
   private final IComparator comparator;

   public WholeWordRenderer(Map<RendererOption, Object> rendererOptions) {
      super(rendererOptions);
      this.comparator = new WholeWordCompare(this);
   }

   public WholeWordRenderer() {
      this(new HashMap<RendererOption, Object>());
   }

   @Override
   public WholeWordRenderer newInstance(Map<RendererOption, Object> rendererOptions) {
      return new WholeWordRenderer(rendererOptions);
   }

   @Override
   public WholeWordRenderer newInstance() {
      return new WholeWordRenderer();
   }

   @Override
   public void addMenuCommandDefinitions(ArrayList<MenuCmdDef> commands, Artifact artifact) {
      ImageDescriptor imageDescriptor = ImageManager.getProgramImageDescriptor("doc");
      commands.add(new MenuCmdDef(CommandGroup.EDIT, SPECIALIZED_EDIT, "MS Word Edit", imageDescriptor));
      commands.add(new MenuCmdDef(CommandGroup.PREVIEW, PREVIEW, "MS Word Preview", imageDescriptor));
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact, Map<RendererOption, Object> rendererOptions) {
      if (!presentationType.matches(GENERALIZED_EDIT, GENERAL_REQUESTED,
         PRODUCE_ATTRIBUTE) && artifact.isAttributeTypeValid(WholeWordContent)) {
         return PRESENTATION_SUBTYPE_MATCH;
      }
      return NO_MATCH;
   }

   @Override
   public InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> artifacts) {
      InputStream stream = null;
      try {
         if (artifacts.isEmpty()) {
            stream = Streams.convertStringToInputStream(WordWholeDocumentAttribute.getEmptyDocumentContent(), "UTF-8");
         } else {
            Artifact artifact = artifacts.iterator().next();
            String content = artifact.getOrInitializeSoleAttributeValue(CoreAttributeTypes.WholeWordContent);
            if (presentationType == PresentationType.DIFF && WordCoreUtil.containsWordAnnotations(content)) {
               throw new OseeStateException(
                  "Trying to diff the [%s] artifact on the [%s] branch, which has tracked changes turned on.  All tracked changes must be removed before the artifacts can be compared.",
                  artifact.getName(), artifact.getBranchToken().getName());
            }

            Set<String> unknownGuids = new HashSet<>();
            LinkType linkType = LinkType.OSEE_SERVER_LINK;
            content = WordMlLinkHandler.link(linkType, artifact, content, unknownGuids, presentationType);
            WordUiUtil.displayUnknownGuids(artifact, unknownGuids);

            String classification =
               artifact.getSoleAttributeValueAsString(CoreAttributeTypes.DataRightsClassification, "");
            if (Strings.isValid(classification)) {
               content = addDataRights(content, classification, artifact);
            }

            stream = Streams.convertStringToInputStream(content, "UTF-8");
         }
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return stream;
   }

   @Override
   public IComparator getComparator() {
      return comparator;
   }

   @Override
   protected IOperation getUpdateOperation(File file, List<Artifact> artifacts, BranchId branch, PresentationType presentationType) {
      return new FileToAttributeUpdateOperation(file, artifacts.get(0), CoreAttributeTypes.WholeWordContent);
   }

   @Override
   public void open(List<Artifact> artifacts, PresentationType presentationType) {
      for (Artifact artifact : artifacts) {
         super.open(Arrays.asList(artifact), presentationType);
      }
   }

   private String addDataRights(String content, String classification, Artifact artifact) {
      String toReturn = content;
      PageOrientation orientation = WordRendererUtil.getPageOrientation(artifact);

      DataRightResult dataRights = ServiceUtil.getOseeClient().getDataRightsEndpoint().getDataRights(
         Collections.singletonList(ArtifactId.valueOf(artifact.getId())), artifact.getBranch(), classification);

      String footer = dataRights.getContent(artifact.getId(), orientation);

      Matcher startFtr = START_PATTERN.matcher(footer);
      Matcher endFtr = END_PATTERN.matcher(footer);
      if (startFtr.find() && endFtr.find()) {
         ChangeSet ftrCs = new ChangeSet(footer);
         ftrCs.delete(0, startFtr.end());
         ftrCs.delete(endFtr.start(), footer.length());
         footer = ftrCs.applyChangesToSelf().toString();
      }

      startFtr.reset(content);
      endFtr.reset(content);
      ChangeSet cs = new ChangeSet(content);
      while (startFtr.find()) {
         if (endFtr.find()) {
            cs.replace(startFtr.end(), endFtr.start(), footer);
         }
      }
      toReturn = cs.applyChangesToSelf().toString();
      return toReturn;
   }
}