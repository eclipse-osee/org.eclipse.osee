/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.testscript.internal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.accessor.types.ArtifactAccessorResult;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.rest.model.transaction.Attribute;
import org.eclipse.osee.orcs.rest.model.transaction.CreateArtifact;

/**
 * @author Ryan T. Baldwin
 */
public class ScriptLogToken extends ArtifactAccessorResult {

   public static final ScriptLogToken SENTINEL = new ScriptLogToken();

   private String logLevel;
   private String logger;
   private String logMessage;
   private String logThrowable;

   public ScriptLogToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public ScriptLogToken(ArtifactReadable art) {
      super(art);
      this.setId(art.getId());
      this.setName(art.getName());
      this.setLogLevel(art.getSoleAttributeAsString(CoreAttributeTypes.LogLevel, ""));
      this.setLogger(art.getSoleAttributeAsString(CoreAttributeTypes.Logger, ""));
      this.setLogMessage(art.getSoleAttributeAsString(CoreAttributeTypes.LogMessage, ""));
      this.setLogThrowable(art.getSoleAttributeAsString(CoreAttributeTypes.LogThrowable, ""));
   }

   public ScriptLogToken(Long id, String name) {
      super(id, name);
   }

   public ScriptLogToken() {
      super();
   }

   public String getLogLevel() {
      return logLevel;
   }

   public void setLogLevel(String logLevel) {
      this.logLevel = logLevel;
   }

   public String getLogger() {
      return logger;
   }

   public void setLogger(String logger) {
      this.logger = logger;
   }

   public String getLogMessage() {
      return logMessage;
   }

   public void setLogMessage(String logMessage) {
      this.logMessage = logMessage;
   }

   public String getLogThrowable() {
      return logThrowable;
   }

   public void setLogThrowable(String logThrowable) {
      this.logThrowable = logThrowable;
   }

   public CreateArtifact createArtifact(String key) {
      Map<AttributeTypeToken, String> values = new HashMap<>();
      values.put(CoreAttributeTypes.Logger, this.getLogger());
      values.put(CoreAttributeTypes.LogLevel, this.getLogLevel());
      values.put(CoreAttributeTypes.LogMessage, this.getLogMessage());
      values.put(CoreAttributeTypes.LogThrowable, this.getLogThrowable());

      CreateArtifact art = new CreateArtifact();
      art.setName(this.getName());
      art.setTypeId(CoreArtifactTypes.ScriptLog.getIdString());

      List<Attribute> attrs = new LinkedList<>();

      for (AttributeTypeToken type : CoreArtifactTypes.ScriptLog.getValidAttributeTypes()) {
         String value = values.get(type);
         if (Strings.isInValid(value)) {
            continue;
         }
         Attribute attr = new Attribute(type.getIdString());
         attr.setValue(Arrays.asList(value));
         attrs.add(attr);
      }

      art.setAttributes(attrs);

      art.setkey(key);

      return art;
   }

}
