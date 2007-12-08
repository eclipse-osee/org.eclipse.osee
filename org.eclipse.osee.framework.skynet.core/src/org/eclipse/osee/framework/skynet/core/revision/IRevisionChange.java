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

import java.io.Serializable;
import org.eclipse.swt.graphics.Image;

/**
 * @author Robert A. Fisher
 */
public interface IRevisionChange extends Serializable {

   /**
    * @return Returns the image for this change.
    */
   public Image getImage();

   /**
    * This value could be -1 if called from ArtifactChange object where there was no gammaId.
    */
   public long getGammaId();

}