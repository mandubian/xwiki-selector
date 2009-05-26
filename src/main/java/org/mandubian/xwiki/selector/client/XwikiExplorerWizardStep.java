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
import com.xpn.xwiki.wysiwyg.client.WysiwygService;
import com.xpn.xwiki.wysiwyg.client.editor.Strings;
import com.xpn.xwiki.wysiwyg.client.plugin.link.LinkConfig;
import com.xpn.xwiki.wysiwyg.client.plugin.link.LinkConfig.LinkType;
import com.xpn.xwiki.wysiwyg.client.plugin.link.ui.WikipageExplorerWizardStep;
import com.xpn.xwiki.wysiwyg.client.plugin.link.ui.LinkWizard.LinkWizardSteps;
import com.xpn.xwiki.wysiwyg.client.util.ResourceName;
import com.xpn.xwiki.wysiwyg.client.util.StringUtils;
import com.xpn.xwiki.wysiwyg.client.widget.wizard.NavigationListener.NavigationDirection;

/**
 * @author mandubian
 *
 */
public class XwikiExplorerWizardStep extends
		WikipageExplorerWizardStep {

	public XwikiExplorerWizardStep(ResourceName editedResource) {
		super(editedResource);
	}

	@Override
	public String getNextStep() {
        if (getLinkData().getType() == LinkType.NEW_WIKIPAGE 
        		&& StringUtils.isEmpty(getLinkData().getPage())) {
            return LinkWizardSteps.WIKIPAGECREATOR.toString();
        } else {
        	// this is the difference with WikipageExplorerWizardStep
        	// NO step after exploration
            return null;
        }
	}	
	
    /**
     * {@inheritDoc}
     */
    public EnumSet<NavigationDirection> getValidDirections()
    {
        return EnumSet.of(NavigationDirection.CANCEL, NavigationDirection.NEXT);
    }
    
    /**
     * @return the default navigation direction, to be fired automatically when enter is hit in an input in the form of
     *         this configuration wizard step. To be overridden by subclasses to provide the specific direction to be
     *         followed.
     */
    public NavigationDirection getDefaultDirection()
    {
        return NavigationDirection.NEXT;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onSubmit(final AsyncCallback<Boolean> async)
    {
        // should check that the selection is ok according to the desired type and to "commit" it in the link config
        String selectedValue = getExplorer().getValue();
        // selected resource should not be empty
        if (StringUtils.isEmpty(selectedValue) && !getExplorer().isNewPage()) {
            Window.alert(Strings.INSTANCE.linkNoPageSelectedError());
            async.onSuccess(false);
        } else {
            // commit the changes in the config
            if (getExplorer().isNewPage()) {
                // if it's a new page to be created, set its parameters in the link config
                getLinkData().setType(LinkType.NEW_WIKIPAGE);
                getLinkData().setWiki(getExplorer().getSelectedWiki());
                getLinkData().setSpace(getExplorer().getSelectedSpace());
                getLinkData().setPage(getExplorer().getSelectedPage());
                // if the selected page is not set in the tree, i.e. the "New page..." option was chosen, return
                if (StringUtils.isEmpty(getExplorer().getSelectedPage())) {
                    async.onSuccess(true);
                    return;
                }
            } else {
                // it's an existing page
                getLinkData().setType(LinkType.WIKIPAGE);
                // set the page space wiki on nothing, since the link will have a reference
                getLinkData().setWiki(getExplorer().getSelectedWiki());
                getLinkData().setSpace(getExplorer().getSelectedSpace());
                getLinkData().setPage(getExplorer().getSelectedPage());
            }
            // build the link url and reference from the parameters.
            // TODO: restrict this to new pages when the explorer will return the selected resource URL, and get the
            // reference from the value of the tree
            WysiwygService.Singleton.getInstance().getPageLink(
        		getExplorer().getSelectedWiki(),
        		getExplorer().getSelectedSpace(), 
        		getExplorer().getSelectedPage(), null, null,
                new AsyncCallback<LinkConfig>()
                {
                    public void onSuccess(LinkConfig result)
                    {
                        getLinkData().setUrl(result.getUrl());
                        getLinkData().setReference(result.getReference());
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
