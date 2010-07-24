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
package org.eclipse.osee.ats.artifact;

import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.NamedIdentity;

/**
 * @author Ryan D. Brooks
 */
public class AtsAttributeTypes extends NamedIdentity implements IAttributeType {
   public static final AtsAttributeTypes LegacyPCRId = new AtsAttributeTypes("AAMFEd3TakphMtQX1zgA",
      "ats.Legacy PCR Id");

   public static final AtsAttributeTypes Active = new AtsAttributeTypes("AAMFEclQOVmzkIvzyWwA", "ats.Active");
   public static final AtsAttributeTypes Resolution = new AtsAttributeTypes("AAMFEdUMfV1KdbQNaKwA", "ats.Resolution");
   public static final AtsAttributeTypes TeamDefinition = new AtsAttributeTypes("AAMFEdd5bFEe18bd0lQA",
      "ats.Team Definition");

   public static final AtsAttributeTypes AtsLog = new AtsAttributeTypes("AAMFEdgB1DX3eJSZb0wA", "ats.Log");
   public static final AtsAttributeTypes AtsState = new AtsAttributeTypes("AAMFEdMa3wzVvp60xLQA", "ats.State");
   public static final AtsAttributeTypes AtsCurrentState = new AtsAttributeTypes("AAMFEdOWL3u6hmX2VbwA",
      "ats.Current State");

   private AtsAttributeTypes(String guid, String name) {
      super(guid, name);
   }
}