package org.springframework.security.oauth2.server.authorization.web.authentication;

/**
 * GitHub OAuth 2.0 协议端点的实用方法
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class OAuth2GitHubEndpointUtils {

	/**
	 * GitHub
	 */
	public static final String AUTH_CODE2SESSION_URI = "https://docs.github.com/cn/developers/apps/building-oauth-apps/authorizing-oauth-apps";

	/**
	 * 错误代码
	 */
	public static final String ERROR_CODE = "C10000";

	/**
	 * 无效错误代码
	 */
	public static final String INVALID_ERROR_CODE = "C20000";

}
