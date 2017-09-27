package com.application.filter;

import org.springframework.security.web.PortResolver;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by qws on 2017/9/27/027.
 */
@Component
public class ReqCache  extends HttpSessionRequestCache {
    public ReqCache() {
        super();
    }

    @Override
    public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
        super.saveRequest(request, response);
    }

    @Override
    public SavedRequest getRequest(HttpServletRequest currentRequest, HttpServletResponse response) {
        return super.getRequest(currentRequest, response);
    }

    @Override
    public void removeRequest(HttpServletRequest currentRequest, HttpServletResponse response) {
        super.removeRequest(currentRequest, response);
    }

    @Override
    public HttpServletRequest getMatchingRequest(HttpServletRequest request, HttpServletResponse response) {
        return super.getMatchingRequest(request, response);
    }

    @Override
    public void setRequestMatcher(RequestMatcher requestMatcher) {
        super.setRequestMatcher(requestMatcher);
    }

    @Override
    public void setCreateSessionAllowed(boolean createSessionAllowed) {
        super.setCreateSessionAllowed(createSessionAllowed);
    }

    @Override
    public void setPortResolver(PortResolver portResolver) {
        super.setPortResolver(portResolver);
    }

    @Override
    public void setSessionAttrName(String sessionAttrName) {
        super.setSessionAttrName(sessionAttrName);
    }
}
