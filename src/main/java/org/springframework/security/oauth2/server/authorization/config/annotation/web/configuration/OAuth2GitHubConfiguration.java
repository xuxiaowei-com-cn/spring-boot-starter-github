package org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.client.GitHubService;
import org.springframework.security.oauth2.server.authorization.client.InMemoryGitHubService;
import org.springframework.security.oauth2.server.authorization.properties.GitHubProperties;

/**
 * GitHub 配置
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Configuration
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class OAuth2GitHubConfiguration {

	private GitHubProperties gitLabProperties;

	@Autowired
	public void setGitHubProperties(GitHubProperties gitLabProperties) {
		this.gitLabProperties = gitLabProperties;
	}

	@Bean
	@ConditionalOnMissingBean
	public GitHubService gitLabService() {
		return new InMemoryGitHubService(gitLabProperties);
	}

}
