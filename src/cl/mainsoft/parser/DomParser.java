package cl.mainsoft.parser;
import java.io.*;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.*;


public class DomParser {
	
	public static String url="";
	public static String tagName="";
	public static String pathPDF="";


	public static void main(String[] args) throws Exception {

	//String url="https://www.w3schools.com/xml/plant_catalog.xml";	
	//String url2="http://alvinalexander.com/rss.xml";
		url = args[1];
		tagName=args[2];
		pathPDF=args[3];

		
		
		java.net.URLEncoder.encode(tagName, "utf-8");
		System.out.println("Url XML: "+ url);
		System.out.println("Condicion: "+ tagName);
		System.out.println("Path PDF: "+ pathPDF);
		
	try {
        new DomParser().start(url);
    } catch (Exception e) {
    
        e.printStackTrace();
        System.exit(-1);
    }
}


private void start(String urlStr) throws Exception
{
		
	boolean error= false;
	
	disableSslVerification();
	
    URL url = new URL(urlStr);
    Authenticator.setDefault(new Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication("admin","dynabci123".toCharArray());
        }
    });
 
    URLConnection yc = url.openConnection();
    BufferedReader in = new BufferedReader(new InputStreamReader(
                                yc.getInputStream()));
    String inputLine;
    while ((inputLine = in.readLine()) != null) {
    	if(inputLine.contains(tagName)){
    		System.out.println(inputLine);
    		error=true;
    	}
    		  }
    
    in.close();
    
    if (error){
    	
    	String pdf;
    	pdf=urlStr.replace("XML", "PDF");
    	pdf=pdf.replace("https://", "");
    	pdf=pdf.trim();
    	System.out.println("Url PDF: "+ pdf);
    	
    	Date ahora = new Date();
	    SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");
	    
	    String FechaHora=formateador.format(ahora).toString();
	    System.out.println("Fecha: " + FechaHora);
	    
	    String nombrePDF;
	    nombrePDF="/Incident_"+ FechaHora +".pdf";
	    System.out.println("Nombre PDF: "+ nombrePDF);
	    
	    String proceso1="curl -k -o "+ pathPDF + nombrePDF;
	    String proceso2=" https://admin:dynabci123@" + pdf;
	    
    	
    	Runtime runtime = Runtime.getRuntime();
    	
    	try {
    	    //Process process = runtime.exec("curl -k -o C:/Dashboard/DevOps_Incident.pdf https://admin:dynabci123@161.131.141.130:8021/rest/management/reports/create/DevOps_Incident?type=PDF");
    		//String proceso="curl -k -o "+ pathPDF + nombrePDF + " https://admin:dynabci123@" + "https://161.131.141.130:8021/rest/management/reports/create/DevOps_Incident?type=XML";

    	    Process process = runtime.exec(proceso1+proceso2);
    	    int resultCode = process.waitFor();

    	    if (resultCode == 0) {
    	        // all is good
    	    } 
    	} catch (Throwable e) {
    		 e.printStackTrace();
    	}
    	
    	System.exit(-1);
    }else{
    	System.exit(0);
    }
    
}


private static void disableSslVerification() {
    try
    {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
    } catch (KeyManagementException e) {
        e.printStackTrace();
    }
}
}
