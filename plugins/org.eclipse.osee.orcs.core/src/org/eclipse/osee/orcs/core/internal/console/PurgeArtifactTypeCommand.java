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
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.data.ArtifactTypes;

/**
 * @author Angel Avila
 */
public class PurgeArtifactTypeCommand implements ConsoleCommand {

   private OrcsApi orcsApi;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public OrcsApi getOrcsApi() {
      return orcsApi;
   }

   @Override
   public String getName() {
      return "purge_artifact_type";
   }

   @Override
   public String getDescription() {
      return "Purges artifact type instances from datastore";
   }

   @Override
   public String getUsage() {
      return "[force=<TRUE|FALSE>] types=<ARTIFACT_TYPE_UUID,...>";
   }

   @Override
   public Callable<?> createCallable(final Console console, final ConsoleParameters params) {
      final OrcsTypes orcsTypes = orcsApi.getOrcsTypes();
      return new Callable<Void>() {

         @Override
         public Void call() throws Exception {
            boolean forcePurge = params.getBoolean("force");
            String[] typesToPurge = params.getArray("types");

            console.writeln();
            console.writeln(!forcePurge ? "Artifact Types" : "Purging artifact types:");

            Set<IArtifactType> types = getTypes(typesToPurge);
            boolean found = !types.isEmpty();

            if (forcePurge && found) {
               orcsTypes.purgeArtifactsByArtifactType(types).call();
            }
            console.writeln(
               found && !forcePurge ? "To >DELETE Artifact DATA!< add --force to confirm." : "Operation finished.");
            return null;
         }

         private Set<IArtifactType> getTypes(String[] typesToPurge) {
            ArtifactTypes artifactTypes = orcsTypes.getArtifactTypes();
            Set<IArtifactType> toReturn = new HashSet<>();
            for (String uuid : typesToPurge) {
               try {
                  Long typeId = Long.valueOf(uuid);
                  IArtifactType type = artifactTypes.get(typeId);
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
