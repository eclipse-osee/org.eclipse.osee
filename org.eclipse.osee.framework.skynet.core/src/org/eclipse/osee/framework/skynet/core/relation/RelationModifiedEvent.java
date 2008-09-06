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
package org.eclipse.osee.framework.skynet.core.relation;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.ui.plugin.event.Event;

/**
 * @author Robert A. Fisher
 */
public abstract class RelationModifiedEvent extends Event {

   private final RelationLink link;
   private final String relationType;
   private final String relationSide;
   private final RelationModType modType;
   public enum RelationModType {
      Changed, Deleted, Added, RationaleMod
   };

   public static RelationModType getModType(String type) {
      for (RelationModType e : RelationModType.values())
         if (e.name().equals(type)) return e;
      return null;
   }

   /**
    * @param branch TODO
    * @param sender TODO
    */
   public RelationModifiedEvent(RelationLink link, Branch branch, String relationType, String relationSide, RelationModType modType, Object sender) {
      super(sender);
      this.link = link;
      this.relationType = relationType;
      this.relationSide = relationSide;
      this.modType = modType;
   }

   public RelationModifiedEvent(RelationLink link, Branch branch, String relationType, String relationSide, String modType, Object sender) {
      this(link, branch, relationType, relationSide, getModType(modType), sender);
   }

   public RelationModType getModType() {
      return modType;
   }

   /**
    * @return Returns the relationSide.
    */
   public String getRelationSide() {
      return relationSide;
   }

   /**
    * @return Returns the relationType.
    */
   public String getRelationType() {
      return relationType;
   }

   /**
    * @return Returns the link.
    */
   public RelationLink getLink() {
      return link;
   }

   public boolean effectsArtifact(Artifact artifact) throws ArtifactDoesNotExist, SQLException {
      boolean isEffected = false;
      // Do a first check to see if artIds event match;  This should save framework from having to load other artifact if not loaded
      isEffected =
            getLink().getAArtifactId() == artifact.getArtId() || getLink().getBArtifactId() == artifact.getArtId();
      if (isEffected) {
         // Perform deeper equals check
         isEffected =
               (getLink().getArtifactA() != null && getLink().getArtifactA().equals(
                     artifact)) || (getLink().getArtifactB() != null && getLink().getArtifactB().equals(
                     artifact));
      }
      return isEffected;
   }
}
