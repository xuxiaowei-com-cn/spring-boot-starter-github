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
import java.util.List;

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
	 * 网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
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

		@JsonProperty("can_create_project")
		private boolean canCreateProject;

		@JsonProperty("private_profile")
		private boolean privateProfile;

		@JsonProperty("work_information")
		private Object workInformation;

		@JsonProperty("commit_email")
		private String commitEmail;

		@JsonProperty("bot")
		private boolean bot;

		@JsonProperty("theme_id")
		private int themeId;

		@JsonProperty("created_at")
		private String createdAt;

		@JsonProperty("bio")
		private String bio;

		@JsonProperty("projects_limit")
		private int projectsLimit;

		@JsonProperty("linkedin")
		private String linkedin;

		@JsonProperty("last_activity_on")
		private String lastActivityOn;

		@JsonProperty("can_create_group")
		private boolean canCreateGroup;

		@JsonProperty("skype")
		private String skype;

		@JsonProperty("twitter")
		private String twitter;

		@JsonProperty("identities")
		private List<IdentitiesItem> identities;

		@JsonProperty("local_time")
		private String localTime;

		@JsonProperty("last_sign_in_at")
		private String lastSignInAt;

		@JsonProperty("color_scheme_id")
		private int colorSchemeId;

		@JsonProperty("id")
		private int id;

		@JsonProperty("state")
		private String state;

		@JsonProperty("confirmed_at")
		private String confirmedAt;

		@JsonProperty("job_title")
		private String jobTitle;

		@JsonProperty("email")
		private String email;

		@JsonProperty("current_sign_in_at")
		private String currentSignInAt;

		@JsonProperty("two_factor_enabled")
		private boolean twoFactorEnabled;

		@JsonProperty("shared_runners_minutes_limit")
		private Object sharedRunnersMinutesLimit;

		@JsonProperty("is_followed")
		private boolean isFollowed;

		@JsonProperty("external")
		private boolean external;

		@JsonProperty("followers")
		private int followers;

		@JsonProperty("avatar_url")
		private String avatarUrl;

		@JsonProperty("web_url")
		private String webUrl;

		@JsonProperty("website_url")
		private String websiteUrl;

		@JsonProperty("extra_shared_runners_minutes_limit")
		private Object extraSharedRunnersMinutesLimit;

		@JsonProperty("organization")
		private String organization;

		@JsonProperty("following")
		private int following;

		@JsonProperty("name")
		private String name;

		@JsonProperty("location")
		private String location;

		@JsonProperty("pronouns")
		private String pronouns;

		@JsonProperty("public_email")
		private String publicEmail;

		@JsonProperty("username")
		private String username;

	}

	@Data
	public static class IdentitiesItem {

		@JsonProperty("provider")
		private String provider;

		@JsonProperty("saml_provider_id")
		private Object samlProviderId;

		@JsonProperty("extern_uid")
		private String externUid;

	}

}
