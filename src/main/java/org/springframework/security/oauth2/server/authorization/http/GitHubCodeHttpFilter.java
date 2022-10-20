package org.springframework.security.oauth2.server.authorization.http;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.endpoint.*;
import org.springframework.security.oauth2.server.authorization.client.GitHubService;
import org.springframework.security.oauth2.server.authorization.properties.GitHubProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.oauth2.server.authorization.authentication.OAuth2GitHubAuthenticationToken.GITLAB;

/**
 * GitHub 授权码接收服务
 *
 * @author xuxiaowei
 * @since 0.0.1
 * @see OAuth2AccessTokenResponse
 * @see DefaultOAuth2AccessTokenResponseMapConverter
 * @see DefaultMapOAuth2AccessTokenResponseConverter
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
@Component
public class GitHubCodeHttpFilter extends HttpFilter {

	public static final String PREFIX_URL = "/github/code";

	public static final String TOKEN_URL = "/oauth2/token?grant_type={grant_type}&appid={appid}&code={code}&state={state}&client_id={client_id}&client_secret={client_secret}&remote_address={remote_address}&session_id={session_id}&binding={binding}";

	private GitHubProperties gitLabProperties;

	private GitHubService gitLabService;

	/**
	 * GitHub 使用code获取授权凭证URL前缀
	 */
	private String prefixUrl = PREFIX_URL;

	@Autowired
	public void setGitHubProperties(GitHubProperties gitLabProperties) {
		this.gitLabProperties = gitLabProperties;
	}

	@Autowired
	public void setGitHubService(GitHubService gitLabService) {
		this.gitLabService = gitLabService;
	}

	@Override
	protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String requestUri = request.getRequestURI();
		AntPathMatcher antPathMatcher = new AntPathMatcher();
		boolean match = antPathMatcher.match(prefixUrl + "/*", requestUri);
		if (match) {
			log.info("requestUri：{}", requestUri);

			String appid = requestUri.replace(prefixUrl + "/", "");
			String code = request.getParameter(OAuth2ParameterNames.CODE);
			String state = request.getParameter(OAuth2ParameterNames.STATE);
			String grantType = GITLAB.getValue();

			boolean valid = gitLabService.stateValid(request, response, appid, code, state);
			if (!valid) {
				return;
			}

			String binding = gitLabService.getBinding(request, response, appid, code, state);

			GitHubProperties.GitHub gitLab = gitLabService.getGitHubByAppid(appid);

			String clientId = gitLab.getClientId();
			String clientSecret = gitLab.getClientSecret();
			String tokenUrlPrefix = gitLab.getTokenUrlPrefix();
			String scope = gitLab.getScope();

			String remoteHost = request.getRemoteHost();
			HttpSession session = request.getSession(false);

			Map<String, String> uriVariables = new HashMap<>(8);
			uriVariables.put(OAuth2ParameterNames.GRANT_TYPE, grantType);
			uriVariables.put(OAuth2GitHubParameterNames.APPID, appid);
			uriVariables.put(OAuth2ParameterNames.CODE, code);
			uriVariables.put(OAuth2ParameterNames.STATE, state);
			uriVariables.put(OAuth2ParameterNames.SCOPE, scope);
			uriVariables.put(OAuth2ParameterNames.CLIENT_ID, clientId);
			uriVariables.put(OAuth2ParameterNames.CLIENT_SECRET, clientSecret);
			uriVariables.put(OAuth2GitHubParameterNames.REMOTE_ADDRESS, remoteHost);
			uriVariables.put(OAuth2GitHubParameterNames.SESSION_ID, session == null ? "" : session.getId());
			uriVariables.put(OAuth2GitHubParameterNames.BINDING, binding);

			OAuth2AccessTokenResponse oauth2AccessTokenResponse = gitLabService.getOAuth2AccessTokenResponse(request,
					response, tokenUrlPrefix, TOKEN_URL, uriVariables);
			if (oauth2AccessTokenResponse == null) {
				return;
			}

			gitLabService.sendRedirect(request, response, uriVariables, oauth2AccessTokenResponse, gitLab);
			return;
		}

		super.doFilter(request, response, chain);
	}

}
