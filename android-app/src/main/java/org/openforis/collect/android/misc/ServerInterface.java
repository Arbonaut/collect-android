package org.openforis.collect.android.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 * 
 * @author K. Waga
 *
 */
public class ServerInterface {

        //public static final String SERVER_URL = "http://ar5.arbonaut.com/demo/fao-mobile/save-received-data-file";
        //lately used: public static final String SERVER_URL = "http://ar5.arbonaut.com/webforest/fao-mobile/save-received-data-file";

        public static String sendDataFiles(String url, String xml, String survey_id, String username, boolean overwrite) {
            return postSyncXML(url, xml, survey_id, username, overwrite);
        }

        public static List<String> getFilesList(String serverPath){        	
        	ArrayList<String> filesList = new ArrayList<String>();
        	try {
        			HttpResponse response = null;
        	        HttpClient client = new DefaultHttpClient();
        	        HttpGet request = new HttpGet();
        	        URI downloadFolder = new URI(serverPath);//new URI("http://ar5.arbonaut.com/awfdatademo/planned/");        
        	        request.setURI(downloadFolder);
        	        response = client.execute(request);
        	        BufferedReader r = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        	        StringBuilder total = new StringBuilder();

        	        String line = null;

        	       while ((line = r.readLine()) != null) {
        	    	   total.append(line);
        	    	   if (line.contains("<a href")&&!line.contains("Parent Directory</a></li>")){
        	    		   line = line.substring(line.lastIndexOf("\"> ")+3,line.indexOf("</a></li>"));
        	    		   filesList.add(line);
        	    	   }        	    	   
        	       }
        	    } catch (Exception e){
        	    	e.printStackTrace();
        	    	filesList = null;
        	    }
        	    return filesList;        	        	
        }
        
        private static String postSyncXML(String url, String xml, String survey_id, String username, boolean overwrite) {
            //String url = "http://ar5.arbonaut.com/webforest/fao-mobile/save-received-data-file";        	
            //String url = "http://ar5.arbonaut.com/demo/fao-mobile/save-received-data-file";
            HttpClient httpclient = new DefaultHttpClient(); 
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("datafile_xml_string",xml));
            ///nameValuePairs.add(new BasicNameValuePair("survey_id","99"));
            nameValuePairs.add(new BasicNameValuePair("survey_id", survey_id));
            nameValuePairs.add(new BasicNameValuePair("username",username));
            nameValuePairs.add(new BasicNameValuePair("overwrite",String.valueOf(overwrite)));

            UrlEncodedFormEntity form;
            try {
                form = new UrlEncodedFormEntity(nameValuePairs);
                        form.setContentEncoding(HTTP.UTF_8);
                HttpPost httppost = new HttpPost(url);
                httppost.setEntity(form);

                HttpResponse response = (HttpResponse) httpclient .execute(httppost);
                HttpEntity resEntity = response.getEntity();  
                String resp = EntityUtils.toString(resEntity);
                return resp;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }        
}
