/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.rest.model.search.artifact;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;

/**
 * @author John Misinco
 */
@XmlRootElement(name = "SearchMatch")
public class SearchMatch {

   private ArtifactId artId;
   private AttributeId attrId;

   @XmlTransient
   private List<MatchLocation> locations;

   public SearchMatch() {
      // default constructor
   }

   public SearchMatch(ArtifactId artId, AttributeId attrId, List<MatchLocation> locations) {
      this.artId = artId;
      this.attrId = attrId;
      this.locations = locations;
   }

   public ArtifactId getArtId() {
      return artId;
   }

   public void setArtId(ArtifactId artId) {
      this.artId = artId;
   }

   public AttributeId getAttrId() {
      return attrId;
   }

   public void setAttrId(AttributeId attrId) {
      this.attrId = attrId;
   }

   public List<MatchLocation> getLocations() {
      return locations;
   }

   public void setLocations(List<MatchLocation> locations) {
      this.locations = locations;
   }

}
