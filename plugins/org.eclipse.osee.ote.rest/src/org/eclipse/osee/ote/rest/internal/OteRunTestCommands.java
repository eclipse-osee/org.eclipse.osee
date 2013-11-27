package org.eclipse.osee.ote.rest.internal;

import org.eclipse.osee.ote.core.framework.command.RunTests;

public interface OteRunTestCommands {
   RunTests getCommand(String id);

   void putCommand(String string, RunTests envTestRun);
}
