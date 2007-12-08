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

import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;

/**
 * @author Robert A. Fisher
 */
public interface IRelationLinkChange extends IRevisionChange {

   /**
    * @return Returns the otherArtifactDescriptor.
    */
   public ArtifactSubtypeDescriptor getOtherArtifactDescriptor();

   /**
    * @return Returns the otherArtifactName.
    */
   public String getOtherArtifactName();

   /**
    * @return Returns the rationale.
    */
   public String getRationale();

   /**
    * @return Returns the relTypeName.
    */
   public String getRelTypeName();

}