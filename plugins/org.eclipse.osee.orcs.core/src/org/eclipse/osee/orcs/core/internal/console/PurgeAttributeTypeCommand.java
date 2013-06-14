/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.console;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleCommand;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.HexUtil;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.data.AttributeTypes;

/**
 * @author Angel Avila
 */
public class PurgeAttributeTypeCommand implements ConsoleCommand {

   private OrcsApi orcsApi;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public OrcsApi getOrcsApi() {
      return orcsApi;
   }

   @Override
   public String getName() {
      return "purge_attribute_type";
   }

   @Override
   public String getDescription() {
      return "Purges attribute type instances from datastore";
   }

   @Override
   public String getUsage() {
      return "[force=<TRUE|FALSE>] types=<ATTRIBUTE_TYPE_UUID,...>";
   }

   @Override
   public Callable<?> createCallable(final Console console, final ConsoleParameters params) {
      final OrcsTypes orcsTypes = orcsApi.getOrcsTypes(null);
      return new Callable<Void>() {

         @Override
         public Void call() throws Exception {
            boolean forcePurge = params.getBoolean("force");
            String[] typesToPurge = params.getArray("types");

            console.writeln();
            console.writeln(!forcePurge ? "Attribute Types" : "Purging attribute types:");

            Set<IAttributeType> types = getTypes(typesToPurge);
            boolean found = !types.isEmpty();

            if (forcePurge && found) {
               orcsTypes.purgeAttributesByAttributeType(types).call();
            }
            console.writeln((found && !forcePurge) ? "To >DELETE Attribute DATA!< add --force to confirm." : "Operation finished.");
            return null;
         }

         private Set<IAttributeType> getTypes(String[] typesToPurge) throws OseeCoreException {
            AttributeTypes attributeTypes = orcsTypes.getAttributeTypes();
            Set<IAttributeType> toReturn = new HashSet<IAttributeType>();
            for (String uuid : typesToPurge) {
               try {
                  Long typeId = HexUtil.toLong(uuid);
                  IAttributeType type = attributeTypes.getByUuid(typeId);
                  console.writeln("Type [%s] found. Guid: [0x%X]", type.getName(), type.getGuid());
                  toReturn.add(type);
               } catch (OseeArgumentException ex) {
                  console.writeln("Type [0x%X] NOT found.", uuid);
                  console.writeln(ex);
               }
            }
            return toReturn;
         }
      };
   }
}
