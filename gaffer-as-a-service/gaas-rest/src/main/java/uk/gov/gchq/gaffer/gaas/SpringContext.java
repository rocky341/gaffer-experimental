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
package uk.gov.gchq.gaffer.gaas;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("uk.gov.gchq.gaffer.gaas.factories")
@ComponentScan("uk.gov.gchq.gaffer.gaas.stores")
@ComponentScan("uk.gov.gchq.gaffer.gaas.auth")
@ComponentScan("uk.gov.gchq.gaffer.gaas.controller")
@ComponentScan("uk.gov.gchq.gaffer.gaas.handlers")
@ComponentScan("uk.gov.gchq.gaffer.gaas.client")
@ComponentScan("uk.gov.gchq.gaffer.gaas.model")
public class SpringContext {
}
