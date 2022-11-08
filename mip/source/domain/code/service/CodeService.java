package com.inzisoft.mobileid.sp.domain.code.service;

import com.inzisoft.mobileid.sp.domain.code.dto.AddCodeParam;
import com.inzisoft.mobileid.sp.domain.code.dto.CodeData;
import com.inzisoft.mobileid.sp.domain.code.dto.FindCodeParam;
import com.inzisoft.mobileid.sp.domain.code.dto.UpdateCodeParam;

import java.util.List;

public interface CodeService {
	CodeData addCode(AddCodeParam addCodeParam);
	CodeData getCode(String groupCode, String code);
	CodeData updateCode(String groupCode, String code, UpdateCodeParam updateCodeParam);
	CodeData deleteCode(String groupCode, String code);

	List<String> getGroupCode();
	List<CodeData> findCode(String groupCode, FindCodeParam param);
}
