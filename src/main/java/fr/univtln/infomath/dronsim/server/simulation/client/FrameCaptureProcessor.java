package fr.univtln.infomath.dronsim.server.simulation.client;

import java.nio.ByteBuffer;
import com.jme3.post.SceneProcessor;
import com.jme3.profile.AppProfiler;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.ViewPort;
import com.jme3.texture.FrameBuffer;
import com.jme3.util.BufferUtils;
import fr.univtln.infomath.dronsim.server.utils.GStreamerSender;
import com.jme3.renderer.queue.RenderQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A custom processor to capture frames from the render output for JMonkey and
 * send them on the network with a GStreamer library.
 * WARNING : GStreamer needs to be installed on the system
 *
 * @author Tom BARTIER
 */
public class FrameCaptureProcessor implements SceneProcessor {

    private static final Logger log = LoggerFactory.getLogger(FrameCaptureProcessor.class);

    private AppProfiler profiler;
    private ByteBuffer byteBuffer;
    private RenderManager renderManager;
    private Renderer renderer;
    private GStreamerSender gstreamerSender;
    private boolean isInitialized = false;
    private int width;
    private int height;

    /**
     * Constructor for the FrameCaptureProcessor.
     *
     * @param width           The width of the frame to capture.
     * @param height          The height of the frame to capture.
     * @param gstreamerSender The GStreamerSender instance to send the captured
     *                        frames.
     */
    public FrameCaptureProcessor(int width, int height, GStreamerSender gstreamerSender) {
        this.byteBuffer = BufferUtils.createByteBuffer(width * height * 4); // RGB = 3
        this.gstreamerSender = gstreamerSender;
        this.width = width;
        this.height = height;

    }

    /**
     * This method is called when the processor is added to the render manager.
     * It initializes the processor.
     *
     * @param rm The render manager.
     * @param vp The viewport.
     */
    @Override
    public void initialize(RenderManager rm, ViewPort vp) {
        renderer = rm.getRenderer();
        this.renderManager = rm;
        this.isInitialized = true;
        System.out.println("FrameCaptureProcessor initialized");
    }

    /**
     * Not implemented, no need to reshape the viewport.
     */
    @Override
    public void reshape(ViewPort vp, int w, int h) {
        return;
    }

    /**
     * Returns true if the processor is initialized.
     * This is used to check if the processor is ready to capture frames.
     */
    @Override
    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Not implemented, no need to preFrame.
     */
    @Override
    public void preFrame(float tpf) {
        return;
    }

    /**
     * Not implemented, no need to preQueue.
     */
    @Override
    public void postQueue(RenderQueue rq) {
        return;
    }

    /**
     * Main method to capture the frame.
     * It reads the framebuffer into a bytebuffer, flips it vertically, and sends
     * it to the GStreamerSender.
     * This method is called after the frame is rendered.
     * Important : it reads the default framebuffer (null) instead of the "out"
     * framebuffer
     *
     * @param out This is supposed to be the framebuffer we read from, but we don't
     *            use it
     */
    @Override
    public void postFrame(FrameBuffer out) {

        if (profiler != null) {
            profiler.appSubStep("FrameCaptureProcessor - postFrame");
        }

        if (renderManager == null) {
            log.error("RenderManager is null in FrameCaptureProcessor. Cannot capture frame.");
            return;
        }
        if (renderer == null) {
            System.err.println("Renderer is null in FrameCaptureProcessor. Cannot capture frame.");
            return;
        }

        Camera curCamera = renderManager.getCurrentCamera();
        int viewX = (int) (curCamera.getViewPortLeft() * curCamera.getWidth());
        int viewY = (int) ((1.0f - curCamera.getViewPortTop()) * curCamera.getHeight());
        int viewWidth = (int) ((curCamera.getViewPortRight() - curCamera.getViewPortLeft()) * curCamera.getWidth());
        int viewHeight = (int) ((curCamera.getViewPortTop() - curCamera.getViewPortBottom()) * curCamera.getHeight());

        // Read the framebuffer into the byteBuffer
        byteBuffer.clear();
        // renderer.setViewPort(0, 0, width, height);
        renderer.setViewPort(viewX, viewY, viewWidth, viewHeight);
        // System.out.println("Reading frame buffer: " + width + "x" + height);

        // We take the default framebuffer (null) instead of "out" because the water
        // filter take in entry "out" but renders to the default framebuffer.
        // This is a workaround to get the correct frame from the water filter.
        renderer.readFrameBuffer(null, byteBuffer);

        // Flip the framebuffer vertically
        byteBuffer.rewind();
        byte[] flippedBuffer = flipVertically(byteBuffer, width, height);

        // Send the byteBuffer to GStreamerSender
        // System.out.println("Sending frame to GStreamerSender: " + width + "x" +
        // height);
        gstreamerSender.pushFrame(flippedBuffer);
    }

    /**
     * Flips the framebuffer vertically and returns it as a bytearray.
     *
     * @param buffer The bytebuffer to flip.
     * @param width  The width of the framebuffer.
     * @param height The height of the framebuffer.
     * @return The flipped bytearray.
     */
    private byte[] flipVertically(ByteBuffer buffer, int width, int height) {
        int bytesPerPixel = 4; // Assuming RGBA format
        byte[] originalBuffer = new byte[buffer.capacity()];
        byte[] flippedBuffer = new byte[buffer.capacity()];
        buffer.rewind();
        buffer.get(originalBuffer);

        int stride = width * bytesPerPixel;
        for (int y = 0; y < height; y++) {
            int srcPos = y * stride;
            int destPos = (height - y - 1) * stride;
            System.arraycopy(originalBuffer, srcPos, flippedBuffer, destPos, stride);
        }

        return flippedBuffer;
    }

    /**
     * Not implemented, no need to cleanup.
     */
    @Override
    public void cleanup() {
        return;
    }

    /**
     * Set the profiler.
     *
     * @param profiler The profiler to set.
     */
    @Override
    public void setProfiler(AppProfiler profiler) {
        this.profiler = profiler;
    }
}
