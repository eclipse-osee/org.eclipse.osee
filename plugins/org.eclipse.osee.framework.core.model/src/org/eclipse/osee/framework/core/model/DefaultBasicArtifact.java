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

import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.jdk.core.type.NamedIdentity;

/**
 * @author Roberto E. Escobar
 */
public final class DefaultBasicArtifact extends NamedIdentity<String> implements IBasicArtifact<Object> {

   private final Long artId;

   public DefaultBasicArtifact(long artId, String guid, String name) {
      super(guid, name);
      this.artId = artId;
   }

   @Override
   public int getArtId() {
      return artId.intValue();
   }

   @Override
   public Object getFullArtifact() {
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

   @Override
   public Long getId() {
      return artId;
   }
}