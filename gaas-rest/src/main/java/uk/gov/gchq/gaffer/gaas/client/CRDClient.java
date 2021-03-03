/*
 * Copyright 2021 Crown Copyright
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

package uk.gov.gchq.gaffer.gaas.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.CustomObjectsApi;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.gchq.gaffer.gaas.exception.GaaSRestApiException;
import uk.gov.gchq.gaffer.graph.GraphConfig;
import uk.gov.gchq.gaffer.store.library.FileGraphLibrary;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import static uk.gov.gchq.gaffer.gaas.converters.GaaSRestExceptionFactory.from;

@Service
public class CRDClient {

    @Value("${group}")
    private String group;
    @Value("${version}")
    private String version;
    @Value("${namespace}")
    private String namespace;
    private static final String PLURAL = "gaffers";
    private static final String PRETTY = null;
    private static final String DRY_RUN = null;
    private static final String FIELD_MANAGER = null;

    @Autowired
    private CustomObjectsApi customObjectsApi;

    @Autowired
    private CoreV1Api coreV1Api;

    public void createCRD(final KubernetesObject requestBody) throws GaaSRestApiException {
        try {
            customObjectsApi.createNamespacedCustomObject(this.group, this.version, this.namespace, this.PLURAL, requestBody, this.PRETTY, this.DRY_RUN, this.FIELD_MANAGER);
        } catch (ApiException e) {
            throw from(e);
        }
    }

    public List<GraphConfig> listAllCRDs() throws GaaSRestApiException {
        try {
            final Object customObject = customObjectsApi.listNamespacedCustomObject(this.group, this.version, this.namespace, this.PLURAL, this.PRETTY, null, null, null, null, null, null, null);
            return convertJsonToGraphs(customObject);
        } catch (ApiException e) {
            throw from(e);
        }
    }

    public void deleteCRD(final String crdName) throws GaaSRestApiException {
        try {
            customObjectsApi.deleteNamespacedCustomObject(group, version, namespace, PLURAL, crdName, null, null, null, this.DRY_RUN, null);
        } catch (ApiException e) {
            throw from(e);
        }
    }

    public List<String> getAllNameSpaces() throws GaaSRestApiException {
        try {
            final V1NamespaceList listNamespace =
                    coreV1Api.listNamespace(
                            "true", null, null, null, null, 0, null, null, Integer.MAX_VALUE, Boolean.FALSE);
            final List<String> list =
                    listNamespace.getItems().stream()
                            .map(v1Namespace -> v1Namespace.getMetadata().getName())
                            .collect(Collectors.toList());
            return list;
        } catch (ApiException e) {
            throw from(e);
        }
    }

    private List<GraphConfig> convertJsonToGraphs(final Object response) {
        final Gson gson = new Gson();
        final JsonArray items = new JsonParser().parse(gson.toJson(response)).getAsJsonObject().get("items").getAsJsonArray();

        final List<GraphConfig> list = new ArrayList();
        final Iterator<JsonElement> iterator = items.iterator();
        while (iterator.hasNext()) {
            final JsonElement key = iterator.next();
            final JsonObject graph = key.getAsJsonObject()
                    .get("spec").getAsJsonObject()
                    .get("graph").getAsJsonObject()
                    .get("config").getAsJsonObject();

            final String graphId = gson.fromJson(graph.get("graphId"), String.class);
            final String graphDescription = gson.fromJson(graph.get("description"), String.class);

            list.add(new GraphConfig.Builder()
                    .graphId(graphId)
                    .description(graphDescription)
                    .library(new FileGraphLibrary()).build());
        }
        return list;
    }
}
