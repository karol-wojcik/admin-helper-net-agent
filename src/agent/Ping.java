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
		// default constructor
		this.prop = new Properties();
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("config/config.properties");
		try {
			this.prop.load(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return 
	 * 
	 * Method to start whole test procedure
	 * Gets hosts from a file and starts testPingSendReport method 
	 */
	public boolean testPing(){
		// get addresses
		FileInputStream in = null;
		try {
			// get file with hosts
			in = new FileInputStream("serverIPs.txt");
		} catch (FileNotFoundException e2) {
			// no file available - create new file with one, default host
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
				// read hosts file and load them into arrayList
				hosts.add(strLine);
			}
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		
		try {
			//close hosts file
			in.close();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		for (String address : hosts) {
			// do test and send report for each host 
			this.testPingSendReport(address);
		}
		
		return true;
	}

	/**
	 * 
	 * @param address ipAddress/host to ping
	 * @return true/false 
	 * 
	 * Method to get ping result, fill the report form up and send (REST - POST) report to API endpoint
	 */
	private boolean testPingSendReport(String address) {
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
		rd.setDevice(address);
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
			return false;
		}
		
		return true;
	}

	/**
	 * 
	 * @param address ipAddress/host to ping 
	 * @return avg ping time as a String
	 * @throws IOException 
	 * @throws IOException
	 * 
	 * Method to ping given address
	 * It's making a GET request to a server. The server performs ping test and returns avgPingTime or -1 elsewhere  
	 */
	private String doPing(String address) throws IOException, IOException {
		// b
		String urlParameters = "url=" + address;
		try {
			String charset = "UTF-8";
			Properties prop = new Properties();
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("config/config.properties");
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
			
			return response;
		} catch (IOException ex) {
    		ex.printStackTrace();
        }
		return "-1";
    }
}