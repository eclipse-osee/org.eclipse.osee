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
package org.eclipse.osee.framework.skynet.core.artifact.search;

/**
 * @author Ryan D. Brooks
 */
public abstract class AbstractArtifactSearchCriteria {

   public abstract void addToTableSql(ArtifactQueryBuilder builder);

   public abstract void addToWhereSql(ArtifactQueryBuilder builder);

   public void addJoinSql(ArtifactQueryBuilder builder, AbstractArtifactSearchCriteria rightCriteria) {

   }

   public void addJoinArtId(ArtifactQueryBuilder builder) {
   }
}