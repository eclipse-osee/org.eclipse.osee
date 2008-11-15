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
package org.eclipse.osee.framework.skynet.core.revision;

import java.util.Collection;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.change.RelationChangeIcons;
import org.eclipse.swt.graphics.Image;

/**
 * @author Robert A. Fisher
 */
public class RelationLinkSummary extends ChangeSummary<RelationLinkChange> implements IRelationLinkChange {
   private static final long serialVersionUID = 1073325175106326504L;

   /**
    * Constructor for deserialization.
    */
   protected RelationLinkSummary() {

   }

   /**
    * @param changes
    */
   public RelationLinkSummary(Collection<RelationLinkChange> changes) {
      super(changes);
   }

   protected Image getImage(ChangeType changeType, ModificationType modType) {
      return RelationChangeIcons.getImage(changeType, modType);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.revision.RelationLinkChange#getOtherArtifactDescriptor()
    */
   public ArtifactType getOtherArtifactDescriptor() {
      return getNewestChange().getOtherArtifactDescriptor();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.revision.RelationLinkChange#getOtherArtifactName()
    */
   public String getOtherArtifactName() {
      return getNewestChange().getOtherArtifactName();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.revision.RelationLinkChange#getRationale()
    */
   public String getRationale() {
      return getNewestChange().getRationale();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.revision.RelationLinkChange#getRelTypeName()
    */
   public String getRelTypeName() {
      return getNewestChange().getRelTypeName();
   }
}
