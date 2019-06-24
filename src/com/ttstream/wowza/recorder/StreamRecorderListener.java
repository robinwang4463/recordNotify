package com.ttstream.wowza.recorder;

import java.io.File;
import java.util.Locale;

import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.joda.time.DateTime;

import com.wowza.wms.livestreamrecord.manager.IStreamRecorder;
import com.wowza.wms.livestreamrecord.manager.IStreamRecorderActionNotify;
import com.wowza.wms.logging.WMSLogger;
import com.wowza.wms.logging.WMSLoggerFactory;
import com.wowza.wms.stream.IMediaStream;

public class StreamRecorderListener implements IStreamRecorderActionNotify {
	
	private WMSLogger logger = WMSLoggerFactory.getLogger(null);
	
	public void onStartRecorder(IStreamRecorder recorder)
	{
	
	}
	
	public void onCreateRecorder(IStreamRecorder recorder)
	{
		
	}
	
	public void onSegmentStart(IStreamRecorder recorder)
	{
		
	}
	
	public void onSegmentEnd(IStreamRecorder recorder)
	{
		logger.info("onSegmentEnd");   
	}
	
	public void onSplitRecorder(IStreamRecorder recorder)
	{
		
	}
	
	public void onStopRecorder(IStreamRecorder recorder)
	{
		logger.info("onStopRecorder");  
		  
		String streamName = recorder.getStreamName();
		long duration = recorder.getCurrentDuration()/1000;
		 
		DateTime startTime = recorder.getStartTime();
		String startTimeStr = startTime.toString("yyyyMMdd_HHmmss",Locale.CHINESE);
		 
		String fileFullName = recorder.getCurrentFile();
		int indexOfName  = fileFullName.lastIndexOf(File.separator);
		String fileName = fileFullName.substring(indexOfName+1);
		  		   
		logger.info("prepare to notify Huikan Server , streamName :"+ streamName + " duration :"+duration + "startTimeStr :"+startTimeStr  + " fileName :"+fileName );
		notifySegment(HuikanConf.server,streamName,startTimeStr,duration,fileName);
	}
	
	public void onSwitchRecorder(IStreamRecorder recorder,IMediaStream newStream)
	{
		
	}
	
	private void notifySegment(String huikanServer,String streamName,String startTimeStr,long duration,String fileName)
	{
        logger.info("begin notifySegment");
          
        String time = String.valueOf(System.currentTimeMillis());
        
        //huikanServer is a http address like "http://x.x.x.x:80/some_url" ,you can edit or remove the parameter as below:
        String url = huikanServer+"?service=live.savaJsonString&jsonString="+time+"&app=vod&streamName="+streamName+"&startTime="+startTimeStr+"&duration="+duration+"&fileName="+fileName;
		  
		logger.info("url :"+url); 
            		    
		CloseableHttpClient httpclient = HttpClients.custom().build();
       	 
        CloseableHttpResponse response = null;
        	
        try {
            HttpGet httpget = new HttpGet(url);
            logger.info("Executing request " + httpget.getRequestLine());

            response = httpclient.execute(httpget);
            
            StatusLine sL = response.getStatusLine();
            int statusCode = sL.getStatusCode();
            if (statusCode == 200)
            {
                logger.info("status code 200 , send segment Notify success!" );
            }else
            {
                logger.info("status code "+String.valueOf(statusCode) +"   /n/r"+sL.toString());
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }finally 
        {
            try{
                response.close();
                httpclient.close();
            }
            catch(Exception e)
            {
                logger.error(e.toString());  
            }
        }
    }
}