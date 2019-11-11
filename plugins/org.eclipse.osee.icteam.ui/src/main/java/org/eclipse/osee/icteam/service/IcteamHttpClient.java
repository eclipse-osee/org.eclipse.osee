/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.service;

import org.apache.commons.httpclient.util.URIUtil;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.env.Environment;

import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.InetAddress;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author Ajay Chandrahasan
 */
@Service
public class IcteamHttpClient {
    private static final boolean PROXY_REQUIRED = false;
    private static final String NT_CREDENTIALS = "domain/userId:password";
    private static final String PROXY_HOST = "domain/userId:password";
    private static final int PROXY_PORT = 8080;
    Logger log = Logger.getLogger(getClass());
    public final String serviceUrl;
    @Autowired
    Environment environment;

    @Autowired
    public IcteamHttpClient(
        @Value("${icteam.service.url}")
    final String serviceUrl) {
        this.serviceUrl = serviceUrl;
        System.out.println(serviceUrl);
    }

    public String httpGet(final String url, final String contentType) {
        String encodedUrl = "";
        String response = null;

        try {
            encodedUrl = URIUtil.encodeQuery(this.serviceUrl + url);

            HttpGet httpGet = new HttpGet(encodedUrl);
            httpGet.setHeader("Content-Type", contentType);
            httpGet.setHeader("Authorization",
                "Basic ZW9wbmF4aXhwdzppb3Bld25jeHoxMjNBSW5tbk8=");
            response = execute(httpGet);
        } catch (Exception e) {
            this.log.error("Error while connecting to server", e);
        }

        return response;
    }

    public String httpPost(final String url, final String data,
        final String contentType) {
        String response = null;

        try {
            HttpPost httpPost = new HttpPost(this.serviceUrl + url);
            httpPost.setHeader("Content-Type", contentType);
            httpPost.setHeader("Authorization",
                "Basic ZW9wbmF4aXhwdzppb3Bld25jeHoxMjNBSW5tbk8=");

            String host = InetAddress.getLocalHost().getHostName();
            String port = this.environment.getProperty("server.port");
            httpPost.setHeader("host", host);
            httpPost.setHeader("port", port);

            AbstractHttpEntity entity = new ByteArrayEntity(data.getBytes());
            httpPost.setEntity(entity);
            response = execute(httpPost);
        } catch (Exception e) {
            this.log.error("Error while connecting to server", e);
        }

        return response;
    }

    public String httpPut(final String url, final String data,
        final String contentType) {
        String response = null;

        try {
            HttpPut httpPut = new HttpPut(this.serviceUrl + url);
            httpPut.setHeader("Content-Type", contentType);
            httpPut.setHeader("Authorization",
                "Basic ZW9wbmF4aXhwdzppb3Bld25jeHoxMjNBSW5tbk8=");

            AbstractHttpEntity entity = new ByteArrayEntity(data.getBytes());
            httpPut.setEntity(entity);
            response = execute(httpPut);
        } catch (Exception e) {
            this.log.error("Error while connecting to server", e);
        }

        return response;
    }

    public String httpDelete(final String url, final String data,
        final String contentType) {
        String response = null;

        try {
            HttpDelete httpDelete = new HttpDelete(this.serviceUrl + url);
            httpDelete.setHeader("Content-Type", contentType);
            httpDelete.setHeader("Authorization",
                "Basic ZW9wbmF4aXhwdzppb3Bld25jeHoxMjNBSW5tbk8=");
            response = execute(httpDelete);
        } catch (Exception e) {
            this.log.error("Error while connecting to server", e);
        }

        return response;
    }

    private String execute(final HttpRequestBase request)
        throws Exception {
        StringBuilder responseData = new StringBuilder();
        String line = "";
        BufferedReader bufferedReader;

        if (PROXY_REQUIRED) {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                new NTCredentials(NT_CREDENTIALS));

            HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT);
            CloseableHttpClient closeableClient = HttpClients.custom()
                                                             .setProxy(proxy)
                                                             .setDefaultCredentialsProvider(credentialsProvider)
                                                             .build();
            CloseableHttpResponse response = closeableClient.execute(request);
            bufferedReader = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent()));
        } else {
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(request);
            bufferedReader = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent()));
        }

        while ((line = bufferedReader.readLine()) != null) {
            responseData.append(line);
        }

        return responseData.toString();
    }

    public void download(final String url, final String data,
        final HttpServletRequest request, final HttpServletResponse response) {
        String encodedUrl = "";

        try {
            encodedUrl = URIUtil.encodeQuery(this.serviceUrl + url);

            HttpPost httppost = new HttpPost(encodedUrl);
            httppost.setHeader("Authorization",
                "Basic ZW9wbmF4aXhwdzppb3Bld25jeHoxMjNBSW5tbk8=");

            HttpClient client = new DefaultHttpClient();
            AbstractHttpEntity entity = new ByteArrayEntity(data.getBytes());
            httppost.setEntity(entity);

            HttpResponse httpResponse = client.execute(httppost);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition",
                "attachment; filename=DataPoints.csv");

            OutputStream out = response.getOutputStream();
            BufferedInputStream bis = new BufferedInputStream(httpResponse.getEntity()
                                                                          .getContent());
            BufferedOutputStream bos = new BufferedOutputStream(out);
            int inByte;

            while ((inByte = bis.read()) != -1) {
                bos.write(inByte);
            }

            bis.close();
            bos.close();
        } catch (Exception e) {
            this.log.error("Error while connecting to server", e);
        }
    }

    @SuppressWarnings("deprecation")
    public String upload(final String url, final MultipartFile file,
        final HttpServletRequest request, final HttpServletResponse response) {
        String encodedUrl = "";
        String response1 = "";

        try {
            encodedUrl = URIUtil.encodeQuery(this.serviceUrl + url);

            HttpPost httppost = new HttpPost(encodedUrl);
            File convFile = new File(file.getOriginalFilename());
            convFile.createNewFile();

            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();

            MultipartEntity mpEntity = new MultipartEntity();
            Enumeration<String> params = request.getParameterNames();
            mpEntity.addPart("file", new FileBody(convFile));

            while (params.hasMoreElements()) {
                String paramName = params.nextElement();
                mpEntity.addPart(paramName,
                    new StringBody(request.getParameter(paramName)));
            }

            httppost.setEntity(mpEntity);
            response1 = execute(httppost);
            convFile.delete();
        } catch (Exception e) {
            this.log.error("Error while connecting to server", e);
        }

        return response1;
    }
}
