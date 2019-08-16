/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.IdSerializer;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.type.NamedIdDescription;

/**
 * @author Ryan D. Brooks
 */
@JsonSerialize(using = IdSerializer.class)
public interface TaggerTypeToken extends NamedId {
   TaggerTypeToken SENTINEL = valueOf(Id.SENTINEL, Named.SENTINEL, "");
   TaggerTypeToken PlainTextTagger =
      valueOf(1L, "Plain Text Tagger", "Tagger that tokenizes on whitespace and punctuation.");
   TaggerTypeToken XmlTagger = valueOf(2L, "XML Tagger", "Tagger that idetified text node conent as tags.");

   public static TaggerTypeToken valueOf(Long id, String name, String description) {
      final class TaggerTypeTokenImpl extends NamedIdDescription implements TaggerTypeToken {
         public TaggerTypeTokenImpl(Long id, String name, String description) {
            super(id, name, description);
         }
      }
      return new TaggerTypeTokenImpl(id, name, description);
   }
}