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
package org.eclipse.osee.orcs.db.internal.loader.data;

import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.VersionData;

/**
 * @author Roberto E. Escobar
 */
public interface RelationObjectFactory extends VersionObjectFactory {

   RelationData createRelationData(VersionData version, int localId, int localTypeID, ModificationType modType, int aArtId, int bArtId, String rationale) throws OseeCoreException;

   RelationData createRelationData(VersionData version, int localId, IRelationType type, ModificationType modType, int aArtId, int bArtId, String rationale) throws OseeCoreException;

   RelationData createCopy(RelationData source) throws OseeCoreException;
}
