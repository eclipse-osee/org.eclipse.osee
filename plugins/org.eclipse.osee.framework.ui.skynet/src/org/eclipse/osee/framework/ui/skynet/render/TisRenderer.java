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

import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.DEFAULT_OPEN;
import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.PREVIEW;
import java.io.InputStream;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.render.word.WordMLProducer;
import org.eclipse.osee.framework.ui.skynet.render.word.template.BasicTemplateAttributeHandler;
import org.eclipse.osee.framework.ui.skynet.render.word.template.ITemplateAttributeHandler;
import org.eclipse.osee.framework.ui.skynet.render.word.template.SRSSpecialPublishingAttributeHandler;
import org.eclipse.osee.framework.ui.skynet.render.word.template.TISAttributeHandler;
import org.eclipse.osee.framework.ui.skynet.render.word.template.WordAttributeTypeAttributeHandler;
import org.eclipse.osee.framework.ui.skynet.render.word.template.WordTemplateManager;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Andrew M. Finkbeiner
 */
public class TisRenderer extends WordTemplateRenderer {

   @Override
   public TisRenderer newInstance() {
      return new TisRenderer();
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, IArtifact artifact) throws OseeCoreException {
      if (artifact.getArtifactType().inheritsFrom(CoreArtifactTypes.TestInformationSheet) && presentationType.matches(
         DEFAULT_OPEN, PREVIEW)) {
         return SPECIALIZED_MATCH;
      }
      return NO_MATCH;
   }

   @Override
   public void addMenuCommandDefinitions(ArrayList<MenuCmdDef> commands, Artifact artifact) {
      ImageDescriptor imageDescriptor = ImageManager.getProgramImageDescriptor("doc");
      commands.add(new MenuCmdDef(CommandGroup.PREVIEW, PREVIEW, "MS Word TIS Preview", imageDescriptor));
   }

   @Override
   public String getName() {
      return "MS Word TIS Preview";
   }

   @Override
   public InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> artifacts) throws OseeCoreException {
      String template;

      if (artifacts.isEmpty()) {
         //  Still need to get a default template with a null artifact list
         template = getTemplate(null, presentationType);
      } else {
         Artifact firstArtifact = artifacts.iterator().next();
         template = getTemplate(firstArtifact, presentationType);
      }

      List<ITemplateAttributeHandler> handlers = new ArrayList<>();
      handlers.add(new SRSSpecialPublishingAttributeHandler());
      handlers.add(new TISAttributeHandler());
      handlers.add(new WordAttributeTypeAttributeHandler());
      handlers.add(new BasicTemplateAttributeHandler());
      WordTemplateManager wtm = new WordTemplateManager(template, handlers);
      CharBackedInputStream charBak = null;
      try {
         charBak = new CharBackedInputStream();
         WordMLProducer wordMl = new WordMLProducer(charBak);
         wtm.processArtifacts(wordMl, artifacts);
      } catch (CharacterCodingException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return charBak;
   }
}
