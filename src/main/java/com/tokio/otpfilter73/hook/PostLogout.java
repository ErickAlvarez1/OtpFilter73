package com.tokio.otpfilter73.hook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.events.LifecycleEvent;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.WebKeys;

@Component(
		immediate = true, 
		property = {
				"key=logout.events.post",
				"com.liferay.portlet.private-session-attributes=false"
		},
		service = LifecycleAction.class
	)

public class PostLogout  implements LifecycleAction {

	@Override
	public void processLifecycleEvent(LifecycleEvent lifecycleEvent) throws ActionException {
				
		System.out.println("LOGOUT POST ACTION" );
		HttpServletRequest request  = lifecycleEvent.getRequest();
		
		HttpServletResponse response = lifecycleEvent.getResponse();
		
		User user = (User) request.getAttribute(WebKeys.USER);
		
		final HttpSession session = request.getSession();

		session.setAttribute("IS_VERIFIED", false);
	}
	
}
