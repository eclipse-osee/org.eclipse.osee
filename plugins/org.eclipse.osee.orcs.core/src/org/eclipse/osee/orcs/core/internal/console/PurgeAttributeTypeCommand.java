/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.core.internal.console;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleCommand;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsTypes;

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
      final OrcsTypes orcsTypes = orcsApi.getOrcsTypes();
      final OrcsTokenService tokenService = orcsApi.tokenService();
      return new Callable<Void>() {

         @Override
         public Void call() throws Exception {
            boolean forcePurge = params.getBoolean("force");
            String[] typesToPurge = params.getArray("types");

            console.writeln();

            Set<AttributeTypeId> types = getTypes(typesToPurge);
            boolean found = !types.isEmpty();

            if (found) {
               console.writeln(!forcePurge ? String.format("Attribute Types: [%s]",
                  types) : String.format("Purging attribute types: [%s]", types));

               if (forcePurge && found) {
                  orcsTypes.purgeAttributesByAttributeType(types).call();
               }
               console.writeln(
                  found && !forcePurge ? "To >DELETE Attribute DATA!< add \"force=TRUE\" to confirm." : "Operation finished.");
            } else {
               console.writeln("No types found.  Aborting...");
            }
            return null;
         }

         private Set<AttributeTypeId> getTypes(String[] typesToPurge) {
            Set<AttributeTypeId> toReturn = new HashSet<>();
            for (String uuid : typesToPurge) {
               try {
                  Long typeId = Long.valueOf(uuid);
                  AttributeTypeId type = tokenService.getAttributeTypeOrCreate(typeId);
                  console.writeln("Type [%s] found.", type);
                  toReturn.add(type);
               } catch (OseeArgumentException ex) {
                  console.writeln("Type [%s] NOT found.", uuid);
                  console.writeln(ex);
               }
            }
            return toReturn;
         }
      };
   }
}
