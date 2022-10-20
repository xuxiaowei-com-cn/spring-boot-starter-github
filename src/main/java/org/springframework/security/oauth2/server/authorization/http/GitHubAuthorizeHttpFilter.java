package org.springframework.security.oauth2.server.authorization.http;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.endpoint.OAuth2GitHubParameterNames;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.client.GitHubService;
import org.springframework.security.oauth2.server.authorization.properties.GitHubProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * GitHub 跳转到GitHub授权页面
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
@Component
public class GitHubAuthorizeHttpFilter extends HttpFilter {

	public static final String PREFIX_URL = "/github/authorize";

	public static final String AUTHORIZE_URL = "https://github.com/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s";

	private GitHubProperties gitLabProperties;

	private GitHubService gitLabService;

	/**
	 * GitHub 授权前缀
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

			String redirectUri = gitLabService.getRedirectUriByAppid(appid);

			String binding = request.getParameter(OAuth2GitHubParameterNames.BINDING);
			String scope = request.getParameter(OAuth2ParameterNames.SCOPE);

			String state = gitLabService.stateGenerate(request, response, appid);
			gitLabService.storeBinding(request, response, appid, state, binding);
			gitLabService.storeUsers(request, response, appid, state, binding);

			String url = String.format(AUTHORIZE_URL, appid, redirectUri, scope, state);

			log.info("redirectUrl：{}", url);

			response.sendRedirect(url);
			return;
		}

		super.doFilter(request, response, chain);
	}

}
