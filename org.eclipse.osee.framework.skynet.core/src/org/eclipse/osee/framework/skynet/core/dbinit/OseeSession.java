/*
 * Created on Sep 18, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.dbinit;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;

/**
 * @author Donald G. Dunne
 */
public class OseeSession implements Serializable {

   private static final long serialVersionUID = 1L;
   private String id = GUID.generateGuidStr();
   private String machineName;
   private String userId;
   private String machineIp;

   public OseeSession() {
      try {
         this.userId = String.valueOf(SkynetAuthentication.getSafeUserId());
         this.machineIp = InetAddress.getLocalHost().getHostAddress();
         this.machineName = InetAddress.getLocalHost().getHostName();
      } catch (Exception ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.toString(), ex);
      }
   }

   public OseeSession(String id, String machineName, String userId, String machineIp) {
      this.id = id;
      this.machineName = machineName;
      this.machineIp = machineIp;
      this.userId = userId;
   }

   @Override
   public String toString() {
      return "SessionId:" + id + " UserId:" + userId + " Machine:" + machineName + " IP:" + machineIp;
   }

   /**
    * @return the id
    */
   public String getId() {
      return id;
   }

   /**
    * @return the machineName
    */
   public String getMachineName() {
      return machineName;
   }

   /**
    * @return the userId
    */
   public String getUserId() {
      return userId;
   }

   /**
    * @return the machineIp
    */
   public String getMachineIp() {
      return machineIp;
   }

}
