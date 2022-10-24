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

package org.eclipse.osee.define.operations.publishing.templatemanager;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Classes in the OSGI <code>org.eclipse.osee.define.rest</code> bundle that are annotated with
 * {@link IsPublishingTemplateProvider} will be instantiated by the {@link TemplateMangerOperationsImpl} instance. The
 * value of the annotation parameter <code>key</code> must be unique for all classes with the annotation. When
 * Publishing Templates are requested by a unique Publishing Template identifier, the identifier string contains a
 * prefix which uniquely specifies the {@link PublishingTemplateProvider} implementation that can provide the Publishing
 * Template. The annotation parameter <code>key</code> is the identifier prefix.
 *
 * @author Loren K. Ashley
 */

@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface IsPublishingTemplateProvider {
   public String key();
}

/* EOF */
