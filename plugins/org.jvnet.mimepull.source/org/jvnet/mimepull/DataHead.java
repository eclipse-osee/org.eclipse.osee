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

import java.io.*;
import java.nio.ByteBuffer;

/**
 * Represents an attachment part in a MIME message. MIME message parsing is done
 * lazily using a pull parser, so the part may not have all the data. {@link #read}
 * and {@link #readOnce} may trigger the actual parsing the message. In fact,
 * parsing of an attachment part may be triggered by calling {@link #read} methods
 * on some other attachemnt parts. All this happens behind the scenes so the
 * application developer need not worry about these details.
 *
 * @author Jitendra Kotamraju
 */
final class DataHead {

    /**
     * Linked list to keep the part's content
     */
    volatile Chunk head, tail;

    /**
     * If the part is stored in a file, non-null.
     */
    DataFile dataFile;

    private final MIMEPart part;

    boolean readOnce;
    volatile long inMemory;

    /**
     * Used only for debugging. This records where readOnce() is called.
     */
    private Throwable consumedAt;

    DataHead(MIMEPart part) {
        this.part = part;
    }

    void addBody(ByteBuffer buf) {
        synchronized(this) {
            inMemory += buf.limit();
        }
        if (tail != null) {
            tail = tail.createNext(this, buf);
        } else {
            head = tail = new Chunk(new MemoryData(buf, part.msg.config));
        }
    }

    void doneParsing() {
    }

    void moveTo(File f) {
        if (dataFile != null) {
            dataFile.renameTo(f);
        } else {
            try {
                OutputStream os = new FileOutputStream(f);
                InputStream in = readOnce();
                byte[] buf = new byte[8192];
                int len;
                while((len=in.read(buf)) != -1) {
                    os.write(buf, 0, len);
                }
                os.close();
            } catch(IOException ioe) {
                throw new MIMEParsingException(ioe);
            }
        }
    }

    void close() {
        if (dataFile != null) {
            head = tail = null;
            dataFile.close();
        }
    }


    /**
     * Can get the attachment part's content multiple times. That means
     * the full content needs to be there in memory or on the file system.
     * Calling this method would trigger parsing for the part's data. So
     * do not call this unless it is required(otherwise, just wrap MIMEPart
     * into a object that returns InputStream for e.g DataHandler)
     *
     * @return data for the part's content
     */
    public InputStream read() {
        if (readOnce) {
            throw new IllegalStateException("readOnce() is called before, read() cannot be called later.");
        }

        // Trigger parsing for the part
        while(tail == null) {
            if (!part.msg.makeProgress()) {
                throw new IllegalStateException("No such MIME Part: "+part);
            }
        }

        if (head == null) {
            throw new IllegalStateException("Already read. Probably readOnce() is called before.");
        }
        return new ReadMultiStream();
    }

    /**
     * Used for an assertion. Returns true when readOnce() is not already called.
     * or otherwise throw an exception.
     *
     * <p>
     * Calling this method also marks the stream as 'consumed'
     *
     * @return true if readOnce() is not called before
     */
    private boolean unconsumed() {
        if (consumedAt != null) {
            AssertionError error = new AssertionError("readOnce() is already called before. See the nested exception from where it's called.");
            error.initCause(consumedAt);
            throw error;
        }
        consumedAt = new Exception().fillInStackTrace();
        return true;
    }

    /**
     * Can get the attachment part's content only once. The content
     * will be lost after the method. Content data is not be stored
     * on the file system or is not kept in the memory for the
     * following case:
     *   - Attachement parts contents are accessed sequentially
     *
     * In general, take advantage of this when the data is used only
     * once.
     *
     * @return data for the part's content
     */
    public InputStream readOnce() {
        assert unconsumed();
        if (readOnce) {
            throw new IllegalStateException("readOnce() is called before. It can only be called once.");
        }
        readOnce = true;
        // Trigger parsing for the part
        while(tail == null) {
            if (!part.msg.makeProgress() && tail == null) {
                throw new IllegalStateException("No such Part: "+part);
            }
        }
        InputStream in = new ReadOnceStream();
        head = null;
        return in;
    }

    class ReadMultiStream extends InputStream {
        Chunk current;
        int offset;
        int len;
        byte[] buf;

        public ReadMultiStream() {
            this.current = head;
            len = current.data.size();
            buf = current.data.read();
        }

        @Override
        public int read(byte b[], int off, int sz) throws IOException {
            if(!fetch())    return -1;

            sz = Math.min(sz, len-offset);
            System.arraycopy(buf,offset,b,off,sz);
            offset += sz;
            return sz;
        }

        public int read() throws IOException {
            if (!fetch()) {
                return -1;
            }
            return (buf[offset++] & 0xff);
        }

        void adjustInMemoryUsage() {
            // Nothing to do in this case.
        }

        /**
         * Gets to the next chunk if we are done with the current one.
         * @return
         */
        private boolean fetch() {
            if (current == null) {
                throw new IllegalStateException("Stream already closed");
            }
            while(offset==len) {
                while(!part.parsed && current.next == null) {
                    part.msg.makeProgress();
                }
                current = current.next;

                if (current == null) {
                    return false;
                }
                adjustInMemoryUsage();
                this.offset = 0;
                this.buf = current.data.read();
                this.len = current.data.size();
            }
            return true;
        }

        public void close() throws IOException {
            super.close();
            current = null;
        }
    }

    final class ReadOnceStream extends ReadMultiStream {

        @Override
        void adjustInMemoryUsage() {
            synchronized(DataHead.this) {
                inMemory -= current.data.size();    // adjust current memory usage
            }
        }

    }


}
