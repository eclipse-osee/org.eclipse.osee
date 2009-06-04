/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
