/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.template.engine.internal;

import java.util.Set;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceToken;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public interface TemplateRegistry {

   public interface TemplateVisitor {
      void onTemplate(Bundle bundle, ResourceToken template);
   }

   ResourceToken resolveTemplate(String viewId, MediaType mediaType);

   IResourceRegistry getResourceRegistry();

   void accept(TemplateVisitor visitor);

   Set<String> getAttributes(ResourceToken template);

}