package de.breuninger.coding.challenge.homefeed.controller.rest;

import de.breuninger.coding.challenge.homefeed.controller.mapper.HomefeedDtoMapper;
import de.breuninger.coding.challenge.homefeed.eto.HomefeedModuleGroupEto;
import de.breuninger.coding.challenge.homefeed.eto.HomefeedResponse;
import de.breuninger.coding.challenge.homefeed.service.HomefeedService;
import de.breuninger.coding.challenge.homefeed.service.module.HomefeedModuleGroup;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/homefeed")
public class HomefeedController {
    private static final Logger logger = LoggerFactory.getLogger(HomefeedController.class);

    private final HomefeedService homefeedService;

    public HomefeedController(HomefeedService homefeedService) {
        this.homefeedService = homefeedService;
    }

    @GetMapping
    public ResponseEntity<HomefeedResponse> getHomefeed(
            @RequestHeader(value = "X-User-Id", required = false) String userId
    ) {
        logger.info("Received request to get Homefeed for {}", StringUtils.isBlank(userId) ? "anonymous" : userId);
        List<HomefeedModuleGroup> homefeedModules = homefeedService.getHomefeed(userId);

        List<HomefeedModuleGroupEto> modules = homefeedModules.stream()
                .map(HomefeedDtoMapper::toGroupDto)
                .toList();

        HomefeedResponse response = new HomefeedResponse(modules);
        return ResponseEntity.ok(response);
    }
}
