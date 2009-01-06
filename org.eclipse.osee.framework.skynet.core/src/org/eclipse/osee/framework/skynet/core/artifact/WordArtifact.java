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
package org.eclipse.osee.framework.skynet.core.artifact;

/**
 * @author Ryan D. Brooks
 */
public class WordArtifact extends Artifact {
   public static final String ARTIFACT_NAME = "Word Artifact";
   public static final String WHOLE_WORD = "Whole Word";
   public static final String WORD_TEMPLATE = "Word Template";

   /**
    * @param parentFactory
    * @param guid
    * @param branch
    */
   public WordArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }
}