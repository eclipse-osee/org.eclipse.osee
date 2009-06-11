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

import static org.eclipse.osee.framework.core.enums.ModificationType.DELETED;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;

/**
 * @author Robert A. Fisher
 */
public class RelationLinkChange extends RevisionChange implements IRelationLinkChange {
   private static final long serialVersionUID = 1L;
   private int relLinkId;
   private String rationale;
   private int order;
   private String relTypeName;
   private String otherArtifactName;
   private ArtifactType otherArtifactType;

   /**
    * Constructor for deserialization.
    */
   protected RelationLinkChange() {

   }

   /**
    * Constructs a new or modified link change.
    * 
    * @param modType
    * @param gammaId
    * @param rationale
    * @param order
    * @param relTypeName
    * @param otherArtifactName
    * @param otherArtifactType
    */
   public RelationLinkChange(ChangeType changeType, ModificationType modType, int relLinkId, long gammaId, String rationale, int order, String relTypeName, String otherArtifactName, ArtifactType otherArtifactType) {
      super(changeType, modType, gammaId);
      this.relLinkId = relLinkId;
      this.rationale = rationale;
      this.order = order;
      this.relTypeName = relTypeName;
      this.otherArtifactName = otherArtifactName;
      this.otherArtifactType = otherArtifactType;
   }

   /**
    * Constructs a deleted link change.
    * 
    * @param gammaId
    * @param relTypeName
    * @param otherArtifactName
    * @param otherArtifactType
    */
   public RelationLinkChange(ChangeType changeType, int relLinkId, long gammaId, String relTypeName, String otherArtifactName, ArtifactType otherArtifactType) {
      super(changeType, DELETED, gammaId);
      this.relLinkId = relLinkId;
      this.relTypeName = relTypeName;
      this.otherArtifactName = otherArtifactName;
      this.otherArtifactType = otherArtifactType;
   }

   /**
    * @return Returns the text for 'change' text.
    */
   @Override
   public String getChange() {
      return rationale;
   }

   /**
    * @return Returns the order.
    */
   public int getOrder() {
      return order;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.revision.RelationLinkChange#getOtherArtifactDescriptor()
    */
   public ArtifactType getOtherArtifactType() {
      return otherArtifactType;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.revision.RelationLinkChange#getOtherArtifactName()
    */
   public String getOtherArtifactName() {
      return otherArtifactName;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.revision.RelationLinkChange#getRationale()
    */
   public String getRationale() {
      return rationale;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.revision.RelationLinkChange#getRelTypeName()
    */
   public String getRelTypeName() {
      return relTypeName;
   }

   /**
    * @return Returns the relLinkId.
    */
   public int getRelLinkId() {
      return relLinkId;
   }

   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      if (adapter == null) throw new IllegalArgumentException("adapter can not be null");

      if (adapter.isInstance(this)) {
         return this;
      }
      return null;
   }
}
