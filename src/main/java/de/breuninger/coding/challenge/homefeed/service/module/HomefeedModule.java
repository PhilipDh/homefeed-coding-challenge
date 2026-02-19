package de.breuninger.coding.challenge.homefeed.service.module;

import de.breuninger.coding.challenge.homefeed.service.ModuleDisplayTypeEnum;
import de.breuninger.coding.challenge.homefeed.service.UserContext;

import java.util.List;

/**
 * Core interface for homefeed content modules. Each module provides a specific type of content
 * (e.g., banners, greetings, highlights) that can be loaded independently and in parallel.
 * Modules are discovered automatically via Spring's dependency injection and executed with
 * a configurable timeout. If a module exceeds the timeout, it is excluded from the response
 * rather than failing the entire homefeed request.
 * NOTE: When adding a new module remember that a user might be anonymous, which can fetched from teh UserContext
 */
public interface HomefeedModule {

    /**
     * Retrieves module entries based on the provided user context.
     *
     * @param context User context containing authentication status, preferences, and segments
     * @return List of entries to display, or empty list if no content is available
     */
    List<HomefeedEntry> getEntries(UserContext context);

    /**
     * @return Unique identifier for this module type
     */
    String getType();

    /**
     * @return Priority for ordering modules in the response (lower values appear first)
     */
    int getPriority();

    /**
     * @return Display type hint for the frontend on how to render this module
     */
    ModuleDisplayTypeEnum getDisplayType();
}
