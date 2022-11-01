package com.inzisoft.mobileid.sp.domain.mip.config;

import com.inzisoft.mobileid.sdk.config.SdkConfig;
import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@PropertySource(ignoreResourceNotFound = true,
		value = {"classpath:config/sdk.properties",
				"file:config/sdk.properties" })
@Component
@Getter
@ToString
public class SdkConfigSpringProperties implements SdkConfig {
	@Value("${vp.localstorage}")
	private String vpLocalStoragePath;
	@Value("${profile.submit.time.limit}")
	private String profileSubmitTimeLimit;
	@Value("${server.id}")
	private String serverId;
}
