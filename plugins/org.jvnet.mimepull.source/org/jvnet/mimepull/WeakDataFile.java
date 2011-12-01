/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package org.jvnet.mimepull;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Removing files based on this
 * <a href="http://java.sun.com/developer/technicalArticles/javase/finalization/">article</a>
 *
 * @author Jitendra Kotamraju
 */
final class WeakDataFile extends WeakReference<DataFile> {

    private static final Logger LOGGER = Logger.getLogger(WeakDataFile.class.getName());
    private static final int MAX_ITERATIONS = 2;
    private static ReferenceQueue<DataFile> refQueue = new ReferenceQueue<DataFile>();
    private static List<WeakDataFile> refList = new ArrayList<WeakDataFile>();
    private final File file;
    private final RandomAccessFile raf;

    WeakDataFile(DataFile df, File file) {
        super(df, refQueue);
        refList.add(this);
        this.file = file;
        try {
            raf = new RandomAccessFile(file, "rw");
        } catch(IOException ioe) {
            throw new MIMEParsingException(ioe);
        }
        drainRefQueueBounded();
    }

    synchronized void read(long pointer, byte[] buf, int offset, int length ) {
        try {
            raf.seek(pointer);
            raf.readFully(buf, offset, length);
        } catch(IOException ioe) {
            throw new MIMEParsingException(ioe);
        }
    }

    synchronized long writeTo(long pointer, byte[] data, int offset, int length) {
        try {
            raf.seek(pointer);
            raf.write(data, offset, length);
            return raf.getFilePointer();    // Update pointer for next write
        } catch(IOException ioe) {
            throw new MIMEParsingException(ioe);
        }
    }

    void close() {
        LOGGER.fine("Deleting file = "+file.getName());
        refList.remove(this);
        try {
            raf.close();
            file.delete();
        } catch(IOException ioe) {
            throw new MIMEParsingException(ioe);
        }
    }

    void renameTo(File f) {
        LOGGER.fine("Moving file="+file+" to="+f);
        refList.remove(this);
        try {
            raf.close();
            file.renameTo(f);
        } catch(IOException ioe) {
            throw new MIMEParsingException(ioe);
        }

    }

    static void drainRefQueueBounded() {
        int iterations = 0;
        WeakDataFile weak = (WeakDataFile) refQueue.poll();
        while (weak != null && iterations < MAX_ITERATIONS) {
            LOGGER.fine("Cleaning file = "+weak.file+" from reference queue.");
            weak.close();
            ++iterations;
            weak = (WeakDataFile) refQueue.poll();
        }
    }

}
