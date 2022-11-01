package com.inzisoft.mobileid.persistence;

import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JDBCTests {
	static {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testConnection() {
		try(Connection con = DriverManager.getConnection("jdbc:oracle:thin:@61.109.145.156:1521:xe","scott","scott")){
			log.info(con.toString());
		}catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
