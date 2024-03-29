package org.springframework.security.oauth2.server.authorization.client;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.GitHubAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2TokenEndpointConfigurer;
import org.springframework.security.oauth2.server.authorization.exception.AppidGitHubException;
import org.springframework.security.oauth2.server.authorization.exception.RedirectGitHubException;
import org.springframework.security.oauth2.server.authorization.exception.RedirectUriGitHubException;
import org.springframework.security.oauth2.server.authorization.properties.GitHubProperties;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2GitHubEndpointUtils;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * GitHub 账户服务接口 基于内存的实现
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
public class InMemoryGitHubService implements GitHubService {

	private final GitHubProperties gitHubProperties;

	public InMemoryGitHubService(GitHubProperties gitHubProperties) {
		this.gitHubProperties = gitHubProperties;
	}

	/**
	 * 根据 appid 获取重定向的地址
	 * @param appid 开放平台 ID
	 * @return 返回重定向的地址
	 * @throws OAuth2AuthenticationException OAuth 2.1 可处理的异常，可使用
	 * {@link OAuth2AuthorizationServerConfigurer#tokenEndpoint(Customizer)} 中的
	 * {@link OAuth2TokenEndpointConfigurer#errorResponseHandler(AuthenticationFailureHandler)}
	 * 拦截处理此异常
	 */
	@Override
	public String getRedirectUriByAppid(String appid) throws OAuth2AuthenticationException {
		GitHubProperties.GitHub gitHub = getGitHubByAppid(appid);
		String redirectUriPrefix = gitHub.getRedirectUriPrefix();

		if (StringUtils.hasText(redirectUriPrefix)) {
			return UriUtils.encode(redirectUriPrefix + "/" + appid, StandardCharsets.UTF_8);
		}
		else {
			OAuth2Error error = new OAuth2Error(OAuth2GitHubEndpointUtils.ERROR_CODE, "重定向地址前缀不能为空", null);
			throw new RedirectUriGitHubException(error);
		}
	}

	/**
	 * 生成状态码
	 * @param request 请求
	 * @param response 响应
	 * @param appid 开放平台 ID
	 * @return 返回生成的授权码
	 */
	@Override
	public String stateGenerate(HttpServletRequest request, HttpServletResponse response, String appid) {
		return UUID.randomUUID().toString();
	}

	/**
	 * 储存绑定参数
	 * @param request 请求
	 * @param response 响应
	 * @param appid 开放平台 ID
	 * @param state 状态码
	 * @param binding 绑定参数
	 */
	@Override
	public void storeBinding(HttpServletRequest request, HttpServletResponse response, String appid, String state,
			String binding) {

	}

	/**
	 * 储存操作用户
	 * @param request 请求
	 * @param response 响应
	 * @param appid 开放平台 ID
	 * @param state 状态码
	 * @param binding 绑定参数
	 */
	@Override
	public void storeUsers(HttpServletRequest request, HttpServletResponse response, String appid, String state,
			String binding) {

	}

	/**
	 * 状态码验证（返回 {@link Boolean#FALSE} 时，将终止后面需要执行的代码）
	 * @param request 请求
	 * @param response 响应
	 * @param appid 开放平台 ID
	 * @param code 授权码
	 * @param state 状态码
	 * @return 返回 状态码验证结果
	 */
	@Override
	public boolean stateValid(HttpServletRequest request, HttpServletResponse response, String appid, String code,
			String state) {
		return true;
	}

	/**
	 * 获取 绑定参数
	 * @param request 请求
	 * @param response 响应
	 * @param appid 开放平台 ID
	 * @param code 授权码
	 * @param state 状态码
	 * @return 返回 绑定参数
	 */
	@Override
	public String getBinding(HttpServletRequest request, HttpServletResponse response, String appid, String code,
			String state) {
		return null;
	}

	/**
	 * 根据 appid 获取 GitHub 属性配置
	 * @param appid 公众号ID
	 * @return 返回 GitHub 属性配置
	 * @throws OAuth2AuthenticationException OAuth 2.1 可处理的异常，可使用
	 * {@link OAuth2AuthorizationServerConfigurer#tokenEndpoint(Customizer)} 中的
	 * {@link OAuth2TokenEndpointConfigurer#errorResponseHandler(AuthenticationFailureHandler)}
	 * 拦截处理此异常
	 */
	@Override
	public GitHubProperties.GitHub getGitHubByAppid(String appid) throws OAuth2AuthenticationException {
		List<GitHubProperties.GitHub> list = gitHubProperties.getList();
		if (list == null) {
			OAuth2Error error = new OAuth2Error(OAuth2GitHubEndpointUtils.ERROR_CODE, "appid 未配置", null);
			throw new AppidGitHubException(error);
		}

		for (GitHubProperties.GitHub gitHub : list) {
			if (appid.equals(gitHub.getAppid())) {
				return gitHub;
			}
		}
		OAuth2Error error = new OAuth2Error(OAuth2GitHubEndpointUtils.ERROR_CODE, "未匹配到 appid", null);
		throw new AppidGitHubException(error);
	}

	/**
	 * 获取 OAuth 2.1 授权 Token（如果不想执行此方法后面的内容，可返回 null）
	 * @param request 请求
	 * @param response 响应
	 * @param clientId 客户ID
	 * @param clientSecret 客户凭证
	 * @param tokenUrlPrefix 获取 Token URL 前缀
	 * @param tokenUrl Token URL
	 * @param uriVariables 参数
	 * @return 返回 OAuth 2.1 授权 Token
	 * @throws OAuth2AuthenticationException OAuth 2.1 可处理的异常，可使用
	 * {@link OAuth2AuthorizationServerConfigurer#tokenEndpoint(Customizer)} 中的
	 * {@link OAuth2TokenEndpointConfigurer#errorResponseHandler(AuthenticationFailureHandler)}
	 * 拦截处理此异常
	 */
	@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
	@Override
	public OAuth2AccessTokenResponse getOAuth2AccessTokenResponse(HttpServletRequest request,
			HttpServletResponse response, String clientId, String clientSecret, String tokenUrlPrefix, String tokenUrl,
			Map<String, String> uriVariables) throws OAuth2AuthenticationException {

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>(8);
		multiValueMap.put(OAuth2ParameterNames.CLIENT_ID, Collections.singletonList(clientId));
		multiValueMap.put(OAuth2ParameterNames.CLIENT_SECRET, Collections.singletonList(clientSecret));

		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(multiValueMap, httpHeaders);
		RestTemplate restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
		messageConverters.add(5, new OAuth2AccessTokenResponseHttpMessageConverter());

		return restTemplate.postForObject(tokenUrlPrefix + tokenUrl, httpEntity, OAuth2AccessTokenResponse.class,
				uriVariables);
	}

	/**
	 * 根据 AppID、code、accessTokenUrl 获取Token
	 * @param appid AppID
	 * @param code 授权码
	 * @param state 状态码
	 * @param binding 是否绑定，需要使用者自己去拓展
	 * @param accessTokenUrl 通过 code 换取网页授权 access_token 的 URL
	 * @param userinfoUrl 通过 access_token 获取用户个人信息
	 * @param remoteAddress 用户IP
	 * @param sessionId SessionID
	 * @return 返回 GitHub授权结果
	 * @throws OAuth2AuthenticationException OAuth 2.1 可处理的异常，可使用
	 * {@link OAuth2AuthorizationServerConfigurer#tokenEndpoint(Customizer)} 中的
	 * {@link OAuth2TokenEndpointConfigurer#errorResponseHandler(AuthenticationFailureHandler)}
	 * 拦截处理此异常
	 */
	@Override
	public GitHubTokenResponse getAccessTokenResponse(String appid, String code, String state, String binding,
			String accessTokenUrl, String userinfoUrl, String remoteAddress, String sessionId)
			throws OAuth2AuthenticationException {
		Map<String, String> uriVariables = new HashMap<>(8);
		uriVariables.put(OAuth2ParameterNames.CLIENT_ID, appid);

		GitHubProperties.GitHub gitHub = getGitHubByAppid(appid);
		String secret = gitHub.getSecret();
		String redirectUri = gitHub.getRedirectUriPrefix() + "/" + appid;

		uriVariables.put(OAuth2ParameterNames.CLIENT_SECRET, secret);
		uriVariables.put(OAuth2ParameterNames.CODE, code);
		uriVariables.put(OAuth2ParameterNames.REDIRECT_URI, redirectUri);

		RestTemplate restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
		messageConverters.set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));

		// 设置代理
		// @formatter:off
		// SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
		// simpleClientHttpRequestFactory.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 12333)));
		// restTemplate.setRequestFactory(simpleClientHttpRequestFactory);
		// @formatter:on

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);

		String forObject = restTemplate.postForObject(accessTokenUrl, httpEntity, String.class, uriVariables);

		GitHubTokenResponse accessTokenResponse;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			accessTokenResponse = objectMapper.readValue(forObject, GitHubTokenResponse.class);
		}
		catch (JsonProcessingException e) {
			OAuth2Error error = new OAuth2Error(OAuth2GitHubEndpointUtils.ERROR_CODE,
					"使用 GitHub  授权code：" + code + " 获取Token异常", OAuth2GitHubEndpointUtils.AUTH_CODE2SESSION_URI);
			throw new OAuth2AuthenticationException(error, e);
		}

		String accessToken = accessTokenResponse.getAccessToken();
		if (accessToken == null) {
			OAuth2Error error = new OAuth2Error(accessTokenResponse.getError(),
					accessTokenResponse.getErrorDescription(), OAuth2GitHubEndpointUtils.AUTH_CODE2SESSION_URI);
			throw new OAuth2AuthenticationException(error);
		}

		httpHeaders.setBearerAuth(accessToken);

		try {
			// HTTP GET 支持设置 Header
			RequestCallback requestCallback = restTemplate.httpEntityCallback(httpEntity,
					GitHubTokenResponse.UserInfo.class);
			HttpMessageConverterExtractor<GitHubTokenResponse.UserInfo> responseExtractor = new HttpMessageConverterExtractor<>(
					GitHubTokenResponse.UserInfo.class, restTemplate.getMessageConverters());
			GitHubTokenResponse.UserInfo userInfo = restTemplate.execute(userinfoUrl, HttpMethod.GET, requestCallback,
					responseExtractor);

			accessTokenResponse.setUserInfo(userInfo);
		}
		catch (Exception e) {
			OAuth2Error error = new OAuth2Error(OAuth2GitHubEndpointUtils.ERROR_CODE, "使用 GitHub  获取用户个人信息异常：",
					OAuth2GitHubEndpointUtils.AUTH_CODE2SESSION_URI);
			throw new OAuth2AuthenticationException(error, e);
		}

		return accessTokenResponse;
	}

	/**
	 * 构建 GitHub 认证信息
	 * @param clientPrincipal 经过身份验证的客户端主体
	 * @param additionalParameters 附加参数
	 * @param details 登录信息
	 * @param appid AppID
	 * @param code 授权码
	 * @param id 用户唯一标识
	 * @param credentials 证书
	 * @param login GitHub登录用户名
	 * @param accessToken 授权凭证
	 * @param refreshToken 刷新凭证
	 * @param expiresIn 过期时间
	 * @param scope 授权范围
	 * @return 返回 认证信息
	 * @throws OAuth2AuthenticationException OAuth 2.1 可处理的异常，可使用
	 * {@link OAuth2AuthorizationServerConfigurer#tokenEndpoint(Customizer)} 中的
	 * {@link OAuth2TokenEndpointConfigurer#errorResponseHandler(AuthenticationFailureHandler)}
	 * 拦截处理此异常
	 */
	@Override
	public AbstractAuthenticationToken authenticationToken(Authentication clientPrincipal,
			Map<String, Object> additionalParameters, Object details, String appid, String code, int id,
			Object credentials, String login, String accessToken, String refreshToken, Integer expiresIn, String scope)
			throws OAuth2AuthenticationException {
		List<GrantedAuthority> authorities = new ArrayList<>();
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(gitHubProperties.getDefaultRole());
		authorities.add(authority);
		User user = new User(id + "", accessToken, authorities);

		UsernamePasswordAuthenticationToken principal = UsernamePasswordAuthenticationToken.authenticated(user, null,
				user.getAuthorities());

		GitHubAuthenticationToken authenticationToken = new GitHubAuthenticationToken(authorities, clientPrincipal,
				principal, user, additionalParameters, details, appid, code, id);

		authenticationToken.setCredentials(credentials);
		authenticationToken.setLogin(login);

		return authenticationToken;
	}

	/**
	 * 授权成功重定向方法
	 * @param request 请求
	 * @param response 响应
	 * @param uriVariables 参数
	 * @param oauth2AccessTokenResponse OAuth2.1 授权 Token
	 * @param gitHub GitHub 配置
	 * @throws OAuth2AuthenticationException OAuth 2.1 可处理的异常，可使用
	 * {@link OAuth2AuthorizationServerConfigurer#tokenEndpoint(Customizer)} 中的
	 * {@link OAuth2TokenEndpointConfigurer#errorResponseHandler(AuthenticationFailureHandler)}
	 * 拦截处理此异常
	 */
	@Override
	public void sendRedirect(HttpServletRequest request, HttpServletResponse response, Map<String, String> uriVariables,
			OAuth2AccessTokenResponse oauth2AccessTokenResponse, GitHubProperties.GitHub gitHub)
			throws OAuth2AuthenticationException {

		OAuth2AccessToken accessToken = oauth2AccessTokenResponse.getAccessToken();

		try {
			response.sendRedirect(
					gitHub.getSuccessUrl() + "?" + gitHub.getParameterName() + "=" + accessToken.getTokenValue());
		}
		catch (IOException e) {
			OAuth2Error error = new OAuth2Error(OAuth2GitHubEndpointUtils.ERROR_CODE, "GitHub 重定向异常", null);
			throw new RedirectGitHubException(error, e);
		}

	}

}
