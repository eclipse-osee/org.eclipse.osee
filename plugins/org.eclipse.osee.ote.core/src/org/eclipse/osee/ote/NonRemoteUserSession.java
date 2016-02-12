package org.eclipse.osee.ote;

import java.io.Serializable;
import java.util.UUID;

import org.eclipse.osee.ote.core.IRemoteUserSession;
import org.eclipse.osee.ote.core.OSEEPerson1_4;
import org.eclipse.osee.ote.core.framework.prompt.IPassFailPromptResponse;
import org.eclipse.osee.ote.core.framework.prompt.IResumeResponse;
import org.eclipse.osee.ote.core.framework.prompt.IUserInputPromptResponse;
import org.eclipse.osee.ote.core.framework.prompt.IYesNoPromptResponse;

public class NonRemoteUserSession implements IRemoteUserSession, Serializable {

   /**
    * 
    */
   private static final long serialVersionUID = -4094993884862781475L;
   
   private OSEEPerson1_4 user;
   private String address;
   
   public NonRemoteUserSession(OSEEPerson1_4 user, String address){
      this.user = user;
      this.address = address;
   }
   
   @Override
   public OSEEPerson1_4 getUser() {
      return user;
   }

   @Override
   public String getAddress() {
      return address;
   }

   @Override
   public byte[] getFile(String workspacePath) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public long getFileDate(String workspacePath) {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public void initiateInformationalPrompt(String message) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void initiatePassFailPrompt(IPassFailPromptResponse prompt) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void initiateYesNoPrompt(IYesNoPromptResponse prompt) throws Exception {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void initiateUserInputPrompt(IUserInputPromptResponse prompt) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void initiateResumePrompt(IResumeResponse prompt) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void cancelPrompts() throws Exception {
      // TODO Auto-generated method stub
      
   }

   @Override
   public boolean isAlive() {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public UUID getUserId() throws Exception {
      // TODO Auto-generated method stub
      return null;
   }

}