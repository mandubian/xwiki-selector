/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.xpn.xwiki.wysiwyg.client.widget;

import java.util.ArrayList;

/**
 * A collection of {@link MenuListener}s. It provides an easy way of notifying all the listeners when a menu event is
 * triggered.
 * 
 * @version $Id$
 */
public class MenuListenerCollection extends ArrayList<MenuListener>
{
    /**
     * Field required by all {@link java.io.Serializable} classes.
     */
    private static final long serialVersionUID = -1315507848121982574L;

    /**
     * Notifies all the listeners in this collection.
     * 
     * @param menuItem the menu item which has been selected
     */
    public void fireMenuItemSelected(MenuItem menuItem)
    {
        for (MenuListener listener : this) {
            listener.onMenuItemSelected(menuItem);
        }
    }
}
