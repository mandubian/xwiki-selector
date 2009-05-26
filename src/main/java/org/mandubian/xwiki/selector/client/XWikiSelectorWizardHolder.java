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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.xpn.xwiki.wysiwyg.client.plugin.link.LinkConfig;
import com.xpn.xwiki.wysiwyg.client.plugin.link.ui.LinkWizard.LinkWizardSteps;
import com.xpn.xwiki.wysiwyg.client.util.Config;
import com.xpn.xwiki.wysiwyg.client.widget.wizard.Wizard;
import com.xpn.xwiki.wysiwyg.client.widget.wizard.WizardListener;

/**
 * @author mandubian
 *
 */
public class XWikiSelectorWizardHolder implements WizardListener 
{
	private XWikiSelectorWizard 	wizard;
	private Config					config;			
	private Element					hookSpace;
	private Element					hookPage;
		
	public XWikiSelectorWizardHolder(
			Config config, Element hookSpace, Element hookPage)
	{
		this.config = config;
		this.hookSpace = hookSpace;
		this.hookPage = hookPage;		
	}
	
	/* (non-Javadoc)
	 * @see com.xpn.xwiki.wysiwyg.client.widget.wizard.WizardListener#onCancel(com.xpn.xwiki.wysiwyg.client.widget.wizard.Wizard)
	 */
	public void onCancel(Wizard sender) {
        GWT.log("WIZARD CANCELLED", null);
	}

	/* (non-Javadoc)
	 * @see com.xpn.xwiki.wysiwyg.client.widget.wizard.WizardListener#onFinish(com.xpn.xwiki.wysiwyg.client.widget.wizard.Wizard, java.lang.Object)
	 */
	public void onFinish(Wizard sender, Object result) {
		// build the HTML block from the configuration data
		LinkConfig cfg = (LinkConfig)result;
        //String linkHTML = LinkHTMLGenerator.getInstance().getLinkHTML((LinkConfig) result);
        
        hookSpace.setAttribute("value", cfg.getSpace());
        hookPage.setAttribute("value", cfg.getPage());
	}

    /**
     * Returns the link wizard.
     * 
     * @return the link wizard.
     */
    private XWikiSelectorWizard getWizard()
    {
        if (wizard == null) {
        	wizard = new XWikiSelectorWizard(this.config);
        	wizard.addWizardListener(this);
        }
        return wizard;
    }
	
    public void start()
    {
    	getWizard().start(
    			LinkWizardSteps.WIKIPAGE.toString(), 
    			new LinkConfig());
    }
}
