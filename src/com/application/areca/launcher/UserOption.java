package com.application.areca.launcher;

/**
 * <BR>
 * @author Olivier PETRUCCI
 * <BR>
 * <BR>Areca Build ID : 2156529904998511409
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
public class UserOption {
    private int tokens;
    private String name;

    public UserOption(String name, int tokens) {
        super();
        this.tokens = tokens;
        this.name = name;
        
        if (tokens < 1 || tokens > 2) {
            throw new IllegalArgumentException();
        }
    }

    public String getName() {
        return name;
    }
    public int getTokens() {
        return tokens;
    }
}
