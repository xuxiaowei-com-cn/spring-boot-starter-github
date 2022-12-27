package org.springframework.security.oauth2.server.authorization.properties;

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

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * GitHub 属性配置类
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Data
@Component
@ConfigurationProperties("github")
public class GitHubProperties {

	/**
	 * GitHub 属性配置列表
	 */
	private List<GitHub> list;

	/**
	 * 默认GitHub 的权限
	 */
	private String defaultRole;

	/**
	 * 默认 AppID
	 */
	private String defaultAppid;

	/**
	 * GitHub 属性配置类
	 *
	 * @author xuxiaowei
	 * @since 0.0.1
	 */
	@Data
	public static class GitHub {

		/**
		 * AppID
		 * <p>
		 * 此处是 {@link String} 类型的 clientId，并不是 GitHub 中的 {@link Integer} 类型的 appid
		 */
		private String appid;

		/**
		 * AppSecret
		 */
		private String secret;

		/**
		 * 重定向的网址前缀（程序使用时，会在后面拼接 /{@link #appid}）
		 */
		private String redirectUriPrefix;

		/**
		 * OAuth2 客户ID
		 */
		private String clientId;

		/**
		 * OAuth2 客户秘钥
		 */
		private String clientSecret;

		/**
		 * 获取 Token URL 前缀
		 */
		private String tokenUrlPrefix;

		/**
		 * 授权范围
		 */
		private String scope;

		/**
		 * 登录成功后重定向的URL
		 */
		private String successUrl;

		/**
		 * 登录成功后重定向的URL OAuth2.1 授权 Token Name
		 */
		private String parameterName = "access_token";

	}

}
