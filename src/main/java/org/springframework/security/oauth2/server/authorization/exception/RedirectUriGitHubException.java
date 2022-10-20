package org.springframework.security.oauth2.server.authorization.exception;

import org.springframework.security.oauth2.core.OAuth2Error;

/**
 * GitHub redirectUri 异常
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
public class RedirectUriGitHubException extends GitHubException {

	public RedirectUriGitHubException(String errorCode) {
		super(errorCode);
	}

	public RedirectUriGitHubException(OAuth2Error error) {
		super(error);
	}

	public RedirectUriGitHubException(OAuth2Error error, Throwable cause) {
		super(error, cause);
	}

	public RedirectUriGitHubException(OAuth2Error error, String message) {
		super(error, message);
	}

	public RedirectUriGitHubException(OAuth2Error error, String message, Throwable cause) {
		super(error, message, cause);
	}

}
