package com.tokio.otpfilter73.filter;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.filter.FilterConfig;
import javax.portlet.filter.PortletFilter;
import javax.portlet.filter.RenderFilter;
import javax.portlet.filter.RenderResponseWrapper;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.util.PortletKeys;


@Component(
		 immediate = true, 
		 property = {
				 /*"javax.portlet.name=" + PortletKeys.LOGIN,
				 "javax.portlet.name=" + PortletKeys.FAST_LOGIN,*/
		    }, 
		 service = PortletFilter.class
		 )
public class LoginRenderFilter implements RenderFilter  {
	
	@Override
	public void init(FilterConfig filterConfig) throws PortletException {
		
	}
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void doFilter(RenderRequest request, RenderResponse response, javax.portlet.filter.FilterChain chain)
			throws IOException, PortletException {
		
		System.out.println("-------FILTER---------");
		
		RenderResponseWrapper renderResponseWrapper = new BufferedRenderResponseWrapper(response);

		chain.doFilter(request, renderResponseWrapper);

		String text = renderResponseWrapper.toString();
		
		if (text != null) {
			String interestingText = "<input  class=\"field form-control\"";

			int index = text.lastIndexOf(interestingText);

			if (index >= 0) {
				String newText1 = text.substring(0, index);
				String newText2 = "\n<div class=\"row\">\n" + 
						"    <div class=\"col-md-12\">\n" + 
						"    <img src=\"/documents/35972/236235/Banner+1-100.jpg/4808dda2-81dd-c2dd-5162-54d4ec713280?t=1581012070211\" style=\"\n" + 
						"    width: 100%;\n" + 
						"\"></div></div>\n";
				String newText3 = text.substring(index);
				
				String newText = newText2 + newText1 + newText3;
				
				response.getWriter().write(newText);
			}
		}
		
	}

	 
	 
}