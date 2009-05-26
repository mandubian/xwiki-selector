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

import java.util.EnumSet;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.xpn.xwiki.wysiwyg.client.WysiwygService;
import com.xpn.xwiki.wysiwyg.client.editor.Strings;
import com.xpn.xwiki.wysiwyg.client.plugin.link.LinkConfig;
import com.xpn.xwiki.wysiwyg.client.util.StringUtils;
import com.xpn.xwiki.wysiwyg.client.widget.wizard.WizardStep;
import com.xpn.xwiki.wysiwyg.client.widget.wizard.NavigationListener.NavigationDirection;

/**
 * directly copied com.xpn.xwiki.wysiwyg.client.plugin.link.ui.CreateNewPageWizardStep
 * because it is not possible to extend this class as there are not accessors
 * to important attributes.
 * 
 * @author mandubian
 *
 */
public class XWikiCreateNewPageWizardStep implements WizardStep
{

	   /**
     * Main panel of this wizard.
     */
    private final Panel mainPanel = new FlowPanel();

    /**
     * The text box to add the name of the new page.
     */
    protected final TextBox pageNameTextBox = new TextBox();

    /**
     * Link data handled by this wizard step.
     */
    protected LinkConfig linkData;

    /**
     * Default constructor.
     */
    public XWikiCreateNewPageWizardStep()
    {
        Label pageNameLabel = new Label(Strings.INSTANCE.linkNewPageLabel());
        mainPanel.addStyleName("xLinkToNewPage");
        mainPanel.add(pageNameLabel);
        pageNameTextBox.setTitle(Strings.INSTANCE.linkNewPageTextBoxTooltip());
        mainPanel.add(pageNameTextBox);
    }

    /**
     * {@inheritDoc}
     */
    public Widget display()
    {
        return mainPanel;
    }

    /**
     * {@inheritDoc}
     */
    public String getDirectionName(NavigationDirection direction)
    {
        if (direction == NavigationDirection.NEXT) {
            return Strings.INSTANCE.select();
        } else {
            return null;
        }
    }

	public String getNextStep() {
       return null;
	}	
	
    /**
     * {@inheritDoc}
     */
    public EnumSet<NavigationDirection> getValidDirections()
    {
        return EnumSet.of(NavigationDirection.FINISH, NavigationDirection.CANCEL, NavigationDirection.PREVIOUS);
    }
	
    /**
     * @return the default navigation direction, to be fired automatically when enter is hit in an input in the form of
     *         this configuration wizard step. To be overridden by subclasses to provide the specific direction to be
     *         followed.
     */
    public NavigationDirection getDefaultDirection()
    {
        return NavigationDirection.FINISH;
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getResult()
    {
        return linkData;
    }

    /**
     * {@inheritDoc}
     */
    public String getStepTitle()
    {
        return Strings.INSTANCE.linkCreateNewPageTitle();
    }


    /**
     * {@inheritDoc}
     */
    public void init(Object data, AsyncCallback< ? > cb)
    {
        linkData = (LinkConfig) data;
        cb.onSuccess(null);
    }

    /**
     * {@inheritDoc}
     */
    public void onCancel(AsyncCallback<Boolean> async)
    {
        async.onSuccess(true);
    }
    
    /**
     * {@inheritDoc}
     */
    public void onSubmit(final AsyncCallback<Boolean> async)
    {
        // get the name of the page from the input
    	// change into final variables to be used in callback
        final String newPageName = pageNameTextBox.getText().trim();
        if (StringUtils.isEmpty(newPageName)) {
            Window.alert(Strings.INSTANCE.linkNewPageError());
            async.onSuccess(false);
        } else {
            // call the server to get the page URL and reference
            WysiwygService.Singleton.getInstance().getPageLink(linkData.getWiki(), linkData.getSpace(),
                newPageName, null, null, new AsyncCallback<LinkConfig>()
                {
                    public void onSuccess(LinkConfig result)
                    {
                        linkData.setUrl(result.getUrl());
                        linkData.setReference(result.getReference());
                        
                        // added this because why not setting also the 
                        // new page name???
                        linkData.setPage(newPageName);
                        async.onSuccess(true);
                    }

                    public void onFailure(Throwable caught)
                    {
                        async.onSuccess(false);
                    }
                });
        }
    }
}
