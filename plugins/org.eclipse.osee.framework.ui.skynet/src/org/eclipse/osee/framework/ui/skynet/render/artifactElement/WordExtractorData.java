/*
 * Created on Mar 14, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.render.artifactElement;

import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
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

   public Artifact getArtifact(Branch branch) throws OseeCoreException {
      if (artifact == null && Strings.isValid(getGuid())) {
         artifact = ArtifactQuery.getArtifactFromId(getGuid(), branch, DeletionFlag.INCLUDE_DELETED);
      }
      return artifact;
   }
}
