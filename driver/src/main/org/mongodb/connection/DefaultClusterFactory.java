/*
 * Copyright (c) 2008 - 2013 10gen, Inc. <http://10gen.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mongodb.connection;

import org.mongodb.MongoCredential;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * The default factory for cluster implementations.
 *
 * @since 3.0
 */
public final class DefaultClusterFactory implements ClusterFactory {
    public DefaultClusterFactory() {
    }

    @Override
    public Cluster create(final ClusterSettings settings, final ServerSettings serverSettings,
                          final ConnectionPoolSettings connectionPoolSettings, final StreamFactory streamFactory,
                          final StreamFactory heartbeatStreamFactory, final ScheduledExecutorService scheduledExecutorService,
                          final List<MongoCredential> credentialList, final BufferProvider bufferProvider) {
        ClusterableServerFactory serverFactory = new DefaultClusterableServerFactory(
                serverSettings,
                connectionPoolSettings,
                streamFactory,
                heartbeatStreamFactory,
                scheduledExecutorService,
                credentialList,
                bufferProvider);

        if (settings.getMode() == ClusterConnectionMode.Single) {
            return new SingleServerCluster(settings, serverFactory);
        }
        else if (settings.getMode() == ClusterConnectionMode.Multiple) {
            return new MultiServerCluster(settings, serverFactory);
        }
        else {
            throw new UnsupportedOperationException("Unsupported cluster mode: " + settings.getMode());
        }
    }
}