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

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.xpn.xwiki.wysiwyg.client.editor.Images;
import com.xpn.xwiki.wysiwyg.client.util.Config;
import com.xpn.xwiki.wysiwyg.client.util.ResourceName;
import com.xpn.xwiki.wysiwyg.client.widget.wizard.SourcesNavigationEvents;
import com.xpn.xwiki.wysiwyg.client.widget.wizard.Wizard;
import com.xpn.xwiki.wysiwyg.client.widget.wizard.WizardStep;
import com.xpn.xwiki.wysiwyg.client.widget.wizard.WizardStepProvider;

/**
 * 
 * @author mandubian
 * 
 */
public class XWikiSelectorWizard extends Wizard 
			implements WizardStepProvider
{

	/**
     * Enumeration steps handled by this link wizard.
     */
    public static enum LinkWizardSteps
    {
        /**
         * Steps managed by this wizard.
         */
        WIKIPAGE, WIKIPAGECREATOR
    };

    /**
     * Map with the instantiated steps to return. Will be lazily initialized upon request.
     */
    private Map<LinkWizardSteps, WizardStep> stepsMap = new HashMap<LinkWizardSteps, WizardStep>();

    /**
     * The resource currently edited by this WYSIWYG, used to determine the context in which link creation takes place.
     */
    private Config config;
    
    private LoginDialog loginDialog = null;
    private String backupStep;
    private Object backupStepData;
    private boolean showLogout = false;
    private boolean acceptUnregistered = false;
    

    /**
     * Builds a {@link XWikiSelectorWizard} from the passed {@link Config}. The configuration is used to get WYSIWYG editor
     * specific information for this wizard, such as the current page, etc.
     * 
     * @param config the context configuration for this {@link XWikiSelectorWizard}
     */
    public XWikiSelectorWizard(Config config)
    {
        super("Select", Images.INSTANCE.link().createImage());
        this.config = config;
        this.setProvider(this);
        
        if(config.getParameter("acceptUnregistered")!=null)
        	acceptUnregistered = 
        		config.getParameter("acceptUnregistered")
        			.equals("true")?true:false;
    }

    LoginDialog getLoginDialog()
    {

    	if(loginDialog == null)
    	{
    		AsyncCallback<String>
				submitCallback = new AsyncCallback<String>() {
					public void onFailure(Throwable arg0) {
						// failed login
						onFinish();
					}
		
					public void onSuccess(String user) {
						if(!user.equals("XWiki.XWikiGuest"))
							showLogout = true;
									            
						startReal(backupStep, backupStepData);
					}
    		
    			};
		
    		AsyncCallback<String>
				isLoggedCallback = new AsyncCallback<String>() {
					public void onFailure(Throwable arg0) {
						// not logged, show login dialog
						loginDialog.center();
					}
		
					public void onSuccess(String user) {
						if(!user.equals("XWiki.XWikiGuest"))
							showLogout = true;
						// already logged, start wizard
						startReal(backupStep, backupStepData);
					}
    		
    			};	
    		loginDialog = new LoginDialog(
    					submitCallback, isLoggedCallback, acceptUnregistered);
    		
	        loginDialog.addPopupListener(this);
    	}
    	
    	return loginDialog;
    }
    
    @Override
	public void start(String startStep, Object data) {
    	this.backupStep = startStep;
    	this.backupStepData = data;
    	        
    	if(getLoginDialog().isLogged())
    		super.start(startStep, data);		
	}

	private void startReal(String startStep, Object data) {
		super.start(startStep, data);		
	}
    
	
    /**
     * Initializes and displays the current wizard step, with the passed data.
     * 
     * @param data the data to initialize the current step with
     */
	@Override
    protected void initAndDisplayCurrentStep(Object data)
    {
        dialog.center();
        dialog.setLoading(true);
        currentStep.init(data, new AsyncCallback<Object>()
        {
            public void onSuccess(Object result)
            {
                dialog.displayStep(currentStep, navigationStack.size() > 1);
                // shows logout button in dialog header
                if(showLogout)
                {
					// already logged, start wizard
					Button logoutButton = 
						new Button(XWikiSelectorStrings.INSTANCE.logout());
					logoutButton.addClickListener(new ClickListener()
		            {
		                public void onClick(Widget sender)
		                {
		                	String url = 
		                		config.getParameter(
		                				"logoutUrl", 
		                				"/xwiki/bin/logout/XWiki/XWikiLogout");
		                	
		                	RequestBuilder builder = 
		                		new RequestBuilder(RequestBuilder.GET, url);

		                    try {
		                    	Request response = builder.sendRequest(
	                    			null, 
	                    			new RequestCallback() {
	                    				public void onError(Request request, Throwable exception) {
	                    					dialog.showError(exception);
	                    				}

	                    				public void onResponseReceived(Request request, Response response) {
	                    					getLoginDialog().reset();
	                    					onFinish();
	                    					start(backupStep, backupStepData);
	                    				}

	                    			}
		                    	);
		                    } 
		                    catch (RequestException e) {
		                      // Code omitted for clarity
		                    }

		                }
		            });
					FlowPanel logoutPanel = new FlowPanel();
					logoutPanel.addStyleName("button-container");
					logoutPanel.add(logoutButton);
		            
					dialog.getHeader().add(logoutPanel);
                }
                
                if (currentStep instanceof SourcesNavigationEvents) {
                    ((SourcesNavigationEvents) currentStep).addNavigationListener(XWikiSelectorWizard.this);
                }
            }

            public void onFailure(Throwable caught)
            {
                dialog.showError(caught);
            }
        });
    }	
    /**
     * {@inheritDoc}
     * 
     * @see WizardStepProvider#getStep(String)
     */
    public WizardStep getStep(String name)
    {
        LinkWizardSteps requestedStep = parseStepName(name);
        WizardStep step = stepsMap.get(requestedStep);
        if (step == null) {
            switch (requestedStep) {
                case WIKIPAGE:
                    step = new XwikiExplorerWizardStep(getEditedResource());
                    break;
                case WIKIPAGECREATOR:
                    step = new XWikiCreateNewPageWizardStep();
                    break;                
                default:
                    // nothing here, leave it null
                    break;
            }
            // if something has been created, add it in the map
            if (step != null) {
                stepsMap.put(requestedStep, step);
            }
        }
        // return the found or newly created step
        return step;
    }

    /**
     * Handle advancing to the next step.
     */
    @Override
    protected void onNext()
    {
        // get the step from the next action of this step
        String nextStepName = currentStep.getNextStep();
        
        // overrides onNext because super.onNext prevents from finishing
        // wizard after first step
        if(nextStepName == null){
            onFinish();
            return;
        }
        
        WizardStep nextStep = provider.getStep(nextStepName);
        if (nextStep == null) {
            onFinish();
            return;
        }
        // prepare next step
        Object result = currentStep.getResult();
        unloadCurrentStep();
        currentStep = nextStep;
        navigationStack.push(nextStepName);
        initAndDisplayCurrentStep(result);
    }
    
    /**
     * @return the currently edited resource, from the configuration
     */
    private ResourceName getEditedResource()
    {
        return new ResourceName(
        		config.getParameter("wiki"), 
        		config.getParameter("space"), 
        		config.getParameter("page"),
            null);
    }

    /**
     * Parses the specified step name in a {@link LinkWizardSteps} value.
     * 
     * @param name the name of the step to parse
     * @return the {@link LinkWizardSteps} {@code enum} value corresponding to the passed name, or {@code null} if no
     *         such value exists.
     */
    private LinkWizardSteps parseStepName(String name)
    {
        // let's be careful about this
        LinkWizardSteps requestedStep = null;
        try {
            requestedStep = LinkWizardSteps.valueOf(name);
        } catch (IllegalArgumentException e) {
            // nothing, just leave it null if it cannot be found in the enum
        }
        return requestedStep;
    }


}
