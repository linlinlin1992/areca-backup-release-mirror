package com.application.areca.launcher.gui.common;

import com.application.areca.context.ReportingConfiguration;

/**
 * @author Stephane Brunel
 * <BR>
 * <BR>Areca Build ID : 3274863990151426915
 */
 
 /*
 Copyright 2005-2007, Olivier PETRUCCI.
 
This file is part of Areca.

    Areca is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    Areca is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Areca; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
public final class ArecaPreferences {
    private static final String STARTUP_MODE = "startup.mode";
	private static final String STARTUP_WORKSPACE = "startup.workspace";
	private static final String LAST_WORKSPACE = "lastworkspace";
	private static final String LANG = "lang";
	private static final String LNF = "lnf";
	private static final String ARCHIVE_STORAGE = "archive.storage";
	private static final String DISPLAY_REPORT = "archive.displayreport";
	private static final String LAST_WORKSPACE_COPY_LOCATION = "workspace.last.copy.location";
	private static final String LAST_WORKSPACE_COPY_MASK = "workspace.last.copy.mask";
	private static final String DISPLAY_LOG = "log.display";
    private static final String TEXT_EDITOR = "editor.text";
    private static final String INFO_SYNTHETIC = "info.channel.synthetic";
	
	public static final int UNDEFINED = -1;
	public static final int LAST_WORKSPACE_MODE = 0;
	public static final int DEFAULT_WORKSPACE_MODE = 1;
	
	static {
	    synchronizeClientConfigurations();
	}
	
	public static String getDefaultArchiveStorage() {
	    return LocalPreferences.instance().get(ARCHIVE_STORAGE, "");
	}
	
	public static void setDefaultArchiveStorage(String dir) {
	    LocalPreferences.instance().set(ARCHIVE_STORAGE, dir);
	    synchronizeClientConfigurations();
	}
	
	public static void setLastWorkspaceCopyLocation(String dir) {
	    LocalPreferences.instance().set(LAST_WORKSPACE_COPY_LOCATION, dir);
	    synchronizeClientConfigurations();
	}
    
    public static void setEditionCommand(String command) {
        LocalPreferences.instance().set(TEXT_EDITOR, command);
        synchronizeClientConfigurations();
    }
	
	public static void setLastWorkspaceCopyMask(boolean mask) {
	    LocalPreferences.instance().set(LAST_WORKSPACE_COPY_MASK, mask);
	    synchronizeClientConfigurations();
	}
    
    public static void setInformationSynthetic(boolean synthetic) {
        LocalPreferences.instance().set(INFO_SYNTHETIC, synthetic);
        synchronizeClientConfigurations();
    }
	
	public static String getLastWorkspace() {
	    return LocalPreferences.instance().get(LAST_WORKSPACE, "");
	}
	
	public static String getLastWorkspaceCopyLocation() {
	    return LocalPreferences.instance().get(LAST_WORKSPACE_COPY_LOCATION, "");
	}
    
    public static String getEditionCommand() {
        return LocalPreferences.instance().get(TEXT_EDITOR, "");
    }
	
	public static boolean getLastWorkspaceCopyMask() {
	    return LocalPreferences.instance().getBoolean(LAST_WORKSPACE_COPY_MASK);
	}
    
    public static boolean isInformationSynthetic() {
        return LocalPreferences.instance().getBoolean(INFO_SYNTHETIC, true);
    }
	
	public static void setLastWorkspace(String lw) {
	    LocalPreferences.instance().set(LAST_WORKSPACE, lw);
	    synchronizeClientConfigurations();
	}
	
	public static int getStartupMode() {
	    String mode = LocalPreferences.instance().get(STARTUP_MODE);
	    if ("last".equals(mode)) {
	        return LAST_WORKSPACE_MODE;
	    } else if ("default".equals(mode)) {
	        return DEFAULT_WORKSPACE_MODE;
	    }
	    return UNDEFINED;
	}
	
	public static void setStartupMode(int mode) {
	    LocalPreferences.instance().set(STARTUP_MODE, mode == LAST_WORKSPACE_MODE ? "last" : "default");
	    synchronizeClientConfigurations();
	}
	
	public static String getDefaultWorkspace() {
	    return LocalPreferences.instance().get(STARTUP_WORKSPACE, "");
	}
	
	public static void setDefaultWorkspace(String dw) {
	    LocalPreferences.instance().set(STARTUP_WORKSPACE, dw);
	    synchronizeClientConfigurations();
	}
	
	public static String getLnF() {
	    return LocalPreferences.instance().get(LNF);
	}

	public static void setLnF(String lnf) {
	    LocalPreferences.instance().set(LNF, lnf);
	    synchronizeClientConfigurations();
	}
	
	public static void setDisplayLog(boolean displayLog) {
	    LocalPreferences.instance().set(DISPLAY_LOG, displayLog);
	    synchronizeClientConfigurations();
	}
	
	public static boolean getDisplayLog() {
	    return LocalPreferences.instance().getBoolean(DISPLAY_LOG);
	}
	
	public static String getLang() {
	    return LocalPreferences.instance().get(LANG, "en");
	}
	
	public static void setLang(String lang) {
	    LocalPreferences.instance().set(LANG, lang);
	    synchronizeClientConfigurations();
	}

	public static boolean getDisplayReport() {
	    return LocalPreferences.instance().getBoolean(DISPLAY_REPORT);
	}
	
	public static void setDisplayReport(boolean display) {
	    LocalPreferences.instance().set(DISPLAY_REPORT, display);
	    synchronizeClientConfigurations();
	}
	
	private static void synchronizeClientConfigurations() {
	    ReportingConfiguration.getInstance().setReportingEnabled(getDisplayReport());
	}
}