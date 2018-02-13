/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workdef;

import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class WorkDefinitionSheet {

   public String name;
   private ArtifactToken artifact = null;
   private final String pluginId;

   public WorkDefinitionSheet(String name, String pluginId) {
      this.name = name;
      this.pluginId = pluginId;
   }

   public WorkDefinitionSheet(ArtifactToken artifact, String pluginId) {
      this(artifact.getName(), pluginId);
      this.artifact = artifact;
   }

   public String getName() {
      return name;
   }

   @Override
   public String toString() {
      return name;
   }

   public ArtifactToken getToken() {
      return artifact;
   }

   public String getPluginId() {
      return pluginId;
   }

}
