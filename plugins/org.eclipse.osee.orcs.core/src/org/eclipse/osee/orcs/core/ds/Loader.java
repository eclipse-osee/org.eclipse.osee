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
package org.eclipse.osee.orcs.core.ds;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Andrew M. Finkbeiner
 */
public interface Loader {

   Loader setOptions(Options sourceOptions);

   Loader includeDeleted();

   Loader includeDeleted(boolean enabled);

   boolean areDeletedIncluded();

   Loader fromTransaction(int transactionId);

   int getFromTransaction();

   Loader headTransaction();

   boolean isHeadTransaction();

   LoadLevel getLoadLevel();

   Loader setLoadLevel(LoadLevel loadLevel);

   Loader resetToDefaults();

   Loader loadAttributeType(IAttributeType... attributeType) throws OseeCoreException;

   Loader loadAttributeTypes(Collection<? extends IAttributeType> attributeTypes) throws OseeCoreException;

   Loader loadRelationType(IRelationType... relationType) throws OseeCoreException;

   Loader loadRelationTypes(Collection<? extends IRelationType> relationType) throws OseeCoreException;

}
