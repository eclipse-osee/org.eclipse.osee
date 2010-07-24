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
package org.eclipse.osee.framework.core.message;

import org.eclipse.osee.framework.core.enums.ModificationType;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactChangeItem extends ChangeItem {

   public ArtifactChangeItem(int artId, int artTypeId, long currentSourceGammaId, ModificationType currentSourceModType) {
      super(artId, artTypeId, artId, currentSourceGammaId, currentSourceModType);
   }
}
