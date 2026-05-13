package eu.ill.visa.web.rest;

import io.smallrye.config.WithName;

public interface DesktopConfiguration {

    @WithName("allowedClipboardUrlHosts")
    String allowedClipboardUrlHostsJson();

    @WithName("keyboardLayouts")
    String keyboardLayoutsJson();

    @WithName("screenResizingEnabled")
    boolean screenResizingEnabled();

    @WithName("autoScreenResize")
    boolean autoScreenResize();

}
