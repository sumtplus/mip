package com.inzisoft.mobileid.sp.domain.admin.controller;

import com.inzisoft.mobileid.sp.domain.admin.dto.AddAdminParam;
import com.inzisoft.mobileid.sp.domain.admin.dto.AdminFindParam;
import com.inzisoft.mobileid.sp.domain.admin.dto.UpdateAdminParam;
import com.inzisoft.mobileid.sp.domain.admin.service.AdminService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value ="/v1/api/admins")
public class AdminController {
	private AdminService adminService;

	public AdminController(AdminService adminService) {
		this.adminService = adminService;
	}

	@PostMapping(value = "",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity addAdmin(@Valid @RequestBody AddAdminParam param) {
		return ResponseEntity.ok(adminService.addAdmin(param));
	}

	@GetMapping(value = "/{employeeNumber}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getAdmin(@PathVariable("employeeNumber") String employeeNumber) {
		return ResponseEntity.ok(adminService.getAdmin(employeeNumber));
	}

	@PostMapping(value = "/{employeeNumber}",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity updateAdmin(@PathVariable("employeeNumber") String employeeNumber,
									  @Valid @RequestBody UpdateAdminParam param) {
		return ResponseEntity.ok(adminService.updateAdmin(employeeNumber, param));
	}

	@GetMapping(value = "",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity findAdmin(@ModelAttribute AdminFindParam param,
									@PageableDefault(size = 10)
									@SortDefault.SortDefaults({
											@SortDefault(sort = "dateInfos.createDateTime", direction = Sort.Direction.DESC),
											@SortDefault(sort = "dateInfos.updateDateTime", direction = Sort.Direction.DESC)
									})Pageable pageable) {
		return ResponseEntity.ok(adminService.findAll(param, pageable));
	}

	@DeleteMapping(value = "/{employeeNumber}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity deleteAdmin(@PathVariable("employeeNumber") String employeeNumber) {
		return ResponseEntity.ok(adminService.updateAdmin(employeeNumber, UpdateAdminParam.createDeleteParam()));
	}

	@GetMapping(value = "/count/{authCode}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity countAdminByAuthCode(@PathVariable("authCode") String authCode) {
		return ResponseEntity.ok(adminService.countAdminByAuthCode(authCode));
	}
	
	@PostMapping(value = "/checkpw",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity checkPw(@Valid @RequestBody  UpdateAdminParam param) {
		return ResponseEntity.ok(adminService.checkPassword(param.getPassword()));
	}
	

}
