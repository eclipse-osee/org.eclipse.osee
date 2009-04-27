/*
 * Created on Apr 24, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.plugin.util;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 * @author Donald G. Dunne
 */
public class AudioFile {

   public static void processAudioFile(OseeUiActivator activator, String filePath) {
      AudioStream as = null;
      try {
         URL url = activator.getBundle().getEntry(filePath);
         as = new AudioStream(url.openStream());
         AudioPlayer.player.start(as);
         AudioPlayer.player.join(3000);
      } catch (Exception ex) {
         OseeLog.log(activator.getClass(), Level.SEVERE, ex);
      } finally {
         if (as != null) {
            try {
               as.close();
            } catch (IOException ex) {
               // Do Nothing
            }
         }
      }
   }

}
