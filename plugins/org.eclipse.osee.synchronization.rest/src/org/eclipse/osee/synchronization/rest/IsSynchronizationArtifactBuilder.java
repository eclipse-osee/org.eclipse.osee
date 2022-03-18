/*********************************************************************
 * Copyright (c) 2022 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.synchronization.rest;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Classes in the OSGi <code>org.eclipse.oss.synchronization</code> bundle that are annotated with
 * {@link IsSynchronizationArtifactBuilder} will become eligible for building Synchronization Artifacts. The annotated
 * class is expected to implement the {@link SynchronizationArtifactBuilder} interface. The value of the annotation
 * parameter <code>artifactType</code> specifies the type of Synchronization Artifact that the builder is for.
 *
 * @author Loren.K.Ashley
 */

@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface IsSynchronizationArtifactBuilder {
   public String artifactType();
}

/* EOF */
