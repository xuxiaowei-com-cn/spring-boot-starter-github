package org.springframework.security.oauth2.server.authorization.authentication;

/*-
 * #%L
 * spring-boot-starter-github
 * %%
 * Copyright (C) 2022 徐晓伟工作室
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.GitHubService;
import org.springframework.security.oauth2.server.authorization.client.GitHubTokenResponse;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2GitHubConfigurerUtils;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.util.Map;
import java.util.Set;

/**
 * GitHub OAuth2 身份验证提供程序
 *
 * @author xuxiaowei
 * @since 0.0.1
 * @see AnonymousAuthenticationProvider
 * @see JwtClientAssertionAuthenticationProvider
 * @see ClientSecretAuthenticationProvider
 * @see PublicClientAuthenticationProvider
 * @see OAuth2AuthorizationCodeRequestAuthenticationProvider
 * @see OAuth2AuthorizationCodeAuthenticationProvider
 * @see OAuth2RefreshTokenAuthenticationProvider
 * @see OAuth2ClientCredentialsAuthenticationProvider
 * @see OAuth2TokenIntrospectionAuthenticationProvider
 * @see OAuth2TokenRevocationAuthenticationProvider
 * @see OidcUserInfoAuthenticationProvider
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class OAuth2GitHubAuthenticationProvider implements AuthenticationProvider {

	/**
	 * @see OAuth2TokenContext#getAuthorizedScopes()
	 */
	private static final String AUTHORIZED_SCOPE_KEY = OAuth2Authorization.class.getName().concat(".AUTHORIZED_SCOPE");

	/**
	 * @see <a href=
	 * "https://docs.github.com/zh/developers/apps/building-oauth-apps/authorizing-oauth-apps#2-%E7%94%A8%E6%88%B7%E8%A2%AB-github-%E9%87%8D%E5%AE%9A%E5%90%91%E5%9B%9E%E4%BD%A0%E7%9A%84%E7%AB%99%E7%82%B9">2.用户被GitHub重定向回你的站点</a>
	 */
	public static final String ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token?client_id={client_id}&client_secret={client_secret}&code={code}&redirect_uri={redirect_uri}";

	/**
	 * @see <a href=
	 * "https://docs.github.com/zh/developers/apps/building-oauth-apps/authorizing-oauth-apps#3-%E4%BD%BF%E7%94%A8%E8%AE%BF%E9%97%AE%E4%BB%A4%E7%89%8C%E8%AE%BF%E9%97%AE-api">3.使用访问令牌访问API</a>
	 */
	public static final String USERINFO_URL = "https://api.github.com/user";

	private final HttpSecurity builder;

	@Setter
	private OAuth2AuthorizationService authorizationService;

	@Setter
	private OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;

	@Setter
	private GitHubService gitHubService;

	public OAuth2GitHubAuthenticationProvider(HttpSecurity builder) {
		Assert.notNull(builder, "HttpSecurity 不能为空");
		this.builder = builder;
		builder.authenticationProvider(this);
	}

	@SuppressWarnings("AlibabaMethodTooLong")
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		// 初始化变量默认值
		variableDefaults();

		OAuth2GitHubAuthenticationToken grantAuthenticationToken = (OAuth2GitHubAuthenticationToken) authentication;

		String appid = grantAuthenticationToken.getAppid();
		String code = grantAuthenticationToken.getCode();
		String state = grantAuthenticationToken.getState();
		String binding = grantAuthenticationToken.getBinding();

		Map<String, Object> additionalParameters = grantAuthenticationToken.getAdditionalParameters();
		Set<String> requestedScopes = StringUtils.commaDelimitedListToSet(grantAuthenticationToken.getScope());

		OAuth2ClientAuthenticationToken clientPrincipal = OAuth2AuthenticationProviderUtils
			.getAuthenticatedClientElseThrowInvalidClient(grantAuthenticationToken);
		RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

		// 自定义GitHub 用户的IP与SessionId
		String remoteAddress = grantAuthenticationToken.getRemoteAddress();
		String sessionId = grantAuthenticationToken.getSessionId();
		sessionId = "".equals(sessionId) ? null : sessionId;
		WebAuthenticationDetails webAuthenticationDetails = new WebAuthenticationDetails(remoteAddress, sessionId);
		clientPrincipal.setDetails(webAuthenticationDetails);

		if (registeredClient == null) {
			OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR, "注册客户不能为空", null);
			throw new OAuth2AuthenticationException(error);
		}

		Set<String> allowedScopes = registeredClient.getScopes();

		if (requestedScopes.isEmpty()) {
			// 请求中的 scope 为空，允许全部
			requestedScopes = allowedScopes;
		}
		else if (!allowedScopes.containsAll(requestedScopes)) {
			OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_SCOPE,
					"OAuth 2.0 参数: " + OAuth2ParameterNames.SCOPE, null);
			throw new OAuth2AuthenticationException(error);
		}

		GitHubTokenResponse gitHubTokenResponse = gitHubService.getAccessTokenResponse(appid, code, state, binding,
				ACCESS_TOKEN_URL, USERINFO_URL, remoteAddress, sessionId);

		GitHubTokenResponse.UserInfo userInfo = gitHubTokenResponse.getUserInfo();

		int id = userInfo.getId();
		String login = userInfo.getLogin();

		String accessToken = gitHubTokenResponse.getAccessToken();
		String refreshToken = gitHubTokenResponse.getRefreshToken();
		Integer expiresIn = gitHubTokenResponse.getExpiresIn();
		String scope = gitHubTokenResponse.getScope();

		OAuth2Authorization.Builder builder = OAuth2Authorization.withRegisteredClient(registeredClient);
		builder.principalName(id + "");
		builder.authorizationGrantType(OAuth2GitHubAuthenticationToken.GITHUB);

		AbstractAuthenticationToken abstractAuthenticationToken = gitHubService.authenticationToken(clientPrincipal,
				additionalParameters, grantAuthenticationToken.getDetails(), appid, code, id, null, login, accessToken,
				refreshToken, expiresIn, scope);

		builder.attribute(Principal.class.getName(), abstractAuthenticationToken);
		builder.attribute(AUTHORIZED_SCOPE_KEY, requestedScopes);

		OAuth2Authorization authorization = builder.build();

		// @formatter:off
		DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
				.registeredClient(registeredClient)
				.principal(authorization.getAttribute(Principal.class.getName()))
				.authorizationServerContext(AuthorizationServerContextHolder.getContext())
				.authorization(authorization)
				.authorizedScopes(authorization.getAttribute(AUTHORIZED_SCOPE_KEY))
				.authorizationGrantType(OAuth2GitHubAuthenticationToken.GITHUB)
				.authorizationGrant(grantAuthenticationToken);
		// @formatter:on

		OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.from(authorization);

		// ----- Access token -----
		OAuth2TokenContext tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
		OAuth2Token generatedAccessToken = tokenGenerator.generate(tokenContext);
		if (generatedAccessToken == null) {
			OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
					"The token generator failed to generate the access token.", null);
			throw new OAuth2AuthenticationException(error);
		}
		OAuth2AccessToken oauth2AccessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
				generatedAccessToken.getTokenValue(), generatedAccessToken.getIssuedAt(),
				generatedAccessToken.getExpiresAt(), tokenContext.getAuthorizedScopes());
		if (generatedAccessToken instanceof ClaimAccessor) {
			authorizationBuilder.token(oauth2AccessToken,
					(metadata) -> metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME,
							((ClaimAccessor) generatedAccessToken).getClaims()));
		}
		else {
			authorizationBuilder.accessToken(oauth2AccessToken);
		}

		// ----- Refresh token -----
		OAuth2RefreshToken oauth2RefreshToken = null;
		if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN) &&
		// Do not issue refresh token to public client
				!clientPrincipal.getClientAuthenticationMethod().equals(ClientAuthenticationMethod.NONE)) {

			tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
			OAuth2Token generatedRefreshToken = tokenGenerator.generate(tokenContext);
			if (!(generatedRefreshToken instanceof OAuth2RefreshToken)) {
				OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR, "令牌生成器无法生成刷新令牌。", null);
				throw new OAuth2AuthenticationException(error);
			}
			oauth2RefreshToken = (OAuth2RefreshToken) generatedRefreshToken;
			authorizationBuilder.refreshToken(oauth2RefreshToken);
		}

		authorization = authorizationBuilder.build();

		authorizationService.save(authorization);

		return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, oauth2AccessToken,
				oauth2RefreshToken, additionalParameters);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return OAuth2GitHubAuthenticationToken.class.isAssignableFrom(authentication);
	}

	/**
	 * 初始化变量默认值
	 */
	private void variableDefaults() {
		if (authorizationService == null) {
			authorizationService = OAuth2GitHubConfigurerUtils.getAuthorizationService(builder);
		}

		if (tokenGenerator == null) {
			tokenGenerator = OAuth2GitHubConfigurerUtils.getTokenGenerator(builder);
		}

		if (gitHubService == null) {
			gitHubService = OAuth2GitHubConfigurerUtils.getGitHubService(builder);
		}
	}

}
