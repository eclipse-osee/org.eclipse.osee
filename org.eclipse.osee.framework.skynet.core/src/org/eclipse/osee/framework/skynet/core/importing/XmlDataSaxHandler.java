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
package org.eclipse.osee.framework.skynet.core.importing;

import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Ryan D. Brooks
 */
public class XmlDataSaxHandler extends AbstractSaxHandler {
   private int level = 0;
   private RoughArtifact roughArtifact;
   private final Branch branch;
   private final AbstractArtifactExtractor extractor;
   private final ArtifactType primaryArtifactType;

   /**
    * @param branch
    */
   public XmlDataSaxHandler(AbstractArtifactExtractor extractor, Branch branch, ArtifactType primaryArtifactType) {
      super();
      this.branch = branch;
      this.extractor = extractor;
      this.primaryArtifactType = primaryArtifactType;
   }

   @Override
   public void endElementFound(String uri, String localName, String name) throws SAXException {
      if (level == 3) {
         roughArtifact.addAttribute(localName, getContents());
      }
      level--;
   }

   @Override
   public void startElementFound(String uri, String localName, String name, Attributes attributes) throws SAXException {
      level++;

      if (level == 2) {
         roughArtifact = new RoughArtifact(RoughArtifactKind.PRIMARY, branch);
         roughArtifact.setPrimaryArtifactType(primaryArtifactType);
         extractor.addRoughArtifact(roughArtifact);
      }
   }
}