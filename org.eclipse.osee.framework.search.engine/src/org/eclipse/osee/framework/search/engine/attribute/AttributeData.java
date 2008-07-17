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
package org.eclipse.osee.framework.search.engine.attribute;

import java.net.URI;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class AttributeData {
   private int artId;
   private long gammaId;
   private int branchId;
   private String value;
   private String uri;
   private String taggerId;

   protected AttributeData(int artId, long gammaId, int branchId, String value, String uri, String taggerId) {
      super();
      this.artId = artId;
      this.gammaId = gammaId;
      this.branchId = branchId;
      this.value = value;
      this.uri = uri;
      this.taggerId = Strings.isValid(taggerId) ? taggerId : "";
   }

   public int getArtId() {
      return artId;
   }

   public int getBranchId() {
      return branchId;
   }

   public long getGammaId() {
      return gammaId;
   }

   public String getTaggerId() {
      return taggerId;
   }

   public String getStringValue() {
      return this.value;
   }

   public String getUri() {
      return this.uri;
   }

   public boolean isUriValid() {
      boolean toReturn = false;
      try {
         String value = getUri();
         if (value != null && value.length() > 0) {
            URI uri = new URI(value);
            if (uri != null) {
               toReturn = true;
            }
         }
      } catch (Exception ex) {
         // DO NOTHING
      }
      return toReturn;
   }
}
