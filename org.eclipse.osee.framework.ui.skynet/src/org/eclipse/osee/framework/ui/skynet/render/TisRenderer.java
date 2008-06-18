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
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.render.word.WordMLProducer;
import org.eclipse.osee.framework.ui.skynet.render.word.template.BasicTemplateAttributeHandler;
import org.eclipse.osee.framework.ui.skynet.render.word.template.ITemplateAttributeHandler;
import org.eclipse.osee.framework.ui.skynet.render.word.template.SRSSpecialPublishingAttributeHandler;
import org.eclipse.osee.framework.ui.skynet.render.word.template.TISAttributeHandler;
import org.eclipse.osee.framework.ui.skynet.render.word.template.WordAttributeTypeAttributeHandler;
import org.eclipse.osee.framework.ui.skynet.render.word.template.WordTemplateManager;

/**
 * @author b1528444
 */
public class TisRenderer extends WordRenderer {

   /**
    * @throws TransformerConfigurationException
    * @throws IOException
    * @throws TransformerFactoryConfigurationError
    * @throws CoreException
    */
   public TisRenderer() throws TransformerConfigurationException, IOException, TransformerFactoryConfigurationError, CoreException {
      super();
   }

   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) {
      if ("Test Information Sheet".equals(artifact.getArtifactTypeName())) {
         return SUBTYPE_TYPE_MATCH;
      }
      return NO_MATCH;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.render.FileRenderer#getRenderInputStream(org.eclipse.core.runtime.IProgressMonitor,
    *      org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String,
    *      org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer.PresentationType)
    */
   @Override
   public InputStream getRenderInputStream(IProgressMonitor monitor, List<Artifact> artifacts, String option, PresentationType presentationType) throws Exception {
      if (PresentationType.EDIT == presentationType) {
         return super.getRenderInputStream(monitor, artifacts, option, presentationType);
      }
      final BlamVariableMap variableMap = new BlamVariableMap();
      String template;

      if (artifacts.isEmpty()) {
         //  Still need to get a default template with a null artifact list
         template = getTemplate(null, presentationType, option);
      } else {
         Artifact firstArtifact = artifacts.iterator().next();
         template = getTemplate(firstArtifact, presentationType, option);
      }

      variableMap.setValue(DEFAULT_SET_NAME, artifacts);

      List<ITemplateAttributeHandler> handlers = new ArrayList<ITemplateAttributeHandler>();
      handlers.add(new SRSSpecialPublishingAttributeHandler());
      handlers.add(new TISAttributeHandler());
      handlers.add(new WordAttributeTypeAttributeHandler());
      handlers.add(new BasicTemplateAttributeHandler());
      WordTemplateManager wtm = new WordTemplateManager(template, handlers);
      CharBackedInputStream charBak = new CharBackedInputStream();
      WordMLProducer wordMl = new WordMLProducer(charBak);
      wtm.processArtifacts(wordMl, variableMap.getArtifacts(wtm.getArtifactSet()));
      return charBak;
   }

}
