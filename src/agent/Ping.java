package agent;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.gson.Gson;

public class Ping {
	private Properties prop;
	
	public Ping(){
		this.prop = new Properties();
		InputStream in = this.getClass().getResourceAsStream("config.properties");
		try {
			this.prop.load(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean testPing(){
		// get addresses
		FileInputStream in = null;
		try {
			in = new FileInputStream("serverIPs.txt");
		} catch (FileNotFoundException e2) {
			FileOutputStream fop = null;
			File file;
			String content = this.prop.getProperty("DEFAULT_IP_TO_TEST"); 
			try {
				file = new File("serverIPs.txt");
				fop = new FileOutputStream(file);
	 
				// if file doesn't exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				}
	 
				// get the content in bytes
				byte[] contentInBytes = content.getBytes();
	 
				fop.write(contentInBytes);
				fop.flush();
				fop.close(); 
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (fop != null) {
						fop.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					in = new FileInputStream("serverIPs.txt");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		String strLine;
		ArrayList<String> hosts = new ArrayList<String>();
		         
		try {
			while ((strLine = br.readLine()) != null) {
				hosts.add(strLine);
			}
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		
		try {
			in.close();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		for (String address : hosts) {
			this.testPingSendReport(address);
		}
		
		return true;
	}

	public boolean testPingSendReport(String address) {
		System.out.print("pinging: " + address);
		String pingRes = "";
		try {
			pingRes = this.doPing(address);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println(" with result: " + pingRes + "ms");
		
		Gson gson = new Gson();
		Report r = new Report();
		ReportData rd = new ReportData();
		ReportDataList rdl = new ReportDataList();
		List<ReportData> dataList = new ArrayList<ReportData>();
		Map<String,String> data = new HashMap<String,String>();
		
		// add new elem to the results map
		data.put("averagePingTime", pingRes);
		// fill up main object
		rd.setTime(System.currentTimeMillis() / 1000L);
		rd.setTable("ping");
		rd.setData(data);
		
		// add object to list of objects
		dataList.add(rd);
		
		// fill up list of objects
		rdl.setList(dataList);
			
		try {
			// send report
			System.out.println("Sent parameters: " + gson.toJson(rdl));
			r.Send("/store",gson.toJson(rdl));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
//	public static long pingUrl(final String address) {
//		 try {
//			final URL url = new URL("http://" + address);
//			final HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
//			urlConn.setConnectTimeout(1000 * 5); // mTimeout is in seconds
//			final long startTime = System.currentTimeMillis();
//			urlConn.connect();
//			final long endTime = System.currentTimeMillis();
//			
//			if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
//				System.out.print("Time (ms) : " + (endTime - startTime));
//				System.out.println(" Ping to "+address +" was success");
//				return endTime - startTime;
//			}
//		 } catch (final MalformedURLException e1) {
////			 e1.printStackTrace();
//			 System.out.println(" Ping to "+address +" with errors");
//		 } catch (final IOException e) {
////			 e.printStackTrace();
//			 System.out.println(" Ping to "+address +" failed");
//		 }
//		 return -1;
//		}
//	
	public String doPing(String address) throws IOException, IOException {	
		String urlParameters = "url=" + address;
		try {
			String charset = "UTF-8";
			Properties prop = new Properties();
			InputStream in = this.getClass().getResourceAsStream("config.properties");
			prop.load(in);
			
			URL url = new URL(prop.getProperty("PING_TOOL_URL"));
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();           
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches (false);
			connection.setInstanceFollowRedirects(false); 
			connection.setRequestMethod("POST"); 
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
			connection.setRequestProperty("charset", charset);
			connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));

			DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
			wr.writeBytes(urlParameters);
//			System.out.println(connection.getResponseCode());
//			System.out.println(connection.getResponseMessage());
			
			BufferedReader inn;
			if (connection.getResponseCode() < 400) {  
			    inn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			} else {  
			     /* error from server */
				return "-1";
//				inn = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
			}
			
			String inputLine;
			String response = "";
			while ((inputLine = inn.readLine()) != null) 
				response = response + inputLine;
			            
			wr.flush();
			wr.close();
			connection.disconnect();
			
//			System.out.println("Ping avg: " + response);
			return response;
		} catch (IOException ex) {
    		ex.printStackTrace();
        }
		return "-1";
    }
}