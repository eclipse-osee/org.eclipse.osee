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
package org.eclipse.osee.define.rest.importing.parsers;

import org.eclipse.define.api.importing.RoughArtifact;
import org.eclipse.define.api.importing.RoughArtifactCollector;
import org.eclipse.define.api.importing.RoughArtifactKind;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.data.IArtifactType;
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
   private final IArtifactType primaryArtifactType;
   private final ActivityLog activityLog;
   private final OrcsApi orcsApi;

   public XmlDataSaxHandler(OrcsApi orcsApi, ActivityLog activityLog, RoughArtifactCollector collector, IArtifactType primaryArtifactType) {
      super();
      this.collector = collector;
      this.primaryArtifactType = primaryArtifactType;
      this.activityLog = activityLog;
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
         roughArtifact = new RoughArtifact(orcsApi, activityLog, RoughArtifactKind.PRIMARY);
         roughArtifact.setPrimaryArtifactType(primaryArtifactType);
         collector.addRoughArtifact(roughArtifact);
      }
   }
}