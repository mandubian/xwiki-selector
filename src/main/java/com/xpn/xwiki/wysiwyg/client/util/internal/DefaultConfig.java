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
package com.xpn.xwiki.wysiwyg.client.util.internal;

import java.util.Collections;
import java.util.Set;

import com.google.gwt.i18n.client.Dictionary;
import com.xpn.xwiki.wysiwyg.client.util.Config;

/**
 * The default implementation of {@link Config} interface. This implementation wraps a {@link Dictionary} instance build
 * from a JavaScript object in the host HTML page.
 * 
 * @version $Id$
 */
public final class DefaultConfig implements Config
{
    /**
     * Empty configuration.
     */
    public static final DefaultConfig DEFAULT = new DefaultConfig();

    /**
     * This is build from a JavaScript object in the container HTML page.
     */
    private final Dictionary params;

    /**
     * Creates a new empty configuration object.
     */
    private DefaultConfig()
    {
        params = null;
    }

    /**
     * Creates a new configuration object based on the given dictionary.
     * 
     * @param params a dictionary.
     */
    public DefaultConfig(Dictionary params)
    {
        this.params = params;
    }

    /**
     * {@inheritDoc}
     * 
     * @see Config#getParameter(String)
     */
    public String getParameter(String paramName)
    {
        return getParameter(paramName, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see Config#getParameter(String, String)
     */
    public String getParameter(String paramName, String defaultValue)
    {
        try {
            Object paramValue = params.get(paramName);
            return (paramValue == null) ? defaultValue : String.valueOf(paramValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see Config#getParameterNames()
     */
    public Set<String> getParameterNames()
    {
        if (params != null) {
            return params.keySet();
        } else {
            return Collections.emptySet();
        }
    }
}
