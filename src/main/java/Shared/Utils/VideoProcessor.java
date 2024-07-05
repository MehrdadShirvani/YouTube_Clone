package Shared.Utils;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class VideoProcessor {

    private String videoFilePath;

    public VideoProcessor(String videoFilePath) {
        this.videoFilePath = videoFilePath;
    }

    public long getVideoDuration() throws Exception {
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(this.videoFilePath);
        frameGrabber.start();

        long duration = frameGrabber.getLengthInTime() / 1000000;

        frameGrabber.stop();
        return duration;
    }

    public void extractFrame(String outputFilePath) throws Exception {
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(videoFilePath);
        frameGrabber.start();

        Frame frame = frameGrabber.grabImage();
        if (frame != null) {
            Java2DFrameConverter converter = new Java2DFrameConverter();
            BufferedImage bufferedImage = converter.convert(frame);

            ImageIO.write(bufferedImage, "jpg", new File(outputFilePath));
        }

        frameGrabber.stop();
    }
}
