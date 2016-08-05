/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.model.writer.config;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.orcs.rest.model.writer.OrcsWriterToken;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class OrcsWriterInputConfig {

   private final List<Long> includeArtifactTypes = new ArrayList<>();
   private final List<Long> includeAttributeTypes = new ArrayList<>();
   private final List<OrcsWriterRelationSide> includeRelationSideTypes = new ArrayList<>();
   private final List<OrcsWriterToken> includeTokens = new ArrayList<>();

   public List<Long> getIncludeArtifactTypes() {
      return includeArtifactTypes;
   }

   public List<Long> getIncludeAttributeTypes() {
      return includeAttributeTypes;
   }

   public List<OrcsWriterRelationSide> getIncludeRelationTypes() {
      return includeRelationSideTypes;
   }

   public List<OrcsWriterToken> getIncludeTokens() {
      return includeTokens;
   }

}
