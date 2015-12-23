/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.http.impl.client;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ProtocolException;
import org.apache.http.ProtocolVersion;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.auth.AuthProtocolState;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthenticationHandler;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.NonRepeatableRequestException;
import org.apache.http.client.RedirectException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.RequestDirector;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.client.methods.AbortableHttpRequest;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.BasicManagedEntity;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.routing.BasicRouteDirector;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRouteDirector;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.conn.ConnectionShutdownException;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;

public class DefaultRequestDirector implements RequestDirector {
	
	private final Log log;

    /** The connection manager. */
    protected final ClientConnectionManager connManager;

    /** The route planner. */
    protected final HttpRoutePlanner routePlanner;

    /** The connection re-use strategy. */
    protected final ConnectionReuseStrategy reuseStrategy;

    /** The keep-alive duration strategy. */
    protected final ConnectionKeepAliveStrategy keepAliveStrategy;

    /** The request executor. */
    protected final HttpRequestExecutor requestExec;

    /** The HTTP protocol processor. */
    protected final HttpProcessor httpProcessor;

    /** The request retry handler. */
    protected final HttpRequestRetryHandler retryHandler;

    /** The redirect handler. */
    @Deprecated
    protected final RedirectHandler redirectHandler;

    /** The redirect strategy. */
    protected final RedirectStrategy redirectStrategy;

    /** The target authentication handler. */
    @Deprecated
    protected final AuthenticationHandler targetAuthHandler;

    /** The target authentication handler. */
    protected final AuthenticationStrategy targetAuthStrategy;

    /** The proxy authentication handler. */
    @Deprecated
    protected final AuthenticationHandler proxyAuthHandler;

    /** The proxy authentication handler. */
    protected final AuthenticationStrategy proxyAuthStrategy;

    /** The user token handler. */
    protected final UserTokenHandler userTokenHandler;

    /** The HTTP parameters. */
    protected final HttpParams params;

    /** The currently allocated connection. */
    protected ManagedClientConnection managedConn;

    protected final AuthState targetAuthState;

    protected final AuthState proxyAuthState;

    private final HttpAuthenticator authenticator;

    private int execCount;

    private int redirectCount;

    private final int maxRedirects;

    private HttpHost virtualHost;

    
	/**
	 * Analyzes a response to check need for a followup.
	 * 
	 * @param roureq
	 *            the request and route.
	 * @param response
	 *            the response to analayze
	 * @param context
	 *            the context used for the current request execution
	 * @return the followup request and route if there is a followup, or
	 *         {@code null} if the response should be returned as is
	 * @throws HttpException
	 *             in case of a problem
	 * @throws IOException
	 *             in case of an IO problem
	 */
	protected RoutedRequest handleResponse(final RoutedRequest roureq,
			final HttpResponse response, final HttpContext context)
			throws HttpException, IOException {
		String something = "";
		String somethingElse = "";
		List<Something> somethingList = null;
		final HttpRoute route = roureq.getRoute();
		final RequestWrapper request = roureq.getRequest();
		final HttpParams params = request.getParams();
		if (HttpClientParams.isAuthenticating(params)) {
			HttpHost target = (HttpHost) context
					.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
			if (target == null) {
				target = route.getTargetHost();
			}
			if (target.getPort() < 0) {
				final Scheme scheme = connManager.getSchemeRegistry()
						.getScheme(target);
				target = new HttpHost(target.getHostName(),
						scheme.getDefaultPort(), target.getSchemeName());
			}
			final boolean targetAuthRequested = this.authenticator
					.isAuthenticationRequested(target, response,
							this.targetAuthStrategy, targetAuthState, context);
			HttpHost proxy = route.getProxyHost();
			if (proxy == null) {
				proxy = route.getTargetHost();
			}
			final boolean proxyAuthRequested = this.authenticator
					.isAuthenticationRequested(proxy, response,
							this.proxyAuthStrategy, proxyAuthState, context);
			if (targetAuthRequested) {
				if (this.authenticator.authenticate(target, response,
						this.targetAuthStrategy, this.targetAuthState, context)) {
					return roureq;
				}
			}
			if (proxyAuthRequested) {
				if (this.authenticator.authenticate(proxy, response,
						this.proxyAuthStrategy, this.proxyAuthState, context)) {
					return roureq;
				}
			}
		}
		if (HttpClientParams.isRedirecting(params)
				&& this.redirectStrategy.isRedirected(request, response,
						context)) {
			if (redirectCount >= maxRedirects) {
				throw new RedirectException("Maximum redirects ("
						+ maxRedirects + ") exceeded");
			}
			redirectCount++;
			virtualHost = null;
			final HttpUriRequest redirect = redirectStrategy.getRedirect(
					request, response, context);
			final HttpRequest orig = request.getOriginal();
			redirect.setHeaders(orig.getAllHeaders());
			final URI uri = redirect.getURI();
			final HttpHost newTarget = URIUtils.extractHost(uri);
			if (newTarget == null) {
				throw new ProtocolException(
						"Redirect URI does not specify a valid host name: "
								+ uri);
			}
			if (!route.getTargetHost().equals(newTarget)) {
				this.log.debug("Resetting target auth state");
				targetAuthState.reset();
				final AuthScheme authScheme = proxyAuthState.getAuthScheme();
				if (authScheme != null && authScheme.isConnectionBased()) {
					this.log.debug("Resetting proxy auth state");
					proxyAuthState.reset();
				}
			}
			final RequestWrapper wrapper = wrapRequest(redirect);
			wrapper.setParams(params);
			final HttpRoute newRoute = determineRoute(newTarget, wrapper,
					context);
			final RoutedRequest newRequest = new RoutedRequest(wrapper,
					newRoute);
			if (this.log.isDebugEnabled()) {
				this.log.debug("Redirecting to '" + uri + "' via " + newRoute);
			}
			return newRequest;
		}
		return null;
	}
}