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

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * An item in a list box. It can contain other widgets not just plain text.
 * 
 * @version $Id$
 */
public class ListItem extends FlowPanel
{
    /**
     * The style name suffix attached to selected list items.
     */
    private static final String DEPENDENT_STYLENAME_SELECTED_ITEM = "selected";

    /**
     * Creates a new list item.
     */
    public ListItem()
    {
        setStylePrimaryName("xListItem");
    }

    /**
     * Visually marks this list item as selected or unselected based on the given argument.
     * 
     * @param selected {@code true} to select this list item, {@code false} to remove the selection
     */
    public void setSelected(boolean selected)
    {
        if (selected) {
            addStyleDependentName(DEPENDENT_STYLENAME_SELECTED_ITEM);
        } else {
            removeStyleDependentName(DEPENDENT_STYLENAME_SELECTED_ITEM);
        }
    }
}
