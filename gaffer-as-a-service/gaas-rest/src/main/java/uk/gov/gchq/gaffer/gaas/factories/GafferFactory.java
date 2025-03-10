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

package uk.gov.gchq.gaffer.gaas.factories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import uk.gov.gchq.gaffer.federatedstore.FederatedStore;
import uk.gov.gchq.gaffer.gaas.model.GaaSCreateRequestBody;
import uk.gov.gchq.gaffer.gaas.model.v1.Gaffer;
import uk.gov.gchq.gaffer.gaas.model.v1.GafferSpec;
import uk.gov.gchq.gaffer.proxystore.operation.GetProxyUrl;
import uk.gov.gchq.gaffer.proxystore.operation.handler.GetProxyUrlHandler;
import uk.gov.gchq.gaffer.store.operation.declaration.OperationDeclaration;
import uk.gov.gchq.gaffer.store.operation.declaration.OperationDeclarations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.gov.gchq.gaffer.gaas.util.Constants.CONFIG_NAME_K8S_METADATA_LABEL;
import static uk.gov.gchq.gaffer.gaas.util.Constants.CONFIG_NAME_KEY;
import static uk.gov.gchq.gaffer.gaas.util.Constants.DESCRIPTION_KEY;
import static uk.gov.gchq.gaffer.gaas.util.Constants.GAFFER_OPERATION_DECLARATION_KEY;
import static uk.gov.gchq.gaffer.gaas.util.Constants.GAFFER_STORE_CLASS_KEY;
import static uk.gov.gchq.gaffer.gaas.util.Constants.GRAPH_ID_KEY;
import static uk.gov.gchq.gaffer.gaas.util.Constants.GROUP;
import static uk.gov.gchq.gaffer.gaas.util.Constants.INGRESS_API_PATH_KEY;
import static uk.gov.gchq.gaffer.gaas.util.Constants.INGRESS_HOST_KEY;
import static uk.gov.gchq.gaffer.gaas.util.Constants.INGRESS_UI_PATH_KEY;
import static uk.gov.gchq.gaffer.gaas.util.Constants.SCHEMA_FILE_KEY;
import static uk.gov.gchq.gaffer.gaas.util.Constants.VERSION;
import static uk.gov.gchq.gaffer.gaas.util.Properties.INGRESS_SUFFIX;
import static uk.gov.gchq.gaffer.gaas.util.Properties.NAMESPACE;

/**
 * GafferHelmValuesFactory is a factory class that creates a Gaffer Helm Values Object that can be passed to the
 * Kubernetes java client and use helm to deploy a Gaffer custom resource instance to a Kubernetes cluster.
 * <p>
 * See <a href="https://github.com/gchq/gaffer-docker/blob/develop/kubernetes/gaffer/values.yaml">values.yaml</a> for
 * the default helm chart values and documentation how Gaffer is deployed to Kubernetes via helm.
 *
 * @see <a href="https://github.com/gchq/gaffer-docker/blob/develop/kubernetes/gaffer/values-federated.yaml">Federated Store overrides</a>
 * for more Gaffer store configuration overrides:
 */
public final class GafferFactory {

    private static final String KIND = "Gaffer";

    public static Gaffer from(final GafferSpec gafferSpecConfig, final GaaSCreateRequestBody createGraphRequest) {

        // TODO: Validate only - and . special characters, see Kubernetes metadata regex
        final Map<String, String> labels = new HashMap<>();
        labels.put(CONFIG_NAME_K8S_METADATA_LABEL, createGraphRequest.getConfigName());

        final V1ObjectMeta metadata = new V1ObjectMeta()
                .name(createGraphRequest.getGraphId())
                .labels(labels);

        return new Gaffer()
                .apiVersion(GROUP + "/" + VERSION)
                .kind(KIND)
                .metaData(metadata)
                .spec(overrideGafferSpecConfig(gafferSpecConfig, createGraphRequest));
    }

    private static GafferSpec overrideGafferSpecConfig(final GafferSpec config, final GaaSCreateRequestBody overrides) {
        config.putNestedObject(overrides.getGraphId(), GRAPH_ID_KEY);
        config.putNestedObject(overrides.getDescription(), DESCRIPTION_KEY);
        config.putNestedObject(overrides.getConfigName(), CONFIG_NAME_KEY);
        if (FederatedStore.class.getName().equals(config.getNestedObject(GAFFER_STORE_CLASS_KEY))) {
            config.putNestedObject(createOperationDeclarations(config), GAFFER_OPERATION_DECLARATION_KEY);
            config.putNestedObject("{}", SCHEMA_FILE_KEY);
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            try {
                config.putNestedObject(objectMapper.writeValueAsString(overrides.getSchema()), SCHEMA_FILE_KEY);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        // Mandatory Ingress values
        config.putNestedObject(overrides.getGraphId().toLowerCase() + "-" + NAMESPACE + "." + INGRESS_SUFFIX, INGRESS_HOST_KEY);
        config.putNestedObject("/rest", INGRESS_API_PATH_KEY);
        config.putNestedObject("/ui", INGRESS_UI_PATH_KEY);
        return config;
    }


    private static OperationDeclarations createOperationDeclarations(final GafferSpec federatedSpec) {
        OperationDeclarations existingOperationDeclarations = null;

        final OperationDeclarations declarations = new OperationDeclarations.Builder()
                .declaration(new OperationDeclaration.Builder()
                        .handler(new GetProxyUrlHandler())
                        .operation(GetProxyUrl.class)
                        .build())
                .build();

        if (federatedSpec != null && federatedSpec.getNestedObject(GAFFER_OPERATION_DECLARATION_KEY) != null) {
            existingOperationDeclarations = (OperationDeclarations) federatedSpec.getNestedObject(GAFFER_OPERATION_DECLARATION_KEY);
        }

        if (existingOperationDeclarations != null) {
            List<OperationDeclaration> operations = existingOperationDeclarations.getOperations();
            operations.forEach((operation) -> declarations.getOperations().add(operation));
        }

        return declarations;
    }

    private GafferFactory() {
        // prevents calls from subclass
        throw new UnsupportedOperationException();
    }
}
