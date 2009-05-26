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

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import com.xpn.xwiki.gwt.api.client.XWikiService;
import com.xpn.xwiki.gwt.api.client.XWikiServiceAsync;
import com.xpn.xwiki.gwt.api.client.app.XWikiAsyncCallback;
import com.xpn.xwiki.gwt.api.client.app.XWikiGWTAppConstants;
import com.xpn.xwiki.gwt.api.client.app.XWikiGWTDefaultApp;

import com.xpn.xwiki.wysiwyg.client.util.Config;
import com.xpn.xwiki.wysiwyg.client.util.StringUtils;
import com.xpn.xwiki.wysiwyg.client.util.internal.DefaultConfig;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * Largely inspired by XWiki Wysiwyg GWT application
 * 
 * @author mandubian
 */
public class XWikiSelector extends XWikiGWTDefaultApp implements EntryPoint 
{
    /**
     * Utility class for accessing the service stub.
     */
    public static final class Singleton
    {
        /**
         * The service stub.
         */
    	private static  XWikiServiceAsync serviceInstance;

        /**
         * Private constructor because this is a utility class.
         */
        private Singleton()
        {
        }
		
		/**
		 * Creates an instance of an XWiki Service
		 * @return
		 */
		public static synchronized XWikiServiceAsync getXWikiServiceInstance() 
		{
		    if (serviceInstance == null) {
		        String moduleBaseURL = GWT.getModuleBaseURL();
		        String baseURL = moduleBaseURL.substring(
		        		0, moduleBaseURL.indexOf(GWT.getModuleName()) - 1);
		        String defaultXWikiService = 
		        	baseURL + XWikiGWTAppConstants.XWIKI_DEFAULT_SERVICE;
		
		        String serviceURL;
                try {
                    // Look in the global configuration object.
                    serviceURL = Dictionary.getDictionary("Wysiwyg").get("xwikiservice");
                } catch (MissingResourceException e) {
                    serviceURL = defaultXWikiService;
                }
		        
		        serviceInstance = (XWikiServiceAsync) GWT.create(XWikiService.class);
		        ((ServiceDefTarget) serviceInstance).setServiceEntryPoint(serviceURL);
		    }
		    return serviceInstance;
		}
    }
	
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() { 
      setName("XWikiSelector");
      // Test to see if we're running in hosted mode or web mode.
      if (!GWT.isScript()) {
          // We're running in hosted mode so we need to login first.
          getXWikiServiceInstance().login("Admin", "admin", true, 
    		  new XWikiAsyncCallback(this)
	          {	        	  
	              public void onFailure(Throwable caught)
	              {
	                  super.onFailure(caught);
	              }
	
	              public void onSuccess(Object result)
	              {
	                  super.onSuccess(result);
	                  loadUI();
	              }
	          });
      } else {
          loadUI();
      } 	  
  }
  
  
  /**
   * Loads all the WYSIWYG editors on the host page.
   */
  private void loadUI()
  {
	  for (final Config config : getConfigs()) {
		  // hook is the container in which the xwikiselector button
		  // will be inserted
          String hookId = config.getParameter("hookId");
          // hookSpace is the space input
          String hookSpaceId = config.getParameter("hookSpaceId");
          // hookpage is the page input
          String hookPageId = config.getParameter("hookPageId");
          if (hookId == null || hookSpaceId==null || hookPageId == null) {
              continue;
          }

          final Element hook = DOM.getElementById(hookId);
          final Element hookSpace = DOM.getElementById(hookSpaceId);
          final Element hookPage = DOM.getElementById(hookPageId);

          // Extract info from DOM
          //String height = String.valueOf(Math.max(hook.getOffsetHeight(), 100)) + "px";

          // Prepare the DOM
          // Hide the hook element
          //hook.getStyle().setProperty(Style.DISPLAY, Style.Display.NONE);
          // Create a container for the editor UI
          Element container = DOM.createDiv();
          String containerId = hookId + "_container";
          container.setId(containerId);
          hook.appendChild(container);
          //hook.getParentElement().insertBefore(container, hook.getNextSibling());
                             
          ClickListener explorerListener = new ClickListener(){
              public void onClick(Widget sender){
            	  XWikiSelectorWizardHolder wizardHolder = 
            		  new XWikiSelectorWizardHolder(config, hookSpace, hookPage);
            	  wizardHolder.start();
              }
          };
          
          Button explorerButton = new Button(XWikiSelectorStrings.INSTANCE.buttonLabel());
          explorerButton.addClickListener(explorerListener);
          
          RootPanel.get(containerId).add(explorerButton);

	  }
  }
  
  /**
   * @return The list of configuration objects present in the host page.
   */
  private List<Config> getConfigs()
  {
      List<Config> configs = new ArrayList<Config>();
      int i = 0;
      Config config = getConfig(i++);
      while (config != null) {
          configs.add(config);
          config = getConfig(i++);
      }
      return configs;
  }

  /**
   * Retrieves the configuration object associated with the WYSIWYG editor with the specified index. We can have more
   * than one WYSIWYG editor in a host page and thus each editor will have an index. The first index is 0. A
   * configuration object is a JavaScript object that can be loaded with GWT's {@link Dictionary} mechanism.
   * 
   * @param index The index of the editor whose configuration object must be retrieved.
   * @return The configuration object for the specified editor.
   */
  private Config getConfig(int index)
  {
      Dictionary dictionary = null;
      try {
          dictionary = Dictionary.getDictionary(getName() + String.valueOf(index));
          return new DefaultConfig(dictionary);
      } catch (MissingResourceException e) {
          return null;
      }
  }

  /**
   * {@inheritDoc}<br/>
   * NOTE: We overwrite this method in order to be able to control the URL of the XWikiService.
   * 
   * @see XWikiGWTDefaultApp#getParam(String, String)
   */
  public String getParam(String key, String defaultValue)
  {
      // First look for meta gwt:property.
      String value = getProperty(key);
      if (!StringUtils.isEmpty(value)) {
          return value;
      }
      // Then look in the global configuration object.
      try {
          return Dictionary.getDictionary(getName()).get(key);
      } catch (MissingResourceException e) {
          return defaultValue;
      }
  }
}
