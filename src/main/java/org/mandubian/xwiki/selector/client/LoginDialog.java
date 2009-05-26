/*
 * Copyright © 2009 mandubian. All Rights Reserved.

 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, 
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution.
 * 
 * 3. The name of the author may not be used to endorse or promote products 
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY MANDUBIAN "AS IS" AND ANY EXPRESS OR IMPLIED 
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
package org.mandubian.xwiki.selector.client;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.xpn.xwiki.gwt.api.client.User;
import com.xpn.xwiki.wysiwyg.client.editor.Images;
import com.xpn.xwiki.wysiwyg.client.widget.ComplexDialogBox;

/**
 * Dialog for selecting one of the available macros.
 * 
 * @version $Id$
 */
public class LoginDialog extends ComplexDialogBox implements ClickListener
{

	/**
     * The login form
     */
    private final Label 		title;
    private final TextItem		username;
	private final TextItem 		password;
	private final DynamicForm 	form;	
    private final Button 		submit;

    private String user = null;
    private boolean acceptUnregistered = false;
    
    /**
     * The AsynCallback to perform action after submit...
     */
    private final AsyncCallback<String> submitCallback;
    private final AsyncCallback<String> isLoggedCallback;

    /**
     * Creates a new dialog for selecting one of the available macros. The dialog is modal.
     * 
     * @param config the object used to configure the newly created dialog
     */
    public LoginDialog(
    		AsyncCallback<String> submitCallback,
    		AsyncCallback<String> isLoggedCallback)
    {
        super(false, true);

        this.submitCallback = submitCallback;
        this.isLoggedCallback = isLoggedCallback;

        getDialog().setIcon(Images.INSTANCE.macroInsert().createImage());
        getDialog().setCaption(LoginDialogStrings.INSTANCE.loginCaption());

        //getHeader().add(new Label(Strings.INSTANCE.macroInsertDialogTitle()));

        title = new Label(LoginDialogStrings.INSTANCE.login());
        getHeader().add(title);
        
        form = new DynamicForm();  
        form.setWidth(250);  
        
        username = new TextItem();  
        username.setName("username");
        username.setTitle(LoginDialogStrings.INSTANCE.username());  
        username.setRequired(true);  
        username.setDefaultValue("");  
        
        password = new TextItem();
        password.setName("password");
        password.setTitle(LoginDialogStrings.INSTANCE.password());  
        password.setRequired(true);  
        password.setType("password");
        
        form.setFields(new FormItem[] {username, password});  
        getBody().add(form);
        
        submit = new Button(LoginDialogStrings.INSTANCE.submit());
        submit.addClickListener(this);
        getFooter().add(submit);
    }
    
    public LoginDialog(
    		AsyncCallback<String> submitCallback,
    		AsyncCallback<String> isLoggedCallback,
    		boolean acceptUnregisteredUser)
    {
    	this(submitCallback, isLoggedCallback);
    	this.acceptUnregistered = acceptUnregisteredUser;
    }
    
    public void reset()
    {
    	user = null;
    	username.setValue("");
    	password.setValue("");
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see ClickListener#onClick(Widget)
     */
    public void onClick(Widget sender)
    {
        if (sender == submit) {
            setLoading(true);
            setCanceled(false);
            XWikiSelector.Singleton.getXWikiServiceInstance().login(
        		form.getValueAsString("username"), 
        		form.getValueAsString("password"), 
        		true, 
        		new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						setLoading(false);
						showError(caught);
						submitCallback.onFailure(caught);
					}

					public void onSuccess(String result) {
						// if logged as guest, means login failed
						if(result == null)
						{
							user = null;
							setLoading(false);
							showError(new Throwable(LoginDialogStrings.INSTANCE.loginFailed()));
						}
						else if(result.equals("XWiki.XWikiGuest"))
						{
							if(!acceptUnregistered) {
								user = null;
								setLoading(false);
								showError(new Throwable(LoginDialogStrings.INSTANCE.loginFailed()));
							}
							else {
								user = result;
								setLoading(false);
								submitCallback.onSuccess(user);	
								hide();
							} 
						}
						else {
							user = result;
							setLoading(false);
							submitCallback.onSuccess(user);	
							hide();
						}
					}
        		});
        }
    }

    public boolean isLogged()
    {    		
    	if(user == null) {
    		XWikiSelector.Singleton.getXWikiServiceInstance().getUser(
        		new AsyncCallback<User>() {
					public void onFailure(Throwable caught) {
						showError(caught);
						isLoggedCallback.onFailure(caught);						
					}

					public void onSuccess(User result) {
						if(result == null)
						{
							isLoggedCallback.onFailure(
									new Throwable(LoginDialogStrings.INSTANCE.loginAsk()));	
						}
						else if(result.getFullName().equals("XWiki.XWikiGuest"))
						{
							if(acceptUnregistered) {
								user = result.getFullName();
								isLoggedCallback.onSuccess(user);
							}
							else isLoggedCallback.onFailure(
									new Throwable(LoginDialogStrings.INSTANCE.loginAsk()));
						}
						else {
							user = result.getFullName();
							isLoggedCallback.onSuccess(user);
						}
					}
        		});
    		return false;
    	}
    	else return true;
    }

	public boolean acceptUnregistered() {
		return acceptUnregistered;
	}

	public void setAcceptUnregistered(boolean acceptUnregistered) {
		this.acceptUnregistered = acceptUnregistered;
	}
}
