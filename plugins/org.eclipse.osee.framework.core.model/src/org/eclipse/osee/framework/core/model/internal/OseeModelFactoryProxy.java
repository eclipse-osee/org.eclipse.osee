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
package org.eclipse.osee.framework.core.model.internal;

import org.eclipse.osee.framework.core.model.BranchFactory;
import org.eclipse.osee.framework.core.model.OseeModelFactoryService;
import org.eclipse.osee.framework.core.model.TransactionRecordFactory;
import org.eclipse.osee.framework.core.model.type.ArtifactTypeFactory;
import org.eclipse.osee.framework.core.model.type.AttributeTypeFactory;
import org.eclipse.osee.framework.core.model.type.OseeEnumTypeFactory;
import org.eclipse.osee.framework.core.model.type.RelationTypeFactory;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;

/**
 * @author Roberto E. Escobar
 */
public class OseeModelFactoryProxy implements IOseeModelFactoryService {
   private IOseeModelFactoryService service;

   public void start() {
      service =
         new OseeModelFactoryService(new BranchFactory(), new TransactionRecordFactory(), new ArtifactTypeFactory(),
            new AttributeTypeFactory(), new RelationTypeFactory(), new OseeEnumTypeFactory());

   }

   public void stop() {
      service = null;
   }

   @Override
   public BranchFactory getBranchFactory() {
      return service.getBranchFactory();
   }

   @Override
   public TransactionRecordFactory getTransactionFactory() {
      return service.getTransactionFactory();
   }

   @Override
   public ArtifactTypeFactory getArtifactTypeFactory() {
      return service.getArtifactTypeFactory();
   }

   @Override
   public AttributeTypeFactory getAttributeTypeFactory() {
      return service.getAttributeTypeFactory();
   }

   @Override
   public RelationTypeFactory getRelationTypeFactory() {
      return service.getRelationTypeFactory();
   }

   @Override
   public OseeEnumTypeFactory getOseeEnumTypeFactory() {
      return service.getOseeEnumTypeFactory();
   }
}
