package com.inzisoft.mobileid.sp.domain.code.service;

import com.inzisoft.mobileid.common.exception.CommonError;
import com.inzisoft.mobileid.common.exception.CommonException;
import com.inzisoft.mobileid.common.exception.MipBizError;
import com.inzisoft.mobileid.common.util.CommonUtil;
import com.inzisoft.mobileid.sp.common.model.ModifyEmployee;
import com.inzisoft.mobileid.sp.domain.code.dto.AddCodeParam;
import com.inzisoft.mobileid.sp.domain.code.dto.CodeData;
import com.inzisoft.mobileid.sp.domain.code.dto.FindCodeParam;
import com.inzisoft.mobileid.sp.domain.code.dto.UpdateCodeParam;
import com.inzisoft.mobileid.sp.domain.code.repository.CodeRepository;
import com.inzisoft.mobileid.sp.domain.code.repository.MipCode;
import com.inzisoft.mobileid.sp.domain.code.repository.MipCodeId;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
public class CodeServiceImpl implements CodeService {
	private CodeRepository codeRepository;

	public CodeServiceImpl(CodeRepository CodeRepository) {
		this.codeRepository = CodeRepository;
	}

	@Override
	@Transactional
	public CodeData addCode(AddCodeParam param) {
		if (codeRepository.findById(MipCodeId.create(param.getGroupCode(), param.getCode())).isPresent()) {
			throw new CommonException(MipBizError.RESOURCE_ERROR,
					String.format("Domain=%s, groupCode=%s, code=%s", "code", param.getGroupCode(), param.getCode()));
		}
		return CodeData.from(codeRepository.save(create(param)));
	}

	public MipCode create(AddCodeParam param) {
		return MipCode.builder()
				.id(MipCodeId.create(param.getGroupCode(), param.getCode()))
				.name(param.getName())
				.description(param.getDescription())
				.enabled(param.isEnabled())
				.modifiedBy(ModifyEmployee.builder()
						.createEmployeeNumber("admin")	// TODO 생성 사용자 정보 저장
						.build())
				.build();
	}

	@Override
	public CodeData getCode(String groupCode, String code) {
		return CodeData.from(findById(MipCodeId.create(groupCode, code)));
	}

	private MipCode findById(MipCodeId mipCodeId) {
		return codeRepository.findById(mipCodeId).orElseThrow(() ->
				new CommonException(CommonError.NOT_FOUND, mipCodeId.toString()));
	}

	@Override
	@Transactional
	public CodeData updateCode(String groupCode, String code, UpdateCodeParam param) {
		MipCode mipCode = findById(MipCodeId.create(groupCode, code));

		if (CommonUtil.isNotNullAndEmpty(param.getName())) {
			mipCode.setName(param.getName());
		}
		if (CommonUtil.isNotNullAndEmpty(param.getDescription())) {
			mipCode.setDescription(param.getDescription());
		}
		if (param.getEnabled() != null) {
			mipCode.setEnabled(param.getEnabled());
		}
		if (CommonUtil.isNotNullAndEmpty(param.getEmployeeNumber())) {	// TODO 수정 사용자 정보 저장
			mipCode.getModifiedBy().setUpdateEmployeeNumber(param.getEmployeeNumber());
		}
		return CodeData.from(mipCode);
	}

	@Override
	@Transactional
	public CodeData deleteCode(String groupCode, String code) {
		MipCode mipCode = findById(MipCodeId.create(groupCode, code));
		codeRepository.delete(mipCode);
		return CodeData.from(mipCode);
	}

	@Override
	public List<String> getGroupCode() {
		return codeRepository.findAllGroupCode();
	}

	@Override
	public List<CodeData> findCode(String groupCode, FindCodeParam param) {
		// TODO 임시 구현, 추후 검색조건 추가 시 수정
		return CodeData.from(codeRepository.findAllByIdGroupCode(groupCode));
	}
}
