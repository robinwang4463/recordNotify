package com.ttstream.wowza.recorder;

import com.wowza.wms.application.*;
import com.wowza.wms.logging.WMSLogger;
import com.wowza.wms.module.*;
import com.wowza.wms.stream.*;

public class StreamRecordLoader extends ModuleBase {
	
	IMediaStreamActionNotify3 actionNotify = new StreamListener();
	
    private WMSLogger logger = getLogger();
	
	private String huikanServer ="";
	
	public void onAppStart(IApplicationInstance appInstance) {
		String fullname = appInstance.getApplication().getName() + "/" + appInstance.getName();
		getLogger().info("onAppStart: " + fullname);
		
		//Huikan means playback a recording file by vod streaming. The parameter "HuikanServer" is a http address like "http://x.x.x.x:80/some_url
		HuikanConf.server = appInstance.getProperties().getPropertyStr("HuikanServer");
		
        getLogger().info("get HuikanServer from Application.xml : " + this.huikanServer);
    }

	public void onStreamCreate(IMediaStream stream) {
		
        logger.info("onStreamCreate: " + stream.getName());
		stream.addClientListener(actionNotify);
		
	}
    
    public void onStreamDestroy(IMediaStream stream) {
		
    	logger.info("onStreamDestroy: " + stream.getName());
	}
}
