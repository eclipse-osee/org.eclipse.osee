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

import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.VersionData;

/**
 * @author Roberto E. Escobar
 */
public interface AttributeObjectFactory extends VersionObjectFactory {

   AttributeData createAttributeData(VersionData version, int localId, long localTypeID, ModificationType modType, int artId, String value, String uri) throws OseeCoreException;

   AttributeData createAttributeData(VersionData version, int localId, IAttributeType type, ModificationType modType, int artId) throws OseeCoreException;

   AttributeData createCopy(AttributeData source) throws OseeCoreException;
}
