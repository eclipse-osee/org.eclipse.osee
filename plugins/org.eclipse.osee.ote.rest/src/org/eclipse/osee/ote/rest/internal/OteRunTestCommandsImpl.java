package org.eclipse.osee.ote.rest.internal;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.osee.ote.core.framework.command.RunTests;

public class OteRunTestCommandsImpl implements OteRunTestCommands {

   private Map<String,  WeakReference<RunTests>> tests;
   
   public OteRunTestCommandsImpl(){
      tests = new HashMap<>();
   }
   
   @Override
   public RunTests getCommand(String id) {
      WeakReference<RunTests> ref = tests.get(id);
      return ref.get();
   }

   @Override
   public void putCommand(String id, RunTests envTestRun) {
      tests.put(id, new WeakReference<RunTests>(envTestRun));
   }

}
