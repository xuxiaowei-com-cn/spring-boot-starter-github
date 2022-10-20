package org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.GitHubService;
import org.springframework.security.oauth2.server.authorization.client.InMemoryGitHubService;
import org.springframework.security.oauth2.server.authorization.properties.GitHubProperties;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

/**
 * GitHub OAuth 2.0 配置器的实用方法。
 *
 * @author xuxiaowei
 * @since 0.0.1
 * @see OAuth2ConfigurerUtils
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class OAuth2GitHubConfigurerUtils {

	public static OAuth2AuthorizationService getAuthorizationService(HttpSecurity httpSecurity) {
		return OAuth2ConfigurerUtils.getAuthorizationService(httpSecurity);
	}

	public static OAuth2TokenGenerator<? extends OAuth2Token> getTokenGenerator(HttpSecurity httpSecurity) {
		return OAuth2ConfigurerUtils.getTokenGenerator(httpSecurity);
	}

	public static GitHubService getGitHubService(HttpSecurity httpSecurity) {
		GitHubService gitLabService = httpSecurity.getSharedObject(GitHubService.class);
		if (gitLabService == null) {
			gitLabService = OAuth2ConfigurerUtils.getOptionalBean(httpSecurity, GitHubService.class);
			if (gitLabService == null) {
				GitHubProperties gitLabProperties = OAuth2ConfigurerUtils.getOptionalBean(httpSecurity,
						GitHubProperties.class);
				gitLabService = new InMemoryGitHubService(gitLabProperties);
			}
		}
		return gitLabService;
	}

}
