/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.model;

import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.ArtifactType;

/**
 * @author Roberto E. Escobar
 */
public final class DefaultBasicArtifact extends NamedIdentity implements IBasicArtifact<Object> {

   private static final long serialVersionUID = -4997763989583925345L;
   private final int artId;

   public DefaultBasicArtifact(int artId, String guid, String name) {
      super(guid, name);
      this.artId = artId;
   }

   @Override
   public int getArtId() {
      return artId;
   }

   @Override
   public Object getFullArtifact() throws OseeCoreException {
      return null;
   }

   @Override
   public ArtifactType getArtifactType() {
      return null;
   }

   @Override
   public Branch getBranch() {
      return null;
   }
}