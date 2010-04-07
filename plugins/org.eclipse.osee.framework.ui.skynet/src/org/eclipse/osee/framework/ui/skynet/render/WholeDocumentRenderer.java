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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.io.Streams;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.WordWholeDocumentAttribute;
import org.eclipse.osee.framework.skynet.core.linking.LinkType;
import org.eclipse.osee.framework.skynet.core.linking.WordMlLinkHandler;
import org.eclipse.osee.framework.skynet.core.word.WordAnnotationHandler;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;
import org.eclipse.osee.framework.ui.skynet.render.compare.WholeWordCompare;

/**
 * @author Jeff C. Phillips
 */
public class WholeDocumentRenderer extends WordRenderer {

   private final IComparator comparator;

   public WholeDocumentRenderer() {
      this.comparator = new WholeWordCompare(this);
   }

   @Override
   public WholeDocumentRenderer newInstance() throws OseeCoreException {
      return new WholeDocumentRenderer();
   }

   @Override
   public List<String> getCommandId(PresentationType presentationType) {
      ArrayList<String> commandIds = new ArrayList<String>(1);

      if (presentationType == PresentationType.SPECIALIZED_EDIT) {
         commandIds.add("org.eclipse.osee.framework.ui.skynet.wholedocumenteditor.command");
      } else if (presentationType == PresentationType.PREVIEW) {
         commandIds.add("org.eclipse.osee.framework.ui.skynet.wholewordpreview.command");
      }

      return commandIds;
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) throws OseeCoreException {
      if (artifact.isAttributeTypeValid(CoreAttributeTypes.WHOLE_WORD_CONTENT) && presentationType != PresentationType.GENERALIZED_EDIT) {
         return PRESENTATION_SUBTYPE_MATCH;
      }
      return NO_MATCH;
   }

   @Override
   public InputStream getRenderInputStream(List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
      return getRenderInputStream(artifacts.iterator().next(), presentationType);
   }

   @Override
   public InputStream getRenderInputStream(Artifact artifact, PresentationType presentationType) throws OseeCoreException {
      InputStream stream = null;
      try {
         if (artifact == null) {
            stream = Streams.convertStringToInputStream(WordWholeDocumentAttribute.getEmptyDocumentContent(), "UTF-8");
         } else {
            String content = artifact.getOrInitializeSoleAttributeValue(CoreAttributeTypes.WHOLE_WORD_CONTENT);
            if (presentationType == PresentationType.DIFF && WordAnnotationHandler.containsWordAnnotations(content)) {
               throw new OseeStateException(
                     "Trying to diff the " + artifact.getName() + " artifact on the " + artifact.getBranch().getShortName() + " branch, which has tracked changes turned on.  All tracked changes must be removed before the artifacts can be compared.");
            }

            LinkType linkType = LinkType.OSEE_SERVER_LINK;
            content = WordMlLinkHandler.link(linkType, artifact, content);
            stream = Streams.convertStringToInputStream(content, "UTF-8");
         }
      } catch (IOException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return stream;
   }

   @Override
   public IComparator getComparator() {
      return comparator;
   }
}