package org.mandubian.xwiki.selector.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

/**
 * This {@link Constants} interface is used to make user interface strings internationalizable.
 * 
 * @version $Id$
 */
public interface LoginDialogStrings extends Constants
{
    /**
     * An instance of this string bundle that can be used anywhere in the code to obtain i18n strings.
     */
	LoginDialogStrings INSTANCE = 
		(LoginDialogStrings) GWT.create(LoginDialogStrings.class);

    String loginCaption();
    
    String login();
    String loginFailed();
    String loginAsk();

    String username();

    String password();
    
    String submit();
}
