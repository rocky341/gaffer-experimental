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

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uk.gov.gchq.gaffer.gaas.exception.GaaSRestApiException;
import uk.gov.gchq.gaffer.gaas.handlers.DeploymentHandler;
import uk.gov.gchq.gaffer.gaas.model.GaaSGraph;
import uk.gov.gchq.gaffer.gaas.model.GraphUrl;
import uk.gov.gchq.gaffer.gaas.model.v1.Gaffer;

import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.gchq.gaffer.gaas.factories.GaaSRestExceptionFactory.from;

@Repository
public class GafferClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(GafferClient.class);

    @Autowired
    private CoreV1Api coreV1Api;

    @Autowired
    private DeploymentHandler deploymentHandler;

    public GraphUrl createGaffer(final Gaffer requestBody) throws GaaSRestApiException {
        try {
            deploymentHandler.onGafferCreate(requestBody);
            return GraphUrl.from(requestBody);
        } catch (ApiException e) {
            if (requestBody == null || requestBody.getMetadata() == null) {
                LOGGER.debug("Failed to create Gaffer \"\". Error: ", e);
            } else {
                LOGGER.debug("Failed to create Gaffer with name \"" + requestBody.getMetadata().getName() + "\". Error: ", e);
            }
            throw from(e);
        }
    }

    public List<GaaSGraph> listAllGaffers() throws GaaSRestApiException {
        KubernetesClient kubernetesClient = new DefaultKubernetesClient();
        try {
            return deploymentHandler.getDeployments(kubernetesClient);
        } catch (ApiException e) {
            LOGGER.debug("Failed to list all Gaffers");
            throw from(e);
        }
    }

    public boolean deleteGaffer(final String crdName) throws GaaSRestApiException {
        KubernetesClient kubernetesClient = new DefaultKubernetesClient();
        try {
            return deploymentHandler.onGafferDelete(crdName, kubernetesClient);
        } catch (ApiException e) {
            LOGGER.debug("Failed to delete CRD. Kubernetes client returned Status Code: " + e.getCode(), e);
            throw from(e);
        }
    }

    public List<String> getAllNameSpaces() throws GaaSRestApiException {
        try {
            final V1NamespaceList v1NamespaceList =
                    coreV1Api.listNamespace(
                            "true", null, null, null, null, 0, null, null, Integer.MAX_VALUE, Boolean.FALSE);
            return namespacesAsList(v1NamespaceList);
        } catch (ApiException e) {
            LOGGER.debug("Failed to list all namespaces. Kubernetes CoreV1Api returned Status Code: " + e.getCode(), e);
            throw from(e);
        }
    }

    private List<String> namespacesAsList(final V1NamespaceList v1NamespaceList) {
        return v1NamespaceList.getItems().stream()
                .map(v1Namespace -> v1Namespace.getMetadata().getName())
                .collect(Collectors.toList());
    }

}
