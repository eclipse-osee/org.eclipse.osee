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
package org.eclipse.osee.framework.skynet.core.importing.parsers;

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifactKind;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.xml.sax.Attributes;

/**
 * @author Ryan D. Brooks
 */
public class XmlDataSaxHandler extends AbstractSaxHandler {
   private int level = 0;
   private RoughArtifact roughArtifact;
   private final RoughArtifactCollector collector;
   private final IArtifactType primaryArtifactType;

   public XmlDataSaxHandler(RoughArtifactCollector collector, IArtifactType primaryArtifactType) {
      super();
      this.collector = collector;
      this.primaryArtifactType = primaryArtifactType;
   }

   @Override
   public void endElementFound(String uri, String localName, String name) {
      if (level == 3) {
         roughArtifact.addAttribute(localName, getContents());
      }
      level--;
   }

   @Override
   public void startElementFound(String uri, String localName, String name, Attributes attributes) {
      level++;

      if (level == 2) {
         roughArtifact = new RoughArtifact(RoughArtifactKind.PRIMARY);
         roughArtifact.setPrimaryArtifactType(primaryArtifactType);
         collector.addRoughArtifact(roughArtifact);
      }
   }
}