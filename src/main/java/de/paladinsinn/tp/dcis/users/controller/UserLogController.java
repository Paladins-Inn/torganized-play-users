package de.paladinsinn.tp.dcis.users.controller;

import de.paladinsinn.tp.dcis.commons.ui.WebUiModelDefaultValueSetter;
import de.paladinsinn.tp.dcis.domain.users.model.UserLogEntry;
import de.paladinsinn.tp.dcis.users.domain.services.UserLogService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;


@Controller
@RequestMapping("/log")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@XSlf4j
public class UserLogController {
    private static final String DEFAULT_PAGE_SIZE = "25";
    private static final String DEFAULT_PAGE_OFFSET = "0";
    
    private final UserLogService logService;
    private final WebUiModelDefaultValueSetter uiSetter;

    
    @GetMapping(path = "/{id}")
    @RolesAllowed("PLAYER")
    public String showLogEntries(
        @PathVariable final UUID id,
        @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE_OFFSET) Integer page,
        @RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer size,
        Authentication authentication, Model model
    ) {
        log.entry(id, page, size, authentication, model);

        prepareList(id, uiSetter.fullUrl("log/" + id), page, size, model);

        return log.exit(uiSetter.addContextPath("user-logs", authentication, model));
    }
    
    /**
     * Prepares the data for the list of operatives.
     *
     * @param url The URL to call for the next page.
     * @param size The size of the list.
     * @param page The displayed page number.
     * @param model The data model to fill with the data.
     */
    private void prepareList(final UUID id, final String url, final int size, final int page, Model model) {
        log.entry(url, size, page, model);
        
        Pageable p = Pageable.ofSize(size).withPage(page);
        
        Page<UserLogEntry> entries = logService.load(id, p);
        log.info("User log loaded. page={}, size={}, noOfEntries={}", page, size, entries.getTotalElements());
        
        model.addAttribute("url", url);
        model.addAttribute("entries", entries);
        
        log.exit();
    }
    
}
