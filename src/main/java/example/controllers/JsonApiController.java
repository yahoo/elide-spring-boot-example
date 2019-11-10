package example.controllers;

import com.yahoo.elide.Elide;
import com.yahoo.elide.ElideResponse;
import example.config.ElideSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedHashMap;
import java.security.Principal;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "${elide.json-api-path}")
@EnableConfigurationProperties(ElideSettings.class)
public class JsonApiController {

    private final Elide elide;
    private final ElideSettings settings;

    @Autowired
    public JsonApiController(Elide elide, ElideSettings settings) {
        log.debug("Started ~~");
        this.settings = settings;
        this.elide = elide;
    }

    @GetMapping(value = "/**")
    public ResponseEntity<String> elideGet(@RequestParam Map<String, String> allRequestParams,
                                           HttpServletRequest request, Principal authentication) {
        String pathname = getJsonApiPath(request, settings.getJsonApiPath());

        ElideResponse response = elide.get(pathname, new MultivaluedHashMap<>(allRequestParams), authentication);
        return ResponseEntity.status(response.getResponseCode()).body(response.getBody());
    }

    private String getJsonApiPath(HttpServletRequest request, String prefix) {
        String pathname = (String) request
                .getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        return pathname.replaceFirst(prefix, "");
    }
}
