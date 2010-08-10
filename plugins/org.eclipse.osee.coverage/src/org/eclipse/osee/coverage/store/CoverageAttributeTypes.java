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
import org.eclipse.osee.framework.core.data.NamedIdentity;

/**
 * @author Ryan D. Brooks
 */
public class CoverageAttributeTypes extends NamedIdentity implements IAttributeType {
   // @formatter:off
   public static final CoverageAttributeTypes Assignees = new CoverageAttributeTypes("AARCA+XjSyKjnh3sweQA", "coverage.Assignees");
   public static final CoverageAttributeTypes Item = new CoverageAttributeTypes("AARr8BmsQHKLOHNzOcQA", "coverage.Coverage Item");
   public static final CoverageAttributeTypes Options = new CoverageAttributeTypes("AAF+8+sqyELZ2mdVV_AA", "coverage.Coverage Options");
   public static final CoverageAttributeTypes FileContents = new CoverageAttributeTypes("AARDJK8YAT3SDnghjQgA", "coverage.File Contents");
   public static final CoverageAttributeTypes Location = new CoverageAttributeTypes("AARA2XwhNRddgQrd0iwA", "coverage.Location");
   public static final CoverageAttributeTypes Namespace = new CoverageAttributeTypes("AAQ_v6uUrh0j39+4D5gA", "coverage.Namespace");
   public static final CoverageAttributeTypes Notes = new CoverageAttributeTypes("AARERmIjazD1udUwfLgA", "coverage.Notes");
   public static final CoverageAttributeTypes Order = new CoverageAttributeTypes("AD72opMBR1pFxB0hVpQA", "coverage.Order");
      // @formatter:on

   private CoverageAttributeTypes(String guid, String name) {
      super(guid, name);
   }
}