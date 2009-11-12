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
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public final class DefaultBasicArtifact extends BaseExchangeData implements IBasicArtifact<Object> {

   private static final long serialVersionUID = -4997763989583925345L;

   private static final String ARTIFACT_ID = "artifact.id";
   private static final String ARTIFACT_GUID = "artifact.guid";
   private static final String ARTIFACT_NAME = "artifact.name";

   public DefaultBasicArtifact(int artId, String guid, String name) {
      super();
      backingData.put(ARTIFACT_ID, artId);
      backingData.put(ARTIFACT_GUID, guid);
      backingData.put(ARTIFACT_NAME, name);
   }

   @Override
   public int getArtId() {
      return backingData.getInt(ARTIFACT_ID);
   }

   @Override
   public Object getFullArtifact() throws OseeCoreException {
      return null;
   }

   @Override
   public String getGuid() {
      return getString(ARTIFACT_GUID);
   }

   @Override
   public String getName() {
      return getString(ARTIFACT_NAME);
   }

}
