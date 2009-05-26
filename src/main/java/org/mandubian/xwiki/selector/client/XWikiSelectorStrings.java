package org.mandubian.xwiki.selector.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

/**
 * This {@link Constants} interface is used to make user interface strings internationalizable.
 * 
 * @version $Id$
 */
public interface XWikiSelectorStrings extends Constants
{
    /**
     * An instance of this string bundle that can be used anywhere in the code to obtain i18n strings.
     */
	XWikiSelectorStrings INSTANCE = 
		(XWikiSelectorStrings) GWT.create(XWikiSelectorStrings.class);

    String buttonLabel();

    String logout();

}
