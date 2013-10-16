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
package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface AttributeDataFactory {

   AttributeData create(ArtifactData parent, IAttributeType attributeType) throws OseeCoreException;

   AttributeData copy(IOseeBranch destination, AttributeData orcsData) throws OseeCoreException;

   AttributeData clone(AttributeData source) throws OseeCoreException;

   AttributeData introduce(IOseeBranch destination, AttributeData source) throws OseeCoreException;

}
