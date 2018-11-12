/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.program;

import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
public interface IAtsProgram extends IAtsConfigObject {
   // do nothing

   IAtsProgram SENTINEL = createSentinel();

   public static IAtsProgram createSentinel() {
      final class IAtsProgramSentinel extends NamedIdBase implements IAtsProgram {

         public IAtsProgramSentinel() {
            super(Id.SENTINEL, "SENTINEL");

         }

         @Override
         public boolean isActive() {
            return false;
         }

         @Override
         public ArtifactTypeId getArtifactType() {
            return null;
         }

      }
      return new IAtsProgramSentinel();
   }

}
