/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.metadata.backend.lucene.index.directory;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.metadata.backend.lucene.index.LuceneIndex;
import org.uberfire.ext.metadata.model.KCluster;

public enum DirectoryType {

    INMEMORY {
        @Override
        public LuceneIndex newIndex(final KCluster cluster,
                                    final IndexWriterConfig config) {
            final Directory directory = new Directory(new RAMDirectory(),
                                                      deleteDirectory -> {
                                                            // no-op
                                                      },
                                                      true);
            return new DirectoryLuceneIndex(cluster,
                                            directory,
                                            config);
        }
    },
    NIO {
        @Override
        public LuceneIndex newIndex(final KCluster cluster,
                                    final IndexWriterConfig config) {

            final File clusterDir = clusterDir(cluster.getClusterId());
            try (final NIOFSDirectory luceneDir = new NIOFSDirectory(clusterDir.toPath())) {
                final Directory directory = new Directory(luceneDir,
                                                          // Delete Command Callback
                                                          deleteDirectory -> {
                                                              close((NIOFSDirectory) deleteDirectory);
                                                              FileDeleteStrategy.FORCE.deleteQuietly(clusterDir);
                                                          },
                                                          freshIndex(clusterDir));

                return new DirectoryLuceneIndex(cluster,
                                                directory,
                                                config);
            } catch (IOException e) {
                logger.error("Error while creating newIndex for NIO DirectoryType. Exception: {}", e);
                throw new org.uberfire.java.nio.IOException(e);
            }

        }
    },
    MMAP {
        @Override
        public LuceneIndex newIndex(final KCluster cluster,
                                    final IndexWriterConfig config) {
            final File clusterDir = clusterDir(cluster.getClusterId());
            try (final MMapDirectory luceneDir = new MMapDirectory(clusterDir.toPath())) {
                final Directory directory = new Directory(luceneDir,
                                                          // Delete Command Callback
                                                          deleteDirectory -> {
                                                              close((MMapDirectory) deleteDirectory);
                                                              FileDeleteStrategy.FORCE.deleteQuietly(clusterDir);
                                                          },
                                                          freshIndex(clusterDir));
                return new DirectoryLuceneIndex(cluster,
                                                directory,
                                                config);
            } catch (IOException e) {
                logger.error("Error while creating newIndex for MMAP DirectoryType. Exception: {}", e);
                throw new org.uberfire.java.nio.IOException(e);
            }
        }
    };

    private static void close(FSDirectory directory) {
        try {
            directory.close();
        } catch (IOException e) {
            logger.warn("Can't close directory",
                        e);
        }
    }

    private static File clusterDir(final String clusterId) {
        return new File(DirectoryFactory.defaultHostingDir(),
                        clusterId);
    }

    private static Logger logger = LoggerFactory.getLogger(DirectoryType.class);

    private static boolean freshIndex(final File clusterDir) {
        return !clusterDir.exists();
    }

    public abstract LuceneIndex newIndex(final KCluster cluster,
                                         final IndexWriterConfig config);
}
