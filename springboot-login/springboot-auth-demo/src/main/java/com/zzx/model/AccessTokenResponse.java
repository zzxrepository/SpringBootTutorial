package com.zzx.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 * Token令牌 - 响应参数
 *
 * @author supanpan
 * @date 2023/10/25
 */
@Data
public class AccessTokenResponse {
 
    @JsonProperty("access_token")
    private String accessToken;
}