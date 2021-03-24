/*
 * Copyright 2020 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.gchq.gaffer.gaas.model;

import uk.gov.gchq.gaffer.cache.impl.HashMapCacheService;
import uk.gov.gchq.gaffer.cache.util.CacheProperties;
import uk.gov.gchq.gaffer.federatedstore.FederatedStore;
import uk.gov.gchq.gaffer.federatedstore.FederatedStoreProperties;
import uk.gov.gchq.gaffer.graph.GraphConfig;
import uk.gov.gchq.gaffer.proxystore.ProxyProperties;
import uk.gov.gchq.gaffer.proxystore.ProxyStore;
import uk.gov.gchq.gaffer.sketches.serialisation.json.SketchesJsonModules;
import uk.gov.gchq.gaffer.store.StoreProperties;
import java.util.HashMap;
import java.util.Map;

public class NewGraph {

    private GraphConfig config;
    private Map<String, Object> storeProperties;

    public NewGraph config(final GraphConfig config) {
        this.config = config;
        return this;
    }

    public NewGraph storeProperties(final StoreType storeType) {
        return storeProperties(storeType, null, null);
    }

    public NewGraph storeProperties(final StoreType storeType, final String host, final String contextRoot) {
        switch (storeType) {
            case ACCUMULO:
                // No AccumuloStoreProperties required for the graph
                // Instead, enableAccumulo() in the GraphSpec to enable Accumulo store properties
                throw new IllegalArgumentException("enableAccumulo() in the GraphSpec to enable Accumulo store properties");
            case FEDERATED_STORE:
                this.storeProperties = getDefaultFederatedStoreProperties();
                return this;
            case MAPSTORE:
                this.storeProperties = getDefaultMapStoreProperties();
                return this;
            case PROXY_STORE:
                if (host == null) {
                    throw new IllegalArgumentException("Host is required to create a proxy store to proxy");
                }
                this.storeProperties = getDefaultProxyStoreProperties(host, contextRoot);
                return this;
            default:
                throw new IllegalArgumentException("Unsupported store type");
        }
    }

    public GraphConfig getConfig() {
        return config;
    }

    public Map<String, Object> getStoreProperties() {
        return storeProperties;
    }

    private Map<String, Object> getDefaultFederatedStoreProperties() {
        final Map<String, Object> federatedStoreProperties = new HashMap<>();
        federatedStoreProperties.put(StoreProperties.STORE_CLASS, FederatedStore.class.getName());
        federatedStoreProperties.put(StoreProperties.STORE_PROPERTIES_CLASS, FederatedStoreProperties.class.getName());
        federatedStoreProperties.put(StoreProperties.JSON_SERIALISER_MODULES, SketchesJsonModules.class.getName());
        return federatedStoreProperties;
    }

    private Map<String, Object> getDefaultMapStoreProperties() {
        final Map<String, Object> mapStoreProperties = new HashMap<>();
        mapStoreProperties.put(CacheProperties.CACHE_SERVICE_CLASS, HashMapCacheService.class.getName());
        mapStoreProperties.put(StoreProperties.JOB_TRACKER_ENABLED, true);
        return mapStoreProperties;
    }

    private Map<String, Object> getDefaultProxyStoreProperties(final String host, final String contextRoot) {
        final Map<String, Object> proxyStoreProperties = new HashMap<>();
        proxyStoreProperties.put(StoreProperties.STORE_CLASS, ProxyStore.class.getName());
        proxyStoreProperties.put(ProxyProperties.GAFFER_HOST, host);
        if (contextRoot != null) {
            proxyStoreProperties.put(ProxyProperties.GAFFER_CONTEXT_ROOT, contextRoot);
            // else, let Gaffer handle the default context root when not specified in GaaS REST request
        }
        return proxyStoreProperties;
    }
}
