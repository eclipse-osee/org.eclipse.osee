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
package org.eclipse.osee.framework.skynet.core.artifact;

/**
 * @author Michael S. Rodgers
 */
public class ArtifactData {
   private String url = "";
   private String source = "";
   private final Artifact[] artifacts;

   public ArtifactData(Artifact[] artifacts, String url, String source) {
      this.artifacts = artifacts;
      this.url = url;
      this.source = source;
   }

   public Artifact[] getArtifacts() {
      return artifacts;
   }

   public String getUrl() {
      return url;
   }

   public String getSource() {
      return source;
   }

   public void setSource(String source) {
      this.source = source;
   }

   public void setUrl(String url) {
      this.url = url;
   }

}
