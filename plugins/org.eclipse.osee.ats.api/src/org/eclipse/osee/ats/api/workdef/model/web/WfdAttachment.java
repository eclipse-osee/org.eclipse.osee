/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.api.workdef.model.web;

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
public class WfdAttachment extends NamedIdBase {

   public String uri;

   public WfdAttachment() {
      // for jax-rs
   }

   public WfdAttachment(Long id, String name) {
      // for jax-rs
   }

   public String getUri() {
      return uri;
   }

   public void setUri(String uri) {
      this.uri = uri;
   }

   public static WfdAttachment valueOf(ArtifactToken art) {
      WfdAttachment attach = new WfdAttachment(art.getId(), art.getName());
      attach.setId(art.getId());
      attach.setName(art.getName());
      return attach;
   }

}
