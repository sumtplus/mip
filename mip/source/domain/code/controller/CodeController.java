package com.inzisoft.mobileid.sp.domain.code.controller;

import com.inzisoft.mobileid.sp.domain.admin.dto.AddAdminParam;
import com.inzisoft.mobileid.sp.domain.code.dto.AddCodeParam;
import com.inzisoft.mobileid.sp.domain.code.dto.FindCodeParam;
import com.inzisoft.mobileid.sp.domain.code.dto.UpdateCodeParam;
import com.inzisoft.mobileid.sp.domain.code.service.CodeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value ="/v1/api/codes")
public class CodeController {
	private CodeService codeService;

	public CodeController(CodeService codeService) {
		this.codeService = codeService;
	}

	@PostMapping(value = "",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity addCode(@Valid @RequestBody AddCodeParam param) {
		return ResponseEntity.ok(codeService.addCode(param));
	}

	@GetMapping(value = "/{groupCode}/{code}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getCode(@PathVariable("groupCode") String groupCode,
								  @PathVariable("code") String code) {
		return ResponseEntity.ok(codeService.getCode(groupCode, code));
	}

	@PostMapping(value = "/{groupCode}/{code}",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity updateCode(@PathVariable("groupCode") String groupCode,
									 @PathVariable("code") String code,
									  @Valid @RequestBody UpdateCodeParam param) {
		return ResponseEntity.ok(codeService.updateCode(groupCode, code, param));
	}

	@GetMapping(value = "/groups",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity findGroupCode() {
		return ResponseEntity.ok(codeService.getGroupCode());
	}

	@GetMapping(value = "/{groupCode}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity findCode(@PathVariable("groupCode") String groupCode,
								   @Valid @ModelAttribute FindCodeParam param) {
		return ResponseEntity.ok(codeService.findCode(groupCode, param));
	}

	@DeleteMapping(value = "/{groupCode}/{code}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity deleteCode(@PathVariable("groupCode") String groupCode,
									 @PathVariable("code") String code) {
		return ResponseEntity.ok(codeService.deleteCode(groupCode, code));
	}
}
