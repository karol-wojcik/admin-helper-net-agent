package agent;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class Report {
	
	public void Send(String endpoint, String urlParameters ) throws IOException{
		try {
			String charset = "UTF-8";
			Properties prop = new Properties();
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("config/config.properties");
			prop.load(in);
			String apiPoint = prop.getProperty("SERVER_URL") + endpoint;
			String sensorKey = prop.getProperty("SENSOR_KEY");
			
			if (sensorKey == null) {
				// TODO: make api call to get sensor_key 
				;
			}
			
			URL url = new URL(apiPoint); 
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();           
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false); 
			connection.setRequestMethod("POST"); 
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
			connection.setRequestProperty("charset", charset);
			connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches (false);

			DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
			wr.writeBytes(urlParameters);
//			System.out.println(connection.getResponseCode());
//			System.out.println(connection.getResponseMessage());
			
			// print server response
			BufferedReader inn;
			if (connection.getResponseCode() < 400) {  
			    inn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			} else {  
			     /* error from server */
				inn = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
			}
			String inputLine;
			String response = "";
			while ((inputLine = inn.readLine()) != null) 
				response = response + inputLine;
			
			wr.flush();
			wr.close();
			connection.disconnect();
			System.out.println("Response from " + apiPoint + ": " + response);
			System.out.println();
		} catch (IOException ex) {
    		ex.printStackTrace();
        }
	}
	
}
