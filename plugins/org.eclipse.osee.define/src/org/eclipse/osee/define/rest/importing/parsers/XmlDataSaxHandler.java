/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.define.rest.importing.parsers;

import org.eclipse.osee.define.api.importing.RoughArtifact;
import org.eclipse.osee.define.api.importing.RoughArtifactCollector;
import org.eclipse.osee.define.api.importing.RoughArtifactKind;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.eclipse.osee.orcs.OrcsApi;
import org.xml.sax.Attributes;

/**
 * @author Ryan D. Brooks
 */
public class XmlDataSaxHandler extends AbstractSaxHandler {
   private int level = 0;
   private RoughArtifact roughArtifact;
   private final RoughArtifactCollector collector;
   private final ArtifactTypeToken primaryArtifactType;
   private final XResultData results;
   private final OrcsApi orcsApi;

   public XmlDataSaxHandler(OrcsApi orcsApi, XResultData results, RoughArtifactCollector collector, ArtifactTypeToken primaryArtifactType) {
      super();
      this.collector = collector;
      this.primaryArtifactType = primaryArtifactType;
      this.results = results;
      this.orcsApi = orcsApi;
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
         roughArtifact = new RoughArtifact(orcsApi, results, RoughArtifactKind.PRIMARY);
         roughArtifact.setPrimaryArtifactType(primaryArtifactType);
         collector.addRoughArtifact(roughArtifact);
      }
   }
}