package fr.univtln.infomath.dronsim.Utils;

import org.freedesktop.gstreamer.*;
import org.freedesktop.gstreamer.elements.AppSrc;

/**
 * GStreamerSender is a utility class that sets up a GStreamer pipeline to send
 * video frames over UDP.
 *
 * It uses the GStreamer library to create a pipeline that encodes video frames
 * in H.264 format and sends them to a specified IP address.
 * GStreamer needs to be installed on the system for this class to work.
 */
public class GStreamerSender {
    private final Pipeline pipeline;
    private final AppSrc appSrc;

    /**
     * Constructor for GStreamerSender.
     *
     * @param width  The width of the video frames in pixels.
     * @param height The height of the video frames in pixels.
     * @param ipDest The destination IP address to send the video stream to.
     */
    public GStreamerSender(int width, int height, String ipDest, int port) {
        Gst.init("GStreamerSender");

        String pipelineStr = "appsrc name=source is-live=true block=true format=TIME caps=video/x-raw,format=RGBA,width="
                + width + ",height=" + height + ",framerate=60/1 ! " +
                "videoconvert ! " +
                "x264enc tune=zerolatency speed-preset=ultrafast bitrate=2500 ! " +
                "rtph264pay config-interval=1 ! " +
                "udpsink host=" + ipDest + " port=" + port + " sync=false";

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
