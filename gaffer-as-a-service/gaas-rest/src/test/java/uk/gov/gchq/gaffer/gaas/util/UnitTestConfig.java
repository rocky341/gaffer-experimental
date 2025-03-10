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

package uk.gov.gchq.gaffer.gaas.util;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.CustomObjectsApi;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import uk.gov.gchq.gaffer.gaas.auth.JwtTokenUtil;
import uk.gov.gchq.gaffer.gaas.auth.JwtUserDetailsService;
import uk.gov.gchq.gaffer.gaas.client.GafferClient;
import uk.gov.gchq.gaffer.gaas.client.graph.GraphCommandExecutor;
import uk.gov.gchq.gaffer.gaas.handlers.DeploymentHandler;
import uk.gov.gchq.gaffer.gaas.services.AuthService;
import uk.gov.gchq.gaffer.gaas.services.CreateFederatedStoreGraphService;
import uk.gov.gchq.gaffer.gaas.services.CreateGraphService;
import uk.gov.gchq.gaffer.gaas.services.DeleteGraphService;
import uk.gov.gchq.gaffer.gaas.services.GetGaaSGraphConfigsService;
import uk.gov.gchq.gaffer.gaas.services.GetGaffersService;
import uk.gov.gchq.gaffer.gaas.services.GetNamespacesService;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class UnitTestConfig {

    @Bean
    @Primary
    public ApiClient apiClient() {
        return mock(ApiClient.class);
    }

    @Bean
    @Primary
    public MeterRegistry meterRegistry() {
        return mock(MeterRegistry.class);
    }

    @Bean
    public GafferClient crdClient() {
        return new GafferClient();
    }

    @Bean
    @Primary
    public CoreV1Api coreV1Api() {
        return mock(CoreV1Api.class);
    }

    @Bean
    public CustomObjectsApi customObjectsApi() {
        return new CustomObjectsApi(apiClient());
    }

    @Bean
    public AuthService authService() {
        return new AuthService();
    }

    @Bean
    public CreateGraphService createGraphService() {
        return new CreateGraphService();
    }

    @Bean
    public CreateFederatedStoreGraphService createFederatedStoreGraphService() {
        return new CreateFederatedStoreGraphService();
    }

    @Bean
    public GetGaffersService getGafferService() {
        return new GetGaffersService();
    }

    @Bean
    public GetNamespacesService getNamespacesService() {
        return new GetNamespacesService();
    }

    @Bean
    public GetGaaSGraphConfigsService getStoreTypesService() {
        return new GetGaaSGraphConfigsService();
    }

    @Bean
    public JwtTokenUtil jwtTokenUtil() {
        return new JwtTokenUtil();
    }

    @Bean
    public JwtUserDetailsService jwtUserDetailsService() {
        return new JwtUserDetailsService();
    }

    @Bean
    public Properties properties() {
        return new Properties();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return mock(AuthenticationManager.class);
    }

    @Bean
    public GraphCommandExecutor graphCommandExecutor() {
        return new GraphCommandExecutor();
    }

    @Bean
    public GafferSpecConfigsLoader propertiesLoader() {
        return new GafferSpecConfigsLoader();
    }

    @Bean
    KubernetesClient kubernetesClient() {
        return mock(KubernetesClient.class);
    }

    @Bean
    public DeploymentHandler deploymentHandler() {
        return mock(DeploymentHandler.class);
    }

    @Bean
    public DeleteGraphService deleteGraphService() {
        return mock(DeleteGraphService.class);
    }
}
