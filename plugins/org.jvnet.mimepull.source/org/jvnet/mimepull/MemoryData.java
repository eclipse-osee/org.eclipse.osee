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

import java.nio.ByteBuffer;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Keeps the Part's partial content data in memory.
 *
 * @author Kohsuke Kawaguchi
 * @author Jitendra Kotamraju
 */
final class MemoryData implements Data {
    private static final Logger LOGGER = Logger.getLogger(MemoryData.class.getName());

    private final byte[] data;
    private final int len;
    private final MIMEConfig config;

    MemoryData(ByteBuffer buf, MIMEConfig config) {
        data = buf.array();
        len = buf.limit();
        this.config = config;
    }

    // size of the chunk given by the parser
    public int size() {
        return len;
    }

    public byte[] read() {
        return data;
    }

    public long writeTo(DataFile file) {
        return file.writeTo(data, 0, len);
    }

    /**
     * 
     * @param dataHead
     * @param buf
     * @return
     */
    public Data createNext(DataHead dataHead, ByteBuffer buf) {
        if (!config.isOnlyMemory() && dataHead.inMemory >= config.memoryThreshold) {
            try {
                String prefix = config.getTempFilePrefix();
                String suffix = config.getTempFileSuffix();
                File dir = config.getTempDir();
                File tempFile = (dir == null)
                        ? File.createTempFile(prefix, suffix)
                        : File.createTempFile(prefix, suffix, dir);
                LOGGER.fine("Created temp file = "+tempFile);
                dataHead.dataFile = new DataFile(tempFile);
            } catch(IOException ioe) {
                throw new MIMEParsingException(ioe);
            }

            if (dataHead.head != null) {
                for(Chunk c=dataHead.head; c != null; c=c.next) {
                    long pointer = c.data.writeTo(dataHead.dataFile);
                    c.data = new FileData(dataHead.dataFile, pointer, len);
                }
            }
            return new FileData(dataHead.dataFile, buf);
        } else {
            return new MemoryData(buf, config);
        }
    }
}
