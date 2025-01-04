package org.gumball.ajug.devnexus.desktop.preferences;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.prefs.Preferences;

@Component
public class AppUserPreferences {
    public String getDownloadDirectory() {
        Preferences prefs = Preferences.userRoot().node(Constants.rootNode);
        String dir = prefs.get(Constants.downloadNodeName, Constants.defaultDownload);
        return dir;
    }
}
