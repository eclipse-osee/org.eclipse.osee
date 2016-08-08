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
package org.eclipse.osee.framework.core.data;

/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
public interface IArtifactToken extends ArtifactId, HasArtifactType, HasBranch {
   // composition interface requires no additional methods,fields

   default ArtifactTypeId getArtifactTypeId() {
      return null;
   }
}