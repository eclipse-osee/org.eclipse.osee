/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.store;

import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.TokenFactory;

/**
 * @author Ryan D. Brooks
 */
public final class CoverageAttributeTypes {

   // @formatter:off
   public static final IAttributeType Assignees = TokenFactory.createAttributeType(0x1000000000000101L, "coverage.Assignees");
   public static final IAttributeType Item = TokenFactory.createAttributeType(0x1000000000000104L, "coverage.Coverage Item");
   public static final IAttributeType Options = TokenFactory.createAttributeType(0x10000000000000FDL, "coverage.Coverage Options");
   public static final IAttributeType FileContents = TokenFactory.createAttributeType(0x10000000000000FEL, "coverage.File Contents");
   public static final IAttributeType Location = TokenFactory.createAttributeType(0x1000000000000103L, "coverage.Location");
   public static final IAttributeType Namespace = TokenFactory.createAttributeType(0x1000000000000105L, "coverage.Namespace");
   public static final IAttributeType WorkProductTaskGuid = TokenFactory.createAttributeType(0x10000000000000FFL, "coverage.WorkProductTaskGuid");
   public static final IAttributeType WorkProductPcrGuid = TokenFactory.createAttributeType(0x1000000000000100L, "coverage.WorkProductPcrGuid");
   public static final IAttributeType Notes = TokenFactory.createAttributeType(0x10000000000000FCL, "coverage.Notes");
   public static final IAttributeType Order = TokenFactory.createAttributeType(0x1000000000000102L, "coverage.Order");
   public static final IAttributeType UnitTestTable = TokenFactory.createAttributeType(0x100000000000037BL, "coverage.UnitTestTable");
   // @formatter:on

   private CoverageAttributeTypes() {
      // Constants
   }
}