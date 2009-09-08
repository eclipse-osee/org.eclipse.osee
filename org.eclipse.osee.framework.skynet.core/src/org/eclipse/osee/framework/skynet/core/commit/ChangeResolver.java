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
package org.eclipse.osee.framework.skynet.core.commit;

import java.util.Collection;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;

/**
 * @author Roberto E. Escobar
 */
public class ChangeResolver implements IChangeResolver {

   // TODO Create Data Stuctures;

   private final HashCollection<Integer, OseeChange> artifactData;
   private final HashCollection<Integer, OseeChange> attributeData;
   private final HashCollection<Integer, OseeChange> relationData;

   public ChangeResolver() {
      artifactData = new HashCollection<Integer, OseeChange>();
      attributeData = new HashCollection<Integer, OseeChange>();
      relationData = new HashCollection<Integer, OseeChange>();
   }

   @Override
   public void reset() {
   }

   @Override
   public void asArtifactChange(OseeChange change) throws OseeCoreException {
      artifactData.put(change.getItemId(), change);
   }

   @Override
   public void asAttributeChange(OseeChange change) throws OseeCoreException {
      attributeData.put(change.getItemId(), change);
   }

   @Override
   public void asRelationChange(OseeChange change) throws OseeCoreException {
      relationData.put(change.getItemId(), change);
   }

   @Override
   public void resolve() throws OseeCoreException {

   }

   private void getNetChange(Collection<OseeChange> changes) {
      ModificationType totalModType = null;
      for (OseeChange change : changes) {
         totalModType = getNetModificationType(totalModType, change.getCurrentSourceModType());
      }
   }

   private ModificationType getNetModificationType(ModificationType previous, ModificationType current) {
      ModificationType toReturn;
      if (previous == null) {
         toReturn = current;
      } else {
         // Calculate something here
         toReturn = null;
      }
      return toReturn;
   }
}
