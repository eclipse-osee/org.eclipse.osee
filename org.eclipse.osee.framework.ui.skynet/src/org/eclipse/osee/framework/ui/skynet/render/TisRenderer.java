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

import java.io.InputStream;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
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

   /**
    * @param rendererId
    */
   public TisRenderer() {
      super();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#newInstance()
    */
   @Override
   public TisRenderer newInstance() throws OseeCoreException {
      return new TisRenderer();
   }

   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) {
      if ("Test Information Sheet".equals(artifact.getArtifactTypeName()) && presentationType == PresentationType.PREVIEW) {
         return SUBTYPE_TYPE_MATCH;
      }
      return NO_MATCH;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer#getCommandId()
    */
   @Override
   public List<String> getCommandId(PresentationType presentationType) {
      ArrayList<String> commandIds = new ArrayList<String>(1);

      if (presentationType == PresentationType.PREVIEW) {
         commandIds.add("org.eclipse.osee.framework.ui.skynet.tispreview.command");
      }

      return commandIds;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.WordRenderer#getName()
    */
   @Override
   public String getName() {
      return "MS Word TIS Preview";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.render.FileRenderer#getRenderInputStream(org.eclipse.core.runtime.IProgressMonitor,
    *      org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String,
    *      org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer.PresentationType)
    */
   @Override
   public InputStream getRenderInputStream(List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
      if (PresentationType.GENERALIZED_EDIT == presentationType) {
         return super.getRenderInputStream(artifacts, presentationType);
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
      try {
         CharBackedInputStream charBak = new CharBackedInputStream();
         WordMLProducer wordMl = new WordMLProducer(charBak);
         wtm.processArtifacts(wordMl, variableMap.getArtifacts(wtm.getArtifactSet()));
         return charBak;
      } catch (CharacterCodingException ex) {
         throw new OseeWrappedException(ex);
      }
   }
}
