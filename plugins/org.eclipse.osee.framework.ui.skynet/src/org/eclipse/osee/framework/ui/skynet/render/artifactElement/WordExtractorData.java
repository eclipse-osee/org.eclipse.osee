/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.framework.ui.skynet.render.artifactElement;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Jeff C. Phillips
 */
public class WordExtractorData {
   private Element parent;
   private String guid;
   private Artifact artifact;

   public void setGuid(String guid) {
      this.guid = guid;
   }

   public void addParent(Element parent) {
      this.parent = parent;
   }

   public void addChild(Node child) {
      parent.appendChild(child);
   }

   public String getGuid() {
      return guid;
   }

   public Element getParentEelement() {
      return parent;
   }

   public Artifact getArtifact(BranchId branch) {
      if (artifact == null && Strings.isValid(getGuid())) {
         artifact = ArtifactQuery.getArtifactFromId(getGuid(), branch, DeletionFlag.INCLUDE_DELETED);
      }
      return artifact;
   }
}
