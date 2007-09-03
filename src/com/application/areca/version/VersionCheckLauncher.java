package com.application.areca.version;

import javax.swing.JOptionPane;

import org.eclipse.swt.widgets.Display;

import com.application.areca.ArecaTechnicalConfiguration;
import com.myJava.util.log.Logger;
import com.myJava.util.os.OSTool;

/**
 * <BR>
 * @author Olivier PETRUCCI
 * <BR>
 * <BR>Areca Build ID : -2622785387388097396
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
public class VersionCheckLauncher {

    public static void main(String[] args) {
        
        try {     
            checkJavaVersion();
            ArecaTechnicalConfiguration.initialize();
            
            VersionCheckWindow check = new VersionCheckWindow();
            check.setBlockOnOpen(true);
            check.open();
            Display.getCurrent().dispose();
        } catch (Throwable e) {
            e.printStackTrace();
            Logger.defaultLogger().error("Unexpected error", e);
        }
    }
    
    private static void checkJavaVersion() {
        if (! OSTool.isJavaVersionGreaterThanOrEquals(VersionInfos.REQUIRED_JAVA_VERSION)) {
            System.out.println("----------------------------------------------------------------------------------\n ");
            System.out.println(VersionInfos.VERSION_MSG);
            System.out.println("----------------------------------------------------------------------------------");
            
            JOptionPane.showMessageDialog(null,
                    VersionInfos.VERSION_MSG, VersionInfos.APP_NAME + " - Invalid Java Version", JOptionPane.ERROR_MESSAGE);

            System.exit(-1);
        }
    }
}
