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
package org.eclipse.osee.framework.core.services;

import org.eclipse.osee.framework.core.model.ArtifactTypeFactory;
import org.eclipse.osee.framework.core.model.AttributeTypeFactory;
import org.eclipse.osee.framework.core.model.BranchFactory;
import org.eclipse.osee.framework.core.model.OseeEnumTypeFactory;
import org.eclipse.osee.framework.core.model.RelationTypeFactory;
import org.eclipse.osee.framework.core.model.TransactionRecordFactory;

/**
 * @author Roberto E. Escobar
 */
public interface IOseeModelFactoryService {

   BranchFactory getBranchFactory();

   TransactionRecordFactory getTransactionFactory();

   ArtifactTypeFactory getArtifactTypeFactory();

   AttributeTypeFactory getAttributeTypeFactory();

   RelationTypeFactory getRelationTypeFactory();

   OseeEnumTypeFactory getOseeEnumTypeFactory();
}
