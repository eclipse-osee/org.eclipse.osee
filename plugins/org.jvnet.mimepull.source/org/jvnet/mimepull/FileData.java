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

/**
 * Keeps the Part's partial content data in a file.
 *
 * @author Kohsuke Kawaguchi
 * @author Jitendra Kotamraju
 */
final class FileData implements Data {
    private final DataFile file;
    private final long pointer;         // read position
    private final int length;

    FileData(DataFile file, ByteBuffer buf) {
        this(file, file.writeTo(buf.array(), 0, buf.limit()), buf.limit());
    }

    FileData(DataFile file, long pointer, int length) {
        this.file = file;
        this.pointer = pointer;
        this.length = length;
    }

    public byte[] read() {
        byte[] buf = new byte[length];
        file.read(pointer, buf, 0, length);
        return buf;
    }

    /*
     * This shouldn't be called
     */
    public long writeTo(DataFile file) {
        throw new IllegalStateException();
    }

    public int size() {
        return length;
    }

    /*
     * Always create FileData
     */
    public Data createNext(DataHead dataHead, ByteBuffer buf) {
        return new FileData(file, buf);
    }
}
