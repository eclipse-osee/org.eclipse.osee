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
   private final Class<?> clazz;

   public WorkDefinitionSheet(String name, Class<?> clazz) {
      this.name = name;
      this.clazz = clazz;
   }

   public WorkDefinitionSheet(ArtifactToken artifact, Class<?> clazz) {
      this(artifact.getName(), clazz);
      this.artifact = artifact;
   }

   public String getName() {
      return name;
   }

   @Override
   public String toString() {
      return name;
   }

   public ArtifactToken getArtifact() {
      return artifact;
   }

   public Class<?> getClazz() {
      return clazz;
   }

}
