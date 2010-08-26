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
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.word.WordMLProducer;
import org.eclipse.osee.framework.ui.skynet.render.word.template.BasicTemplateAttributeHandler;
import org.eclipse.osee.framework.ui.skynet.render.word.template.ITemplateAttributeHandler;
import org.eclipse.osee.framework.ui.skynet.render.word.template.SRSSpecialPublishingAttributeHandler;
import org.eclipse.osee.framework.ui.skynet.render.word.template.TISAttributeHandler;
import org.eclipse.osee.framework.ui.skynet.render.word.template.WordAttributeTypeAttributeHandler;
import org.eclipse.osee.framework.ui.skynet.render.word.template.WordTemplateManager;

/**
 * @author Andrew M. Finkbeiner
 */
public class TisRenderer extends WordTemplateRenderer {

   @Override
   public TisRenderer newInstance() {
      return new TisRenderer();
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) throws OseeCoreException {
      if (artifact.isOfType(CoreArtifactTypes.TestInformationSheet) && presentationType.matches(DEFAULT_OPEN, PREVIEW)) {
         return SUBTYPE_TYPE_MATCH;
      }
      return NO_MATCH;
   }

   @Override
   public List<String> getCommandId(PresentationType presentationType) {
      ArrayList<String> commandIds = new ArrayList<String>(1);

      if (presentationType == PREVIEW) {
         commandIds.add("org.eclipse.osee.framework.ui.skynet.tispreview.command");
      }

      return commandIds;
   }

   @Override
   public String getName() {
      return "MS Word TIS Preview";
   }

   @Override
   public InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> artifacts) throws OseeCoreException {
      if (PresentationType.GENERALIZED_EDIT == presentationType) {
         return super.getRenderInputStream(presentationType, artifacts);
      }
      final VariableMap variableMap = new VariableMap();
      String template;

      if (artifacts.isEmpty()) {
         //  Still need to get a default template with a null artifact list
         template = getTemplate(null, presentationType);
      } else {
         Artifact firstArtifact = artifacts.iterator().next();
         template = getTemplate(firstArtifact, presentationType);
      }

      variableMap.setValue(DEFAULT_SET_NAME, artifacts);

      List<ITemplateAttributeHandler> handlers = new ArrayList<ITemplateAttributeHandler>();
      handlers.add(new SRSSpecialPublishingAttributeHandler());
      handlers.add(new TISAttributeHandler());
      handlers.add(new WordAttributeTypeAttributeHandler());
      handlers.add(new BasicTemplateAttributeHandler());
      WordTemplateManager wtm = new WordTemplateManager(template, handlers);
      CharBackedInputStream charBak = null;
      try {
         charBak = new CharBackedInputStream();
         WordMLProducer wordMl = new WordMLProducer(charBak);
         wtm.processArtifacts(wordMl, variableMap.getArtifacts(wtm.getArtifactSet()));
      } catch (CharacterCodingException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return charBak;
   }
}
