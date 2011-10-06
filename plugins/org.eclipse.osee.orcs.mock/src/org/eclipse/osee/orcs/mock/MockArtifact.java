/*
 * Created on Sep 27, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.mock;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;

public class MockArtifact implements ReadableArtifact {

   @Override
   public int getGammaId() {
      return 0;
   }

   @Override
   public ModificationType getModificationType() {
      return null;
   }

   @Override
   public int getId() {
      return 0;
   }

   @Override
   public IOseeBranch getBranch() {
      return null;
   }

   @Override
   public String getHumanReadableId() {
      return null;
   }

   @Override
   public int getTransactionId() {
      return 0;
   }

   @Override
   public IArtifactType getArtifactType() {
      return null;
   }

   @Override
   public Collection<IAttributeType> getAttributeTypes() throws OseeCoreException {
      return null;
   }

   @Override
   public <T> List<ReadableAttribute<T>> getAttributes(IAttributeType attributeType) throws OseeCoreException {
      return null;
   }

   @Override
   public ReadableArtifact getRelatedArtifact(IRelationTypeSide relationSide) throws OseeCoreException {
      return null;
   }

   @Override
   public List<ReadableArtifact> getRelatedArtifacts(IRelationTypeSide relationEnum) throws OseeCoreException {
      return null;
   }

   @Override
   public String getSoleAttributeAsString(IAttributeType attributeType) throws OseeCoreException {
      return null;
   }

   @Override
   public String getGuid() {
      return null;
   }

   @Override
   public boolean matches(Identity<?>... identities) {
      return false;
   }

   @Override
   public String getName() {
      return null;
   }

   @Override
   public Collection<IRelationType> getValidRelationTypes() {
      return null;
   }

}