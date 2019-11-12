/*
 * Copyright (c) 2018 the original author or authors.
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

package example.controllers;

import com.yahoo.elide.Elide;
import com.yahoo.elide.ElideResponse;
import example.config.ElideConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedHashMap;
import java.security.Principal;
import java.util.Map;

/**
 * Spring rest controller for Elide JSON-API.
 * Based on <a href="https://github.com/illyasviel/elide-spring-boot/blob/master/elide-spring-boot-autoconfigure/src/main/java/org/illyasviel/elide/spring/boot/autoconfigure/ElideControllerAutoConfiguration.java"/>
 */
@Slf4j
@RestController
@RequestMapping(value = "${elide.json-api.path}")
@ConditionalOnExpression("${elide.json-api.enabled:false}")
public class JsonApiController {

    private final Elide elide;
    private final ElideConfigProperties settings;
    private static final String JSON_API_CONTENT_TYPE = "application/vnd.api+json";

    @Autowired
    public JsonApiController(Elide elide, ElideConfigProperties settings) {
        log.debug("Started ~~");
        this.settings = settings;
        this.elide = elide;
    }

    @GetMapping(value = "/**")
    public ResponseEntity<String> elideGet(@RequestParam Map<String, String> allRequestParams,
                                           HttpServletRequest request, Principal authentication) {
        String pathname = getJsonApiPath(request, settings.getJsonApi().getPath());

        ElideResponse response = elide.get(pathname, new MultivaluedHashMap<>(allRequestParams), authentication);
        return ResponseEntity.status(response.getResponseCode()).body(response.getBody());
    }

    @PostMapping(value = "/**", consumes = JSON_API_CONTENT_TYPE)
    public ResponseEntity<String> elidePost(@RequestBody String body,
                                            HttpServletRequest request, Principal authentication) {
        String pathname = getJsonApiPath(request, settings.getJsonApi().getPath());

        ElideResponse response = elide
                .post(pathname, body, authentication);
        return ResponseEntity.status(response.getResponseCode()).body(response.getBody());
    }

    @PatchMapping(value = "/**", consumes = JSON_API_CONTENT_TYPE)
    public ResponseEntity<String> elidePatch(@RequestBody String body,
                                             HttpServletRequest request, Principal authentication) {
        String pathname = getJsonApiPath(request, settings.getJsonApi().getPath());

        ElideResponse response = elide
                .patch(JSON_API_CONTENT_TYPE, JSON_API_CONTENT_TYPE, pathname, body, authentication);
        return ResponseEntity.status(response.getResponseCode()).body(response.getBody());
    }

    @DeleteMapping(value = "/**")
    public ResponseEntity<String> elideDelete(HttpServletRequest request,
                                              Principal authentication) {
        String pathname = getJsonApiPath(request, settings.getJsonApi().getPath());

        ElideResponse response = elide
                .delete(pathname, null, authentication);
        return ResponseEntity.status(response.getResponseCode()).body(response.getBody());
    }

    @DeleteMapping(value = "/**", consumes = JSON_API_CONTENT_TYPE)
    public ResponseEntity<String> elideDeleteRelationship(@RequestBody String body,
                                                          HttpServletRequest request, Principal authentication) {
        String pathname = getJsonApiPath(request, settings.getJsonApi().getPath());

        ElideResponse response = elide
                .delete(pathname, body, authentication);
        return ResponseEntity.status(response.getResponseCode()).body(response.getBody());
    }

    private String getJsonApiPath(HttpServletRequest request, String prefix) {
        String pathname = (String) request
                .getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        return pathname.replaceFirst(prefix, "");
    }
}
