package com.inzisoft.mobileid.provider.jbbank.controller;

import static org.hamcrest.CoreMatchers.notNullValue;
//import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inzisoft.mobileid.config.BeanConfig;
import com.inzisoft.mobileid.provider.jbbank.service.CommonService;
import com.inzisoft.mobileid.sdk.service.MipBizService;
import com.inzisoft.mobileid.sp.domain.transaction.dto.TransactionData;
import com.inzisoft.mobileid.sp.domain.transaction.service.MipTransactionService;
import com.inzisoft.mobileid.sp.provider.config.CustomizeConfig;

@SpringBootTest
@ActiveProfiles("local") // 테스트 수행시 어떤 profile 사용할지 정해줌
@AutoConfigureMockMvc // 테스트 대상이 아닌 @Service나 @Repository가 붙은 객체들도 모두 메모리에 올림( @WebMvcTest : 컨트롤러를 테스트)
public class CommonControllerTest {
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	BeanConfig beanConfig;
	
	@Autowired
	CustomizeConfig customizeConfig;
	
	private final CommonService commonService;
	
	@Autowired
	public CommonControllerTest(CommonService commonService) {
		this.commonService = commonService;
	}
	
	@Test
	public void testExist() {
	}
	

	@Test
	public void bizResultTest() throws Exception{
		Gson gson = new Gson();
		
		Map<String, Object> map = new HashMap<>();
		map.put("trxcode", "20220525037SP00000005");
		String ret = gson.toJson(map);
		System.out.println("ret :["+ ret+"]");
		
		MvcResult result = this.mockMvc.perform(post("/v1/mip/common/bizResult")
							.content(new GsonBuilder().setPrettyPrinting().create().toJson(map))
							.contentType(MediaType.APPLICATION_JSON)
							.characterEncoding("utf-8")
							)
				.andDo(print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn();
				
		// Response Test
		Map<String, Object> resMap = new Gson().fromJson(result.getResponse().getContentAsString(), Map.class);
		System.out.println("resMap : [" + resMap + "]");
		
		// null이 아닐경우 테스트 통과
//		assertThat(resMap.get("trxcode"), notNullValue());
//		assertThat(resMap.get("message"), notNullValue());
		
		// TransactionData Status Test
		//TransactionData transactionData =  mipTransactionService.get(resMap.get("trxcode").toString()).get();
		
//		assertThat(transactionData, notNullValue());
//		assertThat(transactionData.getTransactionCode(), notNullValue());
		
	}
}
