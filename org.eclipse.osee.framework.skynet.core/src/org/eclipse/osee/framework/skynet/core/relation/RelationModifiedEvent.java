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

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.plugin.event.Event;

/**
 * @author Robert A. Fisher
 */
public abstract class RelationModifiedEvent extends Event {

   private IRelationLink link;
   private String relationType;
   private String relationSide;
   private ModType modType;
   public enum ModType {
      Changed, Deleted, Added, RationaleMod
   };

   public static ModType getModType(String type) {
      for (ModType e : ModType.values())
         if (e.name().equals(type)) return e;
      return null;
   }

   /**
    * @param branch TODO
    * @param sender TODO
    */
   public RelationModifiedEvent(IRelationLink link, Branch branch, String relationType, String relationSide, ModType modType, Object sender) {
      super(sender);
      this.link = link;
      this.relationType = relationType;
      this.relationSide = relationSide;
      this.modType = modType;
   }

   public RelationModifiedEvent(IRelationLink link, Branch branch, String relationType, String relationSide, String modType, Object sender) {
      this(link, branch, relationType, relationSide, getModType(modType), sender);
   }

   public ModType getModType() {
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
   public IRelationLink getLink() {
      return link;
   }

   public boolean effectsArtifact(Artifact artifact) {
      boolean isEffected = false;

      isEffected =
            (getLink().getArtifactA() != null && getLink().getArtifactA().equals(artifact)) || (getLink().getArtifactB() != null && getLink().getArtifactB().equals(
                  artifact));

      return isEffected;
   }
}
