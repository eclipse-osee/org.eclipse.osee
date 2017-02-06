/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.attribute;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactReferenceAttribute extends IdentityReferenceAttribute<Artifact> {

   Long rawValue;

   @Override
   protected boolean subClassSetValue(Artifact artifact) throws OseeCoreException {
      rawValue = artifact == null ? null : artifact.getUuid();
      return getAttributeDataProvider().setValue(artifact == null ? "" : artifact.getIdString());
   }

   public Long getRawValue() {
      return rawValue;
   }
}