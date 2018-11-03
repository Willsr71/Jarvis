package sr.will.jarvis.modules.levels.image;

import net.dv8tion.jda.core.entities.User;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.modules.levels.XPUser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageMaker {
    private static final int width = 600;
    private static final int height = 160;
    private static final Color background = Color.decode("#111111");

    public static void createLevelImage(User user, XPUser xpUser, long channelId) {
        try {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Image background
            g.setColor(background);
            g.fillRoundRect(0, 0, width, height, 20, 20);

            // User avatar
            int w = 128;
            int h = 128;
            int x = (height - w) / 2; // 16
            int y = (height - h) / 2; // 16
            BufferedImage avatarImage = getImageFromURL(user.getAvatarUrl());
            Ellipse2D.Double avatar = new Ellipse2D.Double(x, y, w, h);
            g.setClip(avatar);
            g.drawImage(avatarImage, x, y, null);
            g.setClip(null);

            // Circle around avatar
            int cw = 1;
            Area avatarCircle = new Area(new Ellipse2D.Double(avatar.x - cw, avatar.y - cw, avatar.width + (cw * 2), avatar.height + (cw * 2)));
            avatarCircle.subtract(new Area(new Ellipse2D.Double(avatar.x + cw, avatar.y + cw, avatar.width - (cw * 2), avatar.height - (cw * 2))));
            g.setColor(Color.BLACK);
            g.fill(avatarCircle);

            // Level bar
            x = (int) avatar.x + (int) avatar.width + 16; // 16 + 128 + 16 = 160
            w = width - x - 16; // 600 - 160 - 16 = 424
            h = 32;
            y = height - h - 16; // 160 - 32 - 16 = 112
            g.setColor(Color.GRAY);
            RoundRectangle2D.Double levelBar = new RoundRectangle2D.Double(x, y, w, h, 30, 30);
            g.fill(levelBar);

            // Level bar fill
            g.setColor(Color.CYAN);
            w = Math.round(((float) xpUser.getUserLevelXp() / (float) xpUser.getNextLevelXp()) * w);
            g.fillRoundRect(x - 16, y, w + 16, h, 30, 30);

            // Level bar clip
            Area levelBarClip = new Area(new Rectangle2D.Double(levelBar.x - 16, levelBar.y, levelBar.width + 16, levelBar.height));
            levelBarClip.subtract(new Area(new RoundRectangle2D.Double(levelBar.x, levelBar.y, levelBar.width, levelBar.height, 30, 30)));
            g.setColor(background);
            g.fill(levelBarClip);

            // User name
            String username = user.getName();
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 32));
            w = g.getFontMetrics().stringWidth(username);
            x = (int) levelBar.x + 16;
            y = (int) levelBar.y - 16;
            g.setPaint(Color.WHITE);
            g.drawString(username, x, y);
            int usernameWidth = w;

            // User discriminator
            String userdiscrim = " #" + user.getDiscriminator();
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
            x = (int) levelBar.x + 16 + usernameWidth;
            y = (int) levelBar.y - 16;
            g.setPaint(Color.GRAY);
            g.drawString(userdiscrim, x, y);

            // XP to next level
            String toNextLevel = " / " + xpUser.getNextLevelXp() + " XP";
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
            w = g.getFontMetrics().stringWidth(toNextLevel);
            x = (int) levelBar.x + (int) levelBar.width - w;
            y = (int) levelBar.y - 16;
            g.setPaint(Color.GRAY);
            g.drawString(toNextLevel, x, y);
            int toNextLevelWidth = w;

            // XP in level
            String inLevel = xpUser.getUserLevelXp() + "";
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
            w = g.getFontMetrics().stringWidth(inLevel);
            x = (int) levelBar.x + (int) levelBar.width - w - toNextLevelWidth;
            y = (int) levelBar.y - 16;
            g.setPaint(Color.WHITE);
            g.drawString(inLevel, x, y);

            g.dispose();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            Jarvis.getJda().getTextChannelById(channelId).sendFile(outputStream.toByteArray(), "rank.png").queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage getImageFromURL(String imageURL) throws IOException {
        URL url = new URL(imageURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(500);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2891.0 Safari/537.36");
        connection.connect();

        return ImageIO.read(connection.getInputStream());
    }
}
