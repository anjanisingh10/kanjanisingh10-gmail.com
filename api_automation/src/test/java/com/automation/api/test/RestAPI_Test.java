package com.automation.api.test;

import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

public class RestAPI_Test {
	
	RequestSpecification request;
	JSONObject js;
	String userEmailId;
	String pwd;
	
	@BeforeClass
	public void setUp(){
		
		RestAssured.baseURI = "https://iris.scienaptic.com/core/guest";
		userEmailId = "user" + Math.random() + "@gmail.com";
		pwd = "Abc" + Math.random();
		
	}
	
	
	@BeforeMethod(alwaysRun = true)
	public void openBaseUrl(){
		
		request = RestAssured.given();
		js = new JSONObject();
		
	}
	

	@Test
	public void testSignUpWithValidEmail(){
		try{
		
		//trying to sign-up with valid data
		js.put("name", "Anjani");
		js.put("email", userEmailId);
		js.put("password", pwd);
		
		request.header("Content-Type", "application/json");
		request.body(js.toJSONString());
		Response response = request.post("/sign-up");
		
		int responseCode = response.getStatusCode();
		
		String responseMsg = response.asString();
		System.out.println("responseCode : " + responseCode);
		
		System.out.println("responseMsg : " + responseMsg);
		
		//Assertions
		Assert.assertEquals(responseCode, 200);
		
		Assert.assertEquals(responseMsg.contains("token"), true);
		
		} catch (Exception e) {
			System.out.println("Failed to Sign Up application : " + e);
		}
		
	}
	
	
	@Test(dependsOnMethods = "testSignUpWithValidEmail")
	public void testLoginWithValidEmail(){
		try{
			
		//trying to login with valid email id and password
		js.put("email", userEmailId);
		js.put("password", pwd);
		
		request.header("Content-Type", "application/json");
		request.body(js.toJSONString());
		Response response = request.post("/login");
		
		int responseCode = response.getStatusCode();
		
		String responseMsg = response.asString();
		System.out.println("responseCode : " + responseCode);
		
		System.out.println("responseMsg : " + responseMsg);
		
		//Assertions
		Assert.assertEquals(responseCode, 200);
		
		Assert.assertEquals(responseMsg.contains("token"), true);
		
		} catch (Exception e) {
			System.out.println("Failed to Login application : " + e);
		}
		
	}
	
	@Test(dependsOnMethods = "testSignUpWithValidEmail")
	public void testSignUpWithDuplicateEmailId(){
		try{
			
		//trying to sign up with already signed up email
		js.put("name", "DuplicateEmailId");
		js.put("email", userEmailId);
		js.put("password", pwd);
		
		request.header("Content-Type", "application/json");
		request.body(js.toJSONString());
		Response response = request.post("/sign-up");
		
		int responseCode = response.getStatusCode();
		
		//Assertions
		Assert.assertEquals(responseCode, 422);
		
		Assert.assertEquals(response.jsonPath().get("success").toString().contains("false"),true);
		
		Assert.assertEquals(response.jsonPath().get("error.email").toString()
				.contains("Another user with the given email already exists"),true);
		
		} catch (Exception e) {
			System.out.println("Failed to Sign Up with dublicate email id, negative test : " + e);
		}
	
	}
	
	
	@Test
	public void testSignUpWithWeakPassword(){
		try{
			
		//trying to signup with week password
		js.put("name", "WeakPwd");
		js.put("email", "an160@gmail.com");
		js.put("password", "Abc_123");
		
		request.header("Content-Type", "application/json");
		request.body(js.toJSONString());
		Response response = request.post("/sign-up");
		
		int responseCode = response.getStatusCode();
		
		//Assertions
		Assert.assertEquals(responseCode, 422);
		
		Assert.assertEquals(response.jsonPath().get("success").toString().contains("false"),true);
		
		Assert.assertEquals(response.jsonPath().get("error.password").toString().contains("Password is weak"),true);
		
		} catch (Exception e) {
			System.out.println("Failed to Sign Up with weak password, negative test :  " + e);
		}
		
	}
	
	
	
	@Test()
	public void testLoginWithInvalidPassword(){
	try{
		
		//trying to login with only email id, password field is blank in request body	
		js.put("email", "an110@gmail.com");
		js.put("password", "Inva_445");
		
		request.header("Content-Type", "application/json");
		request.body(js.toJSONString());
		Response response = request.post("/login");
		
		int responseCode = response.getStatusCode();
		
		String responseMsg = response.asString();
		System.out.println("responseMsg : " +responseMsg);
		
		//Assertions
		Assert.assertEquals(responseCode, 422);
		
		Assert.assertEquals(response.jsonPath().get("success").toString().contains("false"),true);
		
		Assert.assertEquals(response.jsonPath().get("error.password").toString().contains("Invalid Password"),true);
	
		} catch (Exception e) {
			System.out.println("Failed to login application with invalid password, negative test : " + e);
		}
		
	}
	
	
	@Test()
	public void testLoginWithEmailNotRegistered(){
		try{
			
		//trying to login without registered email
		js.put("email", "a0si@gmail.com");
		js.put("password", "Abc_1987956");
		
		request.header("Content-Type", "application/json");
		request.body(js.toJSONString());
		Response response = request.post("/login");
		
		int responseCode = response.getStatusCode();
		
		String responseMsg = response.asString();
		
		//Assertions
		Assert.assertEquals(responseCode, 422);
		
		Assert.assertEquals(response.jsonPath().get("success").toString().contains("false"),true);
		
		Assert.assertEquals(response.jsonPath().get("error.email").toString().contains("No user account exists with the given email."),true);
		
		Assert.assertEquals(responseMsg.contains("error"), true);
		
		} catch (Exception e) {
			System.out.println("Failed to login application without registered email, negative test : " + e);
		}
		
	}
	
	
	@Test()
	public void testLoginWithInavalidRequestBodyFormat(){
	try{
		
		//trying to login with only email id, password field is missing in request body	
		js.put("email", "a0si@gmail.com");
		
		request.header("Content-Type", "application/json");
		request.body(js.toJSONString());
		Response response = request.post("/login");
		
		int responseCode = response.getStatusCode();
		
		String responseMsg = response.asString();
		
		System.out.println("responseMsg : " +responseMsg);
		
		//Assertions	
		Assert.assertEquals(responseCode, 422);
		
		Assert.assertEquals(response.jsonPath().get("success").toString().contains("false"),true);
		
		Assert.assertEquals(response.jsonPath().get("error.form").toString().contains("Input not in required format"),true);
		
		} catch (Exception e) {
			System.out.println("Failed to login wrong request body, negative test : " + e);
		}
				
	}
	
	
	@Test()
	public void testLoginWithBlankPassword(){
	try{
		
		//trying to login with only email id, password field is blank in request body	
		js.put("email", "a0si@gmail.com");
		js.put("password", "");
		
		request.header("Content-Type", "application/json");
		request.body(js.toJSONString());
		Response response = request.post("/login");
		
		int responseCode = response.getStatusCode();
		
		String responseMsg = response.asString();
		System.out.println("responseMsg : " +responseMsg);
		
		//Assertions
		Assert.assertEquals(responseCode, 422);
		
		Assert.assertEquals(response.jsonPath().get("success").toString().contains("false"),true);
		
		Assert.assertEquals(response.jsonPath().get("error.password").toString().contains("Password cannot be blank"),true);
		
		Assert.assertEquals(responseMsg.contains("error"), true);
		
		} catch (Exception e) {
			System.out.println("Failed to login application with blank password, negative test : " + e);
		}
		
	}
	
	
}
