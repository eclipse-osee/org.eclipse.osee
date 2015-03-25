package org.eclipse.osee.ote.internal;

import org.eclipse.osee.ote.OTEApi;
import org.eclipse.osee.ote.endpoint.OteUdpEndpoint;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.remote.messages.TestEnvironmentSetBatchMode;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

public class SetBatchModeListener implements EventHandler {

   private OTEApi oteApi;

   public SetBatchModeListener(EventAdmin eventAdmin, OteUdpEndpoint oteEndpoint, OTEApi oteApi) {
      this.oteApi = oteApi;
   }

   @Override
   public void handleEvent(Event arg0) {
      if(oteApi.getTestEnvironment() == null){
         return;
      }
      TestEnvironmentSetBatchMode testEnvironmentSetBatchMode = new TestEnvironmentSetBatchMode(OteEventMessageUtil.getBytes(arg0));
      oteApi.getTestEnvironment().setBatchMode(testEnvironmentSetBatchMode.SET_BATCH_MODE.getValue());
   }

}
