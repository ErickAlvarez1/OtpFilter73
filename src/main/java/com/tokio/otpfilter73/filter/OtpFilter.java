package com.tokio.otpfilter73.filter;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.tokio.otp.model.TwoFactor;
import com.tokio.otp.service.DebugEMailsLocalService;
import com.tokio.otp.service.TwoFactorConfigsLocalService;
import com.tokio.otp.service.TwoFactorLocalService;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
		 immediate = true, 
		 property = {
		 "servlet-context-name=", 
		 "servlet-filter-name=Custom Filter",
		 "url-pattern=/*"
		 }, 
		 service = Filter.class
		 )
public class OtpFilter extends BaseFilter  {
	
	@Reference
	private TwoFactorConfigsLocalService _TwoFactorConfigsLocalService;
	
	@Reference
	private DebugEMailsLocalService _DebugEMailsLocalService;
	
	@Reference
	private TwoFactorLocalService _TwoFactorLocalService;

	 @Override
	 protected void processFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	 throws Exception {
		 String currentUrl = PortalUtil.getCurrentCompleteURL(request);
		 
		 boolean doChain = true;
		 System.out.println(currentUrl);
		 if (
			     (!currentUrl.contains("agentes.tokiomarine.com.mx/web/") 
			      && !currentUrl.contains("agentes.tokiomarine.com.mx/c/")
			      && !currentUrl.contains("agentes.tokiomarine.com.mx/combo/")
			      && !currentUrl.contains("agentes.tokiomarine.com.mx/image/")
			      && !currentUrl.contains("agentes.tokiomarine.com.mx/documents/")
			      && !currentUrl.contains("agentes.tokiomarine.com.mx/o/")) 
				 && (currentUrl.contains("agentes.tokiomarine.com.mx") || currentUrl.contains("group/"))
			) {
			 System.out.println("BASE FILTER");
			 System.out.println(currentUrl);
			 //System.out.println(request.getPathInfo());
			 final HttpSession session = request.getSession();
			 
			 String userId = request.getRemoteUser();
			 //User user = (User) session.getAttribute("infoUser");
			 Boolean isVerified = (Boolean) session.getAttribute("IS_VERIFIED");
			 
			 String url = (String) session.getAttribute("redirect_url");
			 
			 System.out.println(isVerified+" -- "+userId);
			 
			 System.out.println(((Validator.isNotNull(isVerified) && !isVerified) && Validator.isNotNull(userId)) );
			 
			 try {
				 if ( Validator.isNotNull(userId)) {
					 User user = (User) request.getAttribute(WebKeys.USER);
					 System.out.println(user.getUserId()+"--"+ user.getCompanyId()+"--"+ user.getGroupId());
					
					 boolean debugging = _TwoFactorConfigsLocalService.isActiveConfig("debuging", user.getCompanyId(), 0 );
					 boolean otpatived = _TwoFactorConfigsLocalService.isActiveConfig("otpatived", user.getCompanyId(), 0 );
					 boolean isValidMail = _DebugEMailsLocalService.existEMail(user.getEmailAddress());
					 boolean canValidate = true;
					 System.out.println("debugging"+debugging);
					 System.out.println(user.getGroupId()+"-"+user.getCompanyId());
					 System.out.println("otpatived:"+otpatived);
					 System.out.println("isValid:"+isValidMail);
					 
					if (debugging) {
						if (!isValidMail){
							canValidate = false;
							session.setAttribute("IS_VERIFIED", true);
						}
					}
					 
					 if ( otpatived ){
						 if ( canValidate ) {
							 TwoFactor token = _TwoFactorLocalService.getTokenByUsuario(user.getUserId(), user.getCompanyId(), 0);
							 System.out.println(token);
							 System.out.println("ACTIVE: "+(Validator.isNotNull(token) && !token.isDisabled()));
							 
							 if (!token.isDisabled()) {
								 if ((Validator.isNotNull(token))) {
									 System.out.println("VALIDATE:"+isVerified);
									 if ((Validator.isNull(isVerified) || !isVerified) ) {
										 System.out.println("REDIRECT");
										 doChain = false;
										 response.sendRedirect("/web/portal-agentes/verify-otp");
									 }
								 }
							 } else {
								 session.setAttribute("IS_VERIFIED", true);
							 }
						 }
					 }
				 }
			 } catch (Exception e) {
				 e.printStackTrace();
			 }
		 }
		 
		 if (doChain) {
			 filterChain.doFilter( request, response);
		 }
	 }
	 @Override
	 protected Log getLog() {
		 return _log;
	 }

	 private static final Log _log = LogFactoryUtil.getLog(OtpFilter.class);

	 
	 
}