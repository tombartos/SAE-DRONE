package fr.univtln.infomath.dronsim.Utils;

import org.freedesktop.gstreamer.*;
import org.freedesktop.gstreamer.elements.AppSrc;

public class GStreamerSender {
    private final Pipeline pipeline;
    private final AppSrc appSrc;

    public GStreamerSender(int width, int height, String ipDest) {
        Gst.init("GStreamerSender");

        String pipelineStr = "appsrc name=source is-live=true block=true format=TIME caps=video/x-raw,format=RGBA,width="
                + width + ",height=" + height + ",framerate=60/1 ! " +
                "videoconvert ! " +
                "x264enc tune=zerolatency speed-preset=ultrafast bitrate=2500 ! " +
                "rtph264pay config-interval=1 ! " +
                "udpsink host=" + ipDest + " port=5600 sync=false";

        pipeline = (Pipeline) Gst.parseLaunch(pipelineStr);
        appSrc = (AppSrc) pipeline.getElementByName("source");
        pipeline.play();
    }

    public void pushFrame(byte[] frameData) {
        Buffer buffer = new Buffer(frameData.length);
        buffer.map(true).put(frameData);
        appSrc.pushBuffer(buffer);
    }
}
