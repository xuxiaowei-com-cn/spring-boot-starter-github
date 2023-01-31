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

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 通过 code 换取网页授权 access_token 返回值
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Data
public class GitHubTokenResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * token 类型
	 */
	@JsonProperty("token_type")
	private String tokenType;

	/**
	 * 授权Token
	 */
	@JsonProperty("access_token")
	private String accessToken;

	/**
	 * access_token接口调用凭证超时时间，单位（秒）
	 */
	@JsonProperty("expires_in")
	private Integer expiresIn;

	/**
	 * 用户刷新access_token
	 */
	@JsonProperty("refresh_token")
	private String refreshToken;

	/**
	 * 刷新Token过期时间
	 */
	@JsonProperty("refresh_token_expires_in")
	private Long refreshTokenExpiresIn;

	/**
	 * 授权范围
	 */
	private String scope;

	/**
	 * 错误码
	 */
	private String error;

	/**
	 * 错误信息
	 */
	@JsonProperty("error_description")
	private String errorDescription;

	/**
	 * 用户信息
	 */
	private UserInfo userInfo;

	/**
	 * 用户信息
	 *
	 * @author xuxiaowei
	 * @since 0.0.1
	 */
	@Data
	public static class UserInfo {

		@JsonProperty("gists_url")
		private String gistsUrl;

		@JsonProperty("repos_url")
		private String reposUrl;

		@JsonProperty("following_url")
		private String followingUrl;

		@JsonProperty("twitter_username")
		private String twitterUsername;

		@JsonProperty("bio")
		private String bio;

		@JsonProperty("created_at")
		private String createdAt;

		@JsonProperty("login")
		private String login;

		@JsonProperty("type")
		private String type;

		@JsonProperty("blog")
		private String blog;

		@JsonProperty("subscriptions_url")
		private String subscriptionsUrl;

		@JsonProperty("updated_at")
		private String updatedAt;

		@JsonProperty("site_admin")
		private boolean siteAdmin;

		@JsonProperty("company")
		private String company;

		@JsonProperty("id")
		private int id;

		@JsonProperty("public_repos")
		private int publicRepos;

		@JsonProperty("gravatar_id")
		private String gravatarId;

		@JsonProperty("email")
		private String email;

		@JsonProperty("organizations_url")
		private String organizationsUrl;

		@JsonProperty("hireable")
		private boolean hireable;

		@JsonProperty("starred_url")
		private String starredUrl;

		@JsonProperty("followers_url")
		private String followersUrl;

		@JsonProperty("public_gists")
		private int publicGists;

		@JsonProperty("url")
		private String url;

		@JsonProperty("received_events_url")
		private String receivedEventsUrl;

		@JsonProperty("followers")
		private int followers;

		@JsonProperty("avatar_url")
		private String avatarUrl;

		@JsonProperty("events_url")
		private String eventsUrl;

		@JsonProperty("html_url")
		private String htmlUrl;

		@JsonProperty("following")
		private int following;

		@JsonProperty("name")
		private String name;

		@JsonProperty("location")
		private String location;

		@JsonProperty("node_id")
		private String nodeId;

	}

}
