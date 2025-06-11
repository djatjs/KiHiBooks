package kr.kh.kihibooks.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenRequest {
	@JsonProperty("imp_key")
	private String impKey;
	@JsonProperty("imp_secret")
	private String impSecret;
}
