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

/**
 * Configuration for MIME message parsing and storing.
 *
 * @author Jitendra Kotamraju
 */
public class MIMEConfig {

    private static final int DEFAULT_CHUNK_SIZE = 8192;
    private static final long DEFAULT_MEMORY_THRESHOLD = 1048576L;
    private static final String DEFAULT_FILE_PREFIX = "MIME";

    // Parses the entire message eagerly
    boolean parseEagerly;

    // Approximate Chunk size
    int chunkSize;

    // Maximum in-memory data per attachment
    long memoryThreshold;

    // Do not store to disk
    boolean onlyMemory;

    // temp Dir to store large files
    File tempDir;
    String prefix;
    String suffix;


    private MIMEConfig(boolean parseEagerly, int chunkSize,
                       long inMemoryThreshold, String dir, String prefix, String suffix) {
        this.parseEagerly = parseEagerly;
        this.chunkSize = chunkSize;
        this.memoryThreshold = inMemoryThreshold;
        this.prefix = prefix;
        this.suffix = suffix;
        setDir(dir);
    }

    public MIMEConfig() {
        this(false, DEFAULT_CHUNK_SIZE, DEFAULT_MEMORY_THRESHOLD, null,
                DEFAULT_FILE_PREFIX, null);
    }

    boolean isParseEagerly() {
        return parseEagerly;
    }

    public void setParseEagerly(boolean parseEagerly) {
        this.parseEagerly = parseEagerly;
    }

    int getChunkSize() {
        return chunkSize;
    }

    void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    long getMemoryThreshold() {
        return memoryThreshold;
    }

    /**
     * If the attachment is greater than the threshold, it is
     * written to the disk.
     *
     * @param memoryThreshold no of bytes per attachment
     *        if -1, then the whole attachment is kept in memory
     */
    public void setMemoryThreshold(long memoryThreshold) {
        this.memoryThreshold = memoryThreshold;
    }

    boolean isOnlyMemory() {
        return memoryThreshold == -1L;
    }

    File getTempDir() {
        return tempDir;
    }

    String getTempFilePrefix() {
        return prefix;
    }

    String getTempFileSuffix() {
        return suffix;
    }

    /**
     * @param dir
     */
    public void setDir(String dir) {
        if (tempDir == null && dir != null && !dir.equals("")) {
            tempDir = new File(dir);
        }
    }

    /**
     * Validates if it can create temporary files. Otherwise, it stores
     * attachment contents in memory.
     */
    public void validate() {
        if (!isOnlyMemory()) {
            try {
                File tempFile = (tempDir == null)
                        ? File.createTempFile(prefix, suffix)
                        : File.createTempFile(prefix, suffix, tempDir);
                tempFile.delete();
            } catch(Exception ioe) {
                memoryThreshold = -1L;      // whole attachment will be in-memory
            }
        }
    }

}
