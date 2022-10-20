package org.springframework.security.oauth2.server.authorization.exception;

import org.springframework.security.oauth2.core.OAuth2Error;

/**
 * GitHub Secret 异常
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
public class SecretGitHubException extends GitHubException {

	public SecretGitHubException(String errorCode) {
		super(errorCode);
	}

	public SecretGitHubException(OAuth2Error error) {
		super(error);
	}

	public SecretGitHubException(OAuth2Error error, Throwable cause) {
		super(error, cause);
	}

	public SecretGitHubException(OAuth2Error error, String message) {
		super(error, message);
	}

	public SecretGitHubException(OAuth2Error error, String message, Throwable cause) {
		super(error, message, cause);
	}

}
