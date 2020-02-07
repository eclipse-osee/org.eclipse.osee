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
package org.eclipse.define.api.importing;

import java.util.function.Function;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author David W. Miller
 */
public class BlockFieldToken extends NamedIdBase {
   protected final Pattern typeRegex;
   public final Pattern contentRegex;
   private final AttributeTypeToken oseeType;
   private final Function<BlockFieldToken, BlockField> parser;

   public BlockFieldToken(Long id, String name, String typeRegex, String contentRegex, Function<BlockFieldToken, BlockField> parser, AttributeTypeToken oseeType) {
      super(id, name);
      this.typeRegex = Pattern.compile(typeRegex);
      this.contentRegex = Pattern.compile(contentRegex, Pattern.DOTALL); // DOTALL is important for block attr text subclass
      this.oseeType = oseeType;
      this.parser = parser;
   }

   public static BlockFieldToken valueOf(long id, String name, String typeRegex, String contentRegex, Function<BlockFieldToken, BlockField> parser, AttributeTypeToken oseeType) {
      return new BlockFieldToken(id, name, typeRegex, contentRegex, parser, oseeType);
   }

   public static BlockFieldToken valueOf(long id, String name, String typeRegex, String contentRegex, Function<BlockFieldToken, BlockField> parser) {
      return valueOf(id, name, typeRegex, contentRegex, parser, AttributeTypeToken.SENTINEL);
   }

   public String getImportTypeName() {
      return this.getName();
   }

   public AttributeTypeToken getOseeType() {
      return oseeType;
   }

   public Pattern getTypeRegex() {
      return typeRegex;
   }

   public Pattern getContentRegex() {
      return contentRegex;
   }

   public BlockField getNewParser() {
      return parser.apply(this);
   }
}
