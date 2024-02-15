/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.framework.core.renderer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.eclipse.osee.framework.core.publishing.FormatIndicator;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * Enumeration to indicate where an artifact render is to be performed.
 *
 * @author Loren K. Ashley
 */

@JsonSerialize(using = RenderLocationSerializer.class)
@JsonDeserialize(using = RenderLocationDeserializer.class)
public enum RenderLocation {

   /**
    * The render is to be performed in the Eclipse client.
    */

   CLIENT(" (Client)"),

   /**
    * The render is to be performed on the OSEE server.
    */

   SERVER(" (Server)");

   /**
    * The JSON field name used for serializations of {@link FormatIndicator} objects.
    */

   static final String jsonObjectName = "renderLocation";

   /**
    * A string to annotate menu commands to indicated the location of the render.
    */

   private final String menuCommandAnnotation;

   /**
    * Creates new enumeration members.
    *
    * @param menuCommandIndicator
    */

   private RenderLocation(String menuCommandIndicator) {
      this.menuCommandAnnotation = Conditions.requireNonNull(menuCommandIndicator);
   }

   /**
    * Gets the menu command annotation for the render location.
    *
    * @return the menu command annotation.
    */

   public String getMenuCommandAnnotation() {
      return this.menuCommandAnnotation;
   }

   /**
    * Predicate to determine if the enumeration member is {@link #CLIENT}.
    *
    * @return <code>true</code> when this member is the {@link #CLIENT} member; otherwise, <code>false</code>.
    */

   public boolean isClient() {
      return this == CLIENT;
   }

   /**
    * Predicate to determine if the enumeration member is {@link #CLIENT}.
    *
    * @return <code>true</code> when this member is the {@link #CLIENT} member; otherwise, <code>false</code>.
    */

   public boolean isServer() {
      return this == SERVER;
   }

}

/* EOF */
