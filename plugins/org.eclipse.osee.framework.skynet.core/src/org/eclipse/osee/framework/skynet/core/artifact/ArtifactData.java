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
