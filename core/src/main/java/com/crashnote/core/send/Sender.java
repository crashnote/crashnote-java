/**
 * Copyright (C) 2011 - 101loops.com <dev@101loops.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.crashnote.core.send;

import com.crashnote.core.config.*;
import com.crashnote.core.log.LogLog;
import com.crashnote.core.model.log.LogReport;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.cert.X509Certificate;
import java.util.zip.GZIPOutputStream;

/**
 * The Dispatcher is responsible for transmitting the data from the client to the server by
 * using Java's build-in capabilities around {@link HttpURLConnection}.
 */
public class Sender<C extends Config>
    implements IConfigChangeListener<C> {

    // configuration settings:
    private String key;
    private String url_post;
    private String clientInfo;
    private int connectionTimeout;

    private final LogLog logger;

    // SETUP ======================================================================================

    public Sender(final C config) {
        updateConfig(config);
        this.logger = config.getLogger(this.getClass());

        // create and install a trust manager that does not validate certificate chains
        installCustomTrustManager();
    }

    public void updateConfig(final C config) {
        config.addListener(this);
        this.key = config.getKey();
        this.url_post = config.getPostUrl();
        this.clientInfo = config.getClientInfo();
        this.connectionTimeout = config.getConnectionTimeout() * 1000;
    }

    // INTERFACE ==================================================================================

    public boolean send(final LogReport report) {
        try {
            logger.debug("POST to '{}'", url_post);
            POST(url_post, report);
            return true;
        } catch (Exception e) {
            logger.error("POST failed", e, url_post);
        }
        return false;
    }

    // SHARED =====================================================================================

    protected void POST(final String url, final LogReport report) throws IOException {
        final Writer writer;
        final OutputStream out;
        HttpURLConnection conn = null;

        try {
            // prepare connection
            conn = createConnection(url, "POST");

            // stream data to server
            out = createStream(conn);
            {
                writer = createWriter(out);
                report.streamTo(writer);
                writer.flush();
                writer.close();
            }
            out.flush();
            out.close();

            conn.getResponseCode();
        } finally {
            // close connection
            if (conn != null) conn.disconnect();
        }
    }

    HttpURLConnection createConnection(final String url, final String typeOf) throws IOException {

        HttpURLConnection.setFollowRedirects(true);
        final HttpURLConnection conn = createConnection(url);
        {
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(typeOf);
            conn.setAllowUserInteraction(false);
            conn.setReadTimeout(connectionTimeout);
            conn.setConnectTimeout(connectionTimeout);

            if (getClientInfo() != null)
                conn.setRequestProperty("User-Agent", getClientInfo());
        }
        return conn;
    }

    // FACTORY ====================================================================================

    protected HttpURLConnection createConnection(final String url) throws IOException {
        return (HttpURLConnection) new URL(url).openConnection();
    }

    protected OutputStream createStream(final HttpURLConnection conn) throws IOException {

        // data is gzipped JSON by default, so add the necessary request properties
        conn.setRequestProperty("Accept", "application/x-gzip");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Content-Encoding", "gzip");

        return new GZIPOutputStream(conn.getOutputStream());
    }

    protected Writer createWriter(final OutputStream stream) {
        return new OutputStreamWriter(stream);
    }

    protected void installCustomTrustManager() {
        try {
            final SSLContext sc = SSLContext.getInstance("TLS");
            final TrustManager[] mgrs = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(final X509Certificate[] certs, final String typeOf) {
                    }

                    public void checkServerTrusted(final X509Certificate[] certs, final String typeOf) {
                    }
                }
            };
            sc.init(null, mgrs, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            logger.warn("unable to install custom SSL manager", e);
        }
    }

    // GET ========================================================================================

    protected String getClientInfo() {
        return clientInfo;
    }

    protected int getConnectionTimeout() {
        return connectionTimeout;
    }
}