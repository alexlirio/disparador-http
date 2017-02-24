package com.company.rebatedor.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.company.disparador.DisparadorHTTP;

public class DispararSemParametrosTest {

	DisparadorHTTP disparadorHTTP = new DisparadorHTTP();

	@Test
	public void test() {
		System.out.println("Inside DispararSemParametrosTest");
		assertNotNull(disparadorHTTP.disparar());
	}

}
