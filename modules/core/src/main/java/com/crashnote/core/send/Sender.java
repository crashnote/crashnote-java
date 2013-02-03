/**
 * Copyright (C) 2012 - 101loops.com <dev@101loops.com>
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

import com.crashnote.core.config.CrashConfig;
import com.crashnote.core.log.LogLog;
import com.crashnote.core.model.log.LogReport;
import com.crashnote.external.json.JSONArray;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/**
 * The Dispatcher is responsible for transmitting the data from the client to the server by
 * using Java's build-in capabilities around {@link HttpURLConnection}.
 */
public class Sender {

    // VARS =======================================================================================

    // configuration settings:
    private String url_post;
    private String clientInfo;
    private int connectionTimeout;

    protected final LogLog logger;


    // SETUP ======================================================================================

    public <C extends CrashConfig> Sender(final C config) {
        this.url_post = config.getPostUrl();
        this.clientInfo = config.getClientInfo();
        this.connectionTimeout = config.getConnectionTimeout();

        this.logger = config.getLogger(this.getClass());

        // create and install a trust manager that does not validate certificate chains
        installCustomTrustManager();

        // config URL connections
        HttpURLConnection.setFollowRedirects(true);
    }

    protected void installCustomTrustManager() {
        try {
            final SSLContext sc = SSLContext.getInstance("TLS");
            final TrustManager[] mgrs = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(final X509Certificate[] certs, final String typeOf) {
                    }

                    @Override
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


    // INTERFACE ==================================================================================

    public void send(final LogReport report) {
        logger.debug("POST to '{}'", url_post);
        POST(url_post, report);
    }


    // SHARED =====================================================================================

    protected void POST(final String url, final LogReport report) {
        HttpURLConnection conn = null;
        try {
            conn = prepareConnection(url);
            try {
                write(conn, report);
            } catch(IOException e) {
                logger.debug("unable to send data", e);
            }
        } catch(IOException e) {
            logger.debug("unable to open connection", e);
        } finally {
            if (conn != null)
                conn.disconnect();
        }
    }

    // INTERNALS ==================================================================================

    private HttpURLConnection prepareConnection(final String url) throws IOException {
        final HttpURLConnection conn = createConnection(url);
        {
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setAllowUserInteraction(false);
            conn.setReadTimeout(connectionTimeout);
            conn.setConnectTimeout(connectionTimeout);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept-Encoding", "gzip");
            conn.setRequestProperty("Content-Encoding", "gzip");
            if (clientInfo != null)
                conn.setRequestProperty("User-Agent", getClientInfo());
        }
        return conn;
    }

    private void write(final HttpURLConnection conn, final LogReport report) throws IOException {
        Writer out = null;
        try {
            final OutputStream os = new DeflaterOutputStream(conn.getOutputStream(), new Deflater(-1));
            {
                out = createWriter(os);
                report.streamTo(out);
            }
            out.flush();
            os.close();
        } finally {
            if (out != null)
                out.close();
        }
    }

    // FACTORY ====================================================================================

    protected HttpURLConnection createConnection(final String url) throws IOException {
        return (HttpURLConnection) new URL(url).openConnection();
    }

    protected Writer createWriter(final OutputStream stream) throws UnsupportedEncodingException {
        return new OutputStreamWriter(stream, "UTF-8");
    }


    // GET ========================================================================================

    protected String getClientInfo() {
        return clientInfo;
    }

    protected int getConnectionTimeout() {
        return connectionTimeout;
    }
}