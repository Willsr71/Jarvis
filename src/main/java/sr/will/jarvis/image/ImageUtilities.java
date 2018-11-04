package sr.will.jarvis.image;

import net.dv8tion.jda.core.entities.MessageChannel;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageUtilities {

    public static BufferedImage getUserAvatar(String imageURL, int size) throws IOException {
        BufferedImage avatar = getImageFromURL(imageURL);

        if (avatar.getWidth() == size && avatar.getHeight() == size) {
            return avatar;
        } else {
            return getScaledImage(avatar, size, size);
        }
    }

    public static BufferedImage getImageFromURL(String imageURL) throws IOException {
        URL url = new URL(imageURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(500);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2891.0 Safari/537.36");
        connection.connect();

        return ImageIO.read(connection.getInputStream());
    }

    public static BufferedImage getScaledImage(BufferedImage image, int width, int height) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        double scaleX = (double) width / imageWidth;
        double scaleY = (double) height / imageHeight;
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
        AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);

        return bilinearScaleOp.filter(
                image,
                new BufferedImage(width, height, image.getType()));
    }

    public static void sendImage(MessageChannel channel, BufferedImage image, String fileName) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        channel.sendFile(outputStream.toByteArray(), fileName).queue();
    }
}
