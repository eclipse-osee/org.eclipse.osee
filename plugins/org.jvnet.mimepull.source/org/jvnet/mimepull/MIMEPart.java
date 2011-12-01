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
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

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
public class MIMEPart {

    private volatile InternetHeaders headers;
    private volatile String contentId;
    private String contentType;
    volatile boolean parsed;    // part is parsed or not
    final MIMEMessage msg;
    private final DataHead dataHead;

    MIMEPart(MIMEMessage msg) {
        this.msg = msg;
        this.dataHead = new DataHead(this);
    }

    MIMEPart(MIMEMessage msg, String contentId) {
        this(msg);
        this.contentId = contentId;   
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
        return dataHead.read();
    }

    /**
     * Cleans up any resources that are held by this part (for e.g. deletes
     * the temp file that is used to serve this part's content). After
     * calling this, one shouldn't call {@link #read()} or {@link #readOnce()}
     */
    public void close() {
        dataHead.close();
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
        return dataHead.readOnce();
    }

    public void moveTo(File f) {
        dataHead.moveTo(f);
    }

    /**
     * Returns Content-ID MIME header for this attachment part
     *
     * @return Content-ID of the part
     */
    public String getContentId() {
        if (contentId == null) {
            getHeaders();
        }
        return contentId;
    }

    /**
     * Returns Content-Type MIME header for this attachment part
     *
     * @return Content-Type of the part
     */
    public String getContentType() {
        if (contentType == null) {
            getHeaders();
        }
        return contentType;
    }

    private void getHeaders() {
        // Trigger parsing for the part headers
        while(headers == null) {
            if (!msg.makeProgress()) {
                if (headers == null) {
                    throw new IllegalStateException("Internal Error. Didn't get Headers even after complete parsing.");
                }
            }
        }
    }

    /**
     * Return all the values for the specified header.
     * Returns <code>null</code> if no headers with the
     * specified name exist.
     *
     * @param	name header name
     * @return	list of header values, or null if none
     */
    public List<String> getHeader(String name) {
        getHeaders();
        assert headers != null;
        return headers.getHeader(name);
    }

    /**
     * Return all the headers
     *
     * @return list of Header objects
     */
    public List<? extends Header> getAllHeaders() {
        getHeaders();
        assert headers != null;
        return headers.getAllHeaders();
    }

    /**
     * Callback to set headers
     *
     * @param headers MIME headers for the part
     */
    void setHeaders(InternetHeaders headers) {
        this.headers = headers;
        List<String> ct = getHeader("Content-Type");
        this.contentType = (ct == null) ? "application/octet-stream" : ct.get(0);
    }

    /**
     * Callback to notify that there is a partial content for the part
     *
     * @param buf content data for the part
     */
    void addBody(ByteBuffer buf) {
        dataHead.addBody(buf);
    }

    /**
     * Callback to indicate that parsing is done for this part
     * (no more update events for this part)
     */
    void doneParsing() {
        parsed = true;
        dataHead.doneParsing();
    }

    /**
     * Callback to set Content-ID for this part
     * @param cid Content-ID of the part
     */
    void setContentId(String cid) {
        this.contentId = cid;
    }

    @Override
    public String toString() {
        return "Part="+contentId;
    }

}
