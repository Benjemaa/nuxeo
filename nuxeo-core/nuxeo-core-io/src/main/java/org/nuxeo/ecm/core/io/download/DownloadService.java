/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Florent Guillaume
 */
package org.nuxeo.ecm.core.io.download;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * This service allows the download of blobs to a HTTP response.
 *
 * @since 7.3
 */
public interface DownloadService {

    String EVENT_NAME = "download";

    String NXFILE = "nxfile";

    String NXDOWNLOADINFO = "nxdownloadinfo";

    String NXBIGBLOB = "nxbigblob";

    String NXBIGZIPFILE = "nxbigzipfile";

    String BLOBHOLDER_PREFIX = "blobholder:";

    String BLOBHOLDER_0 = "blobholder:0";

    public static class ByteRange {

        private final long start;

        private final long end;

        public ByteRange(long start, long end) {
            this.start = start;
            this.end = end;
        }

        public long getStart() {
            return start;
        }

        public long getEnd() {
            return end;
        }

        public long getLength() {
            return end - start + 1;
        }
    }

    /**
     * Gets the URL to use to download the blob at the given xpath in the given document.
     * <p>
     * The URL is relative to the Nuxeo Web Application context.
     * <p>
     * Returns something like {@code nxbigfile/reponame/docuuid/blobholder:0/foo.jpg}
     *
     * @param doc the document
     * @param xpath the blob's xpath or blobholder index, or {@code null} for default
     * @param filename the blob's filename, or {@code null} for default
     * @return the download URL
     */
    String getDownloadUrl(DocumentModel doc, String xpath, String filename);

    /**
     * Gets the URL to use to download the blob at the given xpath in the given document.
     * <p>
     * The URL is relative to the Nuxeo Web Application context.
     * <p>
     * Returns something like {@code nxbigfile/reponame/docuuid/blobholder:0/foo.jpg}
     *
     * @param repositoryName the document repository
     * @param docId the document id
     * @param xpath the blob's xpath or blobholder index, or {@code null} for default
     * @param filename the blob's filename, or {@code null} for default
     * @return the download URL
     */
    String getDownloadUrl(String repositoryName, String docId, String xpath, String filename);

    /**
     * Triggers a blob download.
     *
     * @param doc the document, if available
     * @param xpath the blob's xpath or blobholder index, if available
     * @param blob the blob, if already fetched
     * @param filename the filename to use
     * @param reason the download reason
     */
    void downloadBlob(HttpServletRequest request, HttpServletResponse response, DocumentModel doc, String xpath,
            Blob blob, String filename, String reason) throws IOException;

    /**
     * Triggers a blob download.
     *
     * @param doc the document, if available
     * @param xpath the blob's xpath or blobholder index, if available
     * @param blob the blob, if already fetched
     * @param filename the filename to use
     * @param reason the download reason
     * @param extendedInfos an optional map of extended informations to log
     */
    void downloadBlob(HttpServletRequest request, HttpServletResponse response, DocumentModel doc, String xpath,
            Blob blob, String filename, String reason, Map<String, Serializable> extendedInfos) throws IOException;

    /**
     * Triggers a blob download. The actual byte transfer is done through a {@link DownloadExecutor}.
     *
     * @param doc the document, if available
     * @param xpath the blob's xpath or blobholder index, if available
     * @param blob the blob, if already fetched
     * @param filename the filename to use
     * @param reason the download reason
     * @param extendedInfos an optional map of extended informations to log
     * @param inline if not null, force the inline flag for content-disposition
     * @param blobTransferer the transferer of the actual blob
     * @since 7.10
     */
    void downloadBlob(HttpServletRequest request, HttpServletResponse response, DocumentModel doc, String xpath,
            Blob blob, String filename, String reason, Map<String, Serializable> extendedInfos, Boolean inline,
            Consumer<ByteRange> blobTransferer) throws IOException;

    /**
     * Copies the blob stream at the given byte range into the supplied {@link OutputStream}.
     *
     * @param blob the blob
     * @param byteRange the byte range
     * @param outputStreamSupplier the {@link OutputStream} supplier
     * @since 7.10
     */
    void transferBlobWithByteRange(Blob blob, ByteRange byteRange, Supplier<OutputStream> outputStreamSupplier);

    /**
     * Logs a download.
     *
     * @param doc the doc for which this download occurs, if available
     * @param blobXPath the blob's xpath or blobholder index, if available
     * @param filename the filename
     * @param reason the download reason
     * @param extendedInfos an optional map of extended informations to log
     */
    void logDownload(DocumentModel doc, String blobXPath, String filename, String reason,
            Map<String, Serializable> extendedInfos);

    /**
     * Finds a document's blob given an xpath or blobholder index
     *
     * @param doc the document
     * @param xpath the xpath or blobholder index
     * @return the blob, or {@code null} if not found
     */
    Blob resolveBlob(DocumentModel doc, String xpath);

    /**
     * Checks whether the download of the blob is allowed.
     *
     * @param doc the doc for which this download occurs, if available
     * @param blobXPath the blob's xpath or blobholder index, if available
     * @param blob the blob
     * @param reason the download reason
     * @param extendedInfos an optional map of extended informations to log
     * @return {@code true} if download is allowed
     * @since 7.10
     */
    boolean checkPermission(DocumentModel doc, String xpath, Blob blob, String reason,
            Map<String, Serializable> extendedInfos);

}