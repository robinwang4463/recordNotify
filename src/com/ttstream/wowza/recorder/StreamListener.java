package com.ttstream.wowza.recorder;

import com.wowza.wms.amf.AMFPacket;
import com.wowza.wms.application.IApplication;
import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.client.IClient;
import com.wowza.wms.livestreamrecord.manager.ILiveStreamRecordManager;
import com.wowza.wms.livestreamrecord.manager.IStreamRecorder;
import com.wowza.wms.livestreamrecord.manager.IStreamRecorderConstants;
import com.wowza.wms.livestreamrecord.manager.StreamRecorderParameters;
import com.wowza.wms.media.model.MediaCodecInfoAudio;
import com.wowza.wms.media.model.MediaCodecInfoVideo;
import com.wowza.wms.stream.IMediaStream;
import com.wowza.wms.logging.WMSLogger;
import com.wowza.wms.logging.WMSLoggerFactory;
import com.wowza.wms.stream.IMediaStreamActionNotify3;
import com.wowza.wms.vhost.IVHost;
import com.wowza.wms.vhost.VHostSingleton;


public class StreamListener implements IMediaStreamActionNotify3{
	
	
	private WMSLogger logger = WMSLoggerFactory.getLogger(null);
	
	private ILiveStreamRecordManager lrm = null;
	
	private IApplication app = null;
	
	private IApplicationInstance appIns = null;
	
	public void onMetaData(IMediaStream stream, AMFPacket metaDataPacket)
	{
		
	}

	public void onPauseRaw(IMediaStream stream, boolean isPause, double location)
	{
		
	}

	public void onPause(IMediaStream stream, boolean isPause, double location)
	{
		
	}

	public void onPlay(IMediaStream stream, String streamName, double playStart, double playLen, int playReset)
	{
		
	}
	public void onSeek(IMediaStream stream, double location)
	{
		
	}

	public void onStop(IMediaStream stream)
	{
		
	}

	public void onCodecInfoAudio(IMediaStream stream,MediaCodecInfoAudio codecInfoAudio) {
	
	}

	public void onCodecInfoVideo(IMediaStream stream,MediaCodecInfoVideo codecInfoVideo) {
		
	}
	
	public void onPublish(IMediaStream stream, String streamName, boolean isRecord, boolean isAppend)
	{
	    logger.info("onPublish[" + stream.getContextStr() + "]: streamName:" + streamName + " isRecord:" + isRecord + " isAppend:" + isAppend);
		 
	    IClient client = stream.getClient();
	    if (client == null){
	    	return;
	    }
	    	
	    String appName = client.getApplication().getName();
	    		
	    IVHost vhost = null;
		
		
		try
		{
			vhost = VHostSingleton.getInstance(IVHost.VHOST_DEFAULT);
			if(vhost == null)
			{
				WMSLoggerFactory.getLogger(null).warn("Failed to get Vhost, recoder task can not run.");
				return;
			}
		}
		catch (Exception e)
		{
			WMSLoggerFactory.getLogger(null).error(": Failed to get Vhost, recoder task can not run.", e);
			e.printStackTrace();
			return;
		}
		
	    try
		{
			app = vhost.getApplication(appName);
						
			appIns = app.getAppInstance(IApplicationInstance.DEFAULT_APPINSTANCE_NAME);
			if ((app == null)||(appIns == null))
			{
				WMSLoggerFactory.getLogger(null).error(": Failed to get app :" + appName + " or it's default instance, recoder task can not run.");
				return;
			}
			
		}
		catch (Exception e)
		{
			WMSLoggerFactory.getLogger(null).error(": Failed to get app :" + appName + " or it's default instance, recoder task can not run.", e);
			e.printStackTrace();
			return;	
		}
		
	    try
		{
		   StreamRecorderParameters recordParams = new StreamRecorderParameters(appIns);
		
		   recordParams.segmentationType = IStreamRecorderConstants.SEGMENT_NONE;
		   recordParams.fileFormat = IStreamRecorderConstants.FORMAT_MP4;
		   recordParams.startOnKeyFrame =  true;
		   recordParams.recordData = true;
		   
		   recordParams.versioningOption = IStreamRecorderConstants.VERSION_FILE;
		   
		   WMSLoggerFactory.getLogger(null).info("begain to start recoder a live stream ,app :" + appName + " ,  streamName : "+streamName+" !");
		   
		   lrm = vhost.getLiveStreamRecordManager();
		   
		   lrm.startRecording(appIns,streamName,recordParams);
		   
		   if ((HuikanConf.server != null)||(HuikanConf.server != "")){
			   IStreamRecorder recorder = lrm.getRecorder(appIns,streamName);
			   recorder.addListener(new StreamRecorderListener());
		   }
		}
		catch(Exception e)
		{
			WMSLoggerFactory.getLogger(null).error("Failed to start recode live stream ,app :" + appName + " ,  streamName : "+streamName+" !", e);
			e.printStackTrace();
		}
	    
	}
	
    
    
	public void onUnPublish(IMediaStream stream, String streamName, boolean isRecord, boolean isAppend)
	{
            lrm.stopRecording(appIns,streamName);
			lrm = null;
	}
	
}
