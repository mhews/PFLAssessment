/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.;
 */

package myapp;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.StringEntity;

import java.util.Enumeration;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

public class DemoServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
		System.out.println(req.getParameter("type"));
		resp.setContentType("text/plain");
		//get product list or place order, depending on type parameter
		if (req.getParameter("type") != null){
			if (req.getParameter("type").equals("getProducts")){
				resp.getWriter().println(getProducts());
			}
			else if (req.getParameter("type").equals("order")){
				resp.getWriter().println(order(req));
			}
			else{
				resp.getWriter().println("fail");
			}
		}
		
  }
  public String order(HttpServletRequest req){
  	String data = "";

	  	try{
	  		DefaultHttpClient httpClient = new DefaultHttpClient();
	  		//create JSON object for placing order
	  		JSONObject obj = new JSONObject();
	  		obj.put("partnerOrderReference", "MyReferenceNumber");
	  		JSONObject customer = new JSONObject();
	  		customer.put("firstName", "John");
	  		customer.put("lastName", "Doe");
	  		customer.put("companyName","ACME");
	  		customer.put("address1", "1 ACME Way");
	  		customer.put("address2", "");
	  		customer.put("City", "Livingston");
	  		customer.put("State", "MT");
	  		customer.put("postalCode", "59047");
	  		customer.put("countryCode", "US");
	  		customer.put("email", "jdoe@acme.com");
	  		customer.put("phone", "1234567890");
	  		obj.put("orderCustomer", customer);
	  		JSONArray items = new JSONArray();
			JSONObject item = new JSONObject();
			item.put("itemSequenceNumber", 1);
			item.put("productID", Integer.parseInt(req.getParameter("productID")));
			item.put("quantity", Integer.parseInt(req.getParameter("quantity")));
			items.add(item);
			obj.put("items", items);
			JSONArray shipment = new JSONArray();
			JSONObject method = new JSONObject();
			method.put("shipmentSequenceNumber", 1);
			method.put("firstName", "John");
	  		method.put("lastName", "Doe");
	  		method.put("companyName","ACME");
	  		method.put("address1", "1 ACME Way");
	  		method.put("address2", "");
	  		method.put("City", "Livingston");
	  		method.put("State", "MT");
	  		method.put("postalCode", "59047");
	  		method.put("countryCode", "US");
	  		method.put("email", "jdoe@acme.com");
	  		method.put("phone", "1234567890");
	  		method.put("shippingMethod", req.getParameter("delivery"));
	  		shipment.add(method);
	  		obj.put("shipments", shipment);
	  		
	  		//build post request
			HttpPost postRequest = new HttpPost(
				"https://testapi.pfl.com/orders/?apikey=136085");
			String authString = "miniproject:Pr!nt123";
			byte[] authEncBytes = Base64.getEncoder().encode(authString.getBytes());
			String authHeader = "Basic " + new String(authEncBytes);
			postRequest.addHeader("Authorization", authHeader);
			postRequest.setHeader("Accept", "application/json");
			postRequest.setHeader("Content-type", "application/json");
			String payload = obj.toString();
			StringEntity entity = new StringEntity(payload);
	        postRequest.setEntity(entity);
	        while (data.length() == 0){
				HttpResponse response = httpClient.execute(postRequest);
				if (response.getStatusLine().getStatusCode() != 200) {
					return order(req);
					
				}
		
				BufferedReader br = new BufferedReader(
		                         new InputStreamReader((response.getEntity().getContent())));
		
				String output;
		
				while ((output = br.readLine()) != null) {
					data += output;
				}
			}
			httpClient.getConnectionManager().shutdown();
	
		   }catch (ClientProtocolException e) {
			return order(req);

	
		  } catch (IOException e) {
			return order(req);

		  }
	  

  	

	  return (data);
  

  }
  public String getProducts(){
  	String data = "";
  	try {
		//build get request
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(
			"https://testapi.pfl.com/products/?apikey=136085");
		String authString = "miniproject:Pr!nt123";
		byte[] authEncBytes = Base64.getEncoder().encode(authString.getBytes());
		String authHeader = "Basic " + new String(authEncBytes);
		getRequest.addHeader("Authorization", authHeader);
		while (data.length() == 0){
			HttpResponse response = httpClient.execute(getRequest);
	
			if (response.getStatusLine().getStatusCode() != 200) {
				return getProducts();
			}
	
			BufferedReader br = new BufferedReader(
	                         new InputStreamReader((response.getEntity().getContent())));
	
			String output;
			while ((output = br.readLine()) != null) {
				data += output;
			}
		}
	
		httpClient.getConnectionManager().shutdown();
		}
	   catch (ClientProtocolException e) {
		return getProducts();


	  } catch (IOException e) {
		return getProducts();

	  }

  	return data;


  }
  
}

