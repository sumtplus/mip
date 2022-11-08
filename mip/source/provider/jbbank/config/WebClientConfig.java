package com.inzisoft.mobileid.provider.jbbank.config;


import javax.net.ssl.SSLException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.inzisoft.mobileid.provider.jbbank.utils.ThrowingConsumer;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.SslProvider.SslContextSpec;

@Configuration
@Slf4j
public class WebClientConfig {
	
	@Bean
	public WebClient webClient() {
//		ExchangeStrategies exchangeStrategies = ExchangeStrategies
//										 			.builder()
//		// clientCodecConfigurer.defaultCodecs 기본으로 등록된 HTTP 메시지 판독기 및 작성기를 사용자지정하거나 교체
//		// in-memory buffer 값이 256KB이므로 값을 늘려줌								 			
//													.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024*1024*50))
//													.build();
//		
//		// HTTP 메세지 reader/writer 커스텀
//		// debug레벨일 때 form Data와 Trace 레벨일 때 header 정보는 민감한 정보를 포함하므로 기본설정에서는 확인불가 -> 상세히 확인하기 위해 변경
//		exchangeStrategies
//			.messageReaders().stream()
//		// LoggingCodecSupport : 로거를 사용하고 잠재적으로 민감한 요청 데이터를 표시하는 Encoder, Decoder, HttpMessageReader또는 의 기본 클래스입니다 	
//			.filter(LoggingCodecSupport.class::isInstance)
//		// 양식 데이터를 DEBUG 수준에서 기록하고 헤더를 TRACE 수준에서 기록할지 여부, 기본값 false	
//			.forEach(writer -> ((LoggingCodecSupport)writer).setEnableLoggingRequestDetails(true));
		
		// httpClient TimeOut 설정
		// WebClient.builder().clientConnector()를 통해 Reactor Netty의 httpClient를 직접설정
		
		// http 클라이언트 라이브러리(ex) SSL등) 세팅
		return WebClient.builder()
				.clientConnector(
		// 옵션이 추가된 새로운 HttpClient를 설정				
					new ReactorClientHttpConnector(
							HttpClient
								.create()
								// SSL구성 사용자지정을 적용
								.secure(
//									// stream 처리에서 Exception 처리
//									// https 인증서를 검증하지 않고 바로 접속	
//									ThrowingConsumer.unchecked(
//										sslContextSpec -> sslContextSpec.sslContext(
//												// trustMananger : 인증서를 확인하는 신뢰할수 잇는 관리자
//												// InsecureTrustManagerFactory : 아무런확인없이 모든 인증서를 신뢰
//												SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build()
//												)
//											)
										//webclient가 https 통신이 되도록 설정
//										t -> {
//											try {
//												t.sslContext(SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build());
//											} catch (SSLException e) {
//												log.error("SSLException\n", e);
//											}
//										}
										)
										//tcp연결시 타임아웃설정
										.tcpConfiguration(
											// 주어진 옵션키를 업데이트
											client -> client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 120_000)
															// connection 연결된 후 호출되는 콜백을 설정하거나 추가
															.doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(180))
																						.addHandlerLast(new WriteTimeoutHandler(180))
										)
								)
						)
					)
//					// Request / Response 데이터에 대한 조작/추가 작업
//					// Request / Response header를 출력
//					.exchangeStrategies(exchangeStrategies)
//					.filter(ExchangeFilterFunction.ofRequestProcessor(
//							clientRequest -> {
//								log.debug("Request: {} {}", clientRequest.method(), clientRequest.url());
//								clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.debug("{} : {}", name, value)));
//								// 여러스트림을 하나의 결과를 모아줄 때?
//								return Mono.just(clientRequest);
//							}
//					))
//					.filter(ExchangeFilterFunction.ofResponseProcessor(
//						clientResponse -> {
//								clientResponse.headers().asHttpHeaders().forEach((name, values) -> values.forEach(value -> log.debug("{} : {}", name, value)));
//								return Mono.just(clientResponse);
//							}
//					))
					//.defaultHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.3")
					.build();
		
	}
}

