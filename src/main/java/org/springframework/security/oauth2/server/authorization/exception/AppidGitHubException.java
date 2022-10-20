package org.springframework.security.oauth2.server.authorization.exception;

import org.springframework.security.oauth2.core.OAuth2Error;

/**
 * GitHub AppID 异常
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
public class AppidGitHubException extends GitHubException {

	public AppidGitHubException(String errorCode) {
		super(errorCode);
	}

	public AppidGitHubException(OAuth2Error error) {
		super(error);
	}

	public AppidGitHubException(OAuth2Error error, Throwable cause) {
		super(error, cause);
	}

	public AppidGitHubException(OAuth2Error error, String message) {
		super(error, message);
	}

	public AppidGitHubException(OAuth2Error error, String message, Throwable cause) {
		super(error, message, cause);
	}

}
