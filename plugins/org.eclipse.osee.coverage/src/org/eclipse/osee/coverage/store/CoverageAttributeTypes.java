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
   public static final IAttributeType Assignees = TokenFactory.createAttributeType("AARCA+XjSyKjnh3sweQA", "coverage.Assignees");
   public static final IAttributeType Item = TokenFactory.createAttributeType("AARr8BmsQHKLOHNzOcQA", "coverage.Coverage Item");
   public static final IAttributeType Options = TokenFactory.createAttributeType("AAF+8+sqyELZ2mdVV_AA", "coverage.Coverage Options");
   public static final IAttributeType FileContents = TokenFactory.createAttributeType("AARDJK8YAT3SDnghjQgA", "coverage.File Contents");
   public static final IAttributeType Location = TokenFactory.createAttributeType("AARA2XwhNRddgQrd0iwA", "coverage.Location");
   public static final IAttributeType Namespace = TokenFactory.createAttributeType("AAQ_v6uUrh0j39+4D5gA", "coverage.Namespace");
   public static final IAttributeType WorkProductTaskGuid = TokenFactory.createAttributeType("A+m7Y2sV2z83QUlkzIAA", "coverage.WorkProductTaskGuid");
   public static final IAttributeType WorkProductPcrGuid = TokenFactory.createAttributeType("AWuQmRuq1gwrejEWRgAA", "coverage.WorkProductPcrGuid");
   public static final IAttributeType Notes = TokenFactory.createAttributeType("AARERmIjazD1udUwfLgA", "coverage.Notes");
   public static final IAttributeType Order = TokenFactory.createAttributeType("AD72opMBR1pFxB0hVpQA", "coverage.Order");
   // @formatter:on

   private CoverageAttributeTypes() {
      // Constants
   }
}