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

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;

/**
 * @author Roberto E. Escobar
 */
public interface IChangeFactory {

   // TODO make more specific
   public OseeChange createAttributeChange(TxChange txChange, int gammaId, ModificationType modificationType, int itemId);

   public OseeChange createArtifactChange(TxChange txChange, int gammaId, ModificationType modificationType, int itemId);

   public OseeChange createRelationChange(TxChange txChange, int gammaId, ModificationType modificationType, int itemId);
}
