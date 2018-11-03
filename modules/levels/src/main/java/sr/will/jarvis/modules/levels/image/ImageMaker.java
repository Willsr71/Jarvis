package sr.will.jarvis.modules.levels.image;

import net.dv8tion.jda.core.entities.User;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.modules.levels.XPUser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageMaker {
    private static final Color background = Color.decode("#111111");

    public static void createLevelImage(User user, XPUser xpUser, long channelId) {
        int width = 600;
        int height = 160;

        long startTime = System.currentTimeMillis();
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
            int x = (height - w) / 2;
            int y = (height - h) / 2;
            BufferedImage avatarImage = getUserAvatar(user.getAvatarUrl());
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
            x = (int) avatar.x + (int) avatar.width + 16;
            w = width - x - 16;
            h = 32;
            y = height - h - 16;
            g.setColor(Color.GRAY);
            RoundRectangle2D.Double levelBar = new RoundRectangle2D.Double(x, y, w, h, 30, 30);
            g.fill(levelBar);

            // Level bar fill
            g.setColor(Color.CYAN);
            w = Math.round(((float) xpUser.getUserLevelXp() / (float) (xpUser.getNextLevelXp() - xpUser.getLevelXp())) * w);
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
            g.setColor(Color.WHITE);
            g.drawString(username, x, y);
            int usernameWidth = w;

            // User discriminator
            String userdiscrim = " #" + user.getDiscriminator();
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
            x = (int) levelBar.x + 16 + usernameWidth;
            y = (int) levelBar.y - 16;
            g.setColor(Color.GRAY);
            g.drawString(userdiscrim, x, y);

            // XP to next level
            String toNextLevel = " / " + (xpUser.getNextLevelXp() - xpUser.getLevelXp()) + " XP";
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
            w = g.getFontMetrics().stringWidth(toNextLevel);
            x = (int) levelBar.x + (int) levelBar.width - w;
            y = (int) levelBar.y - 16;
            g.setColor(Color.GRAY);
            g.drawString(toNextLevel, x, y);
            int toNextLevelWidth = w;

            // XP in level
            String inLevel = xpUser.getUserLevelXp() + "";
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
            w = g.getFontMetrics().stringWidth(inLevel);
            x = (int) levelBar.x + (int) levelBar.width - w - toNextLevelWidth;
            y = (int) levelBar.y - 16;
            g.setColor(Color.WHITE);
            g.drawString(inLevel, x, y);

            // Level number
            String levelNum = xpUser.getLevel() + "";
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 32));
            w = g.getFontMetrics().stringWidth(levelNum);
            h = getStringHeight(g, levelNum);
            x = width - 16 - w;
            y = h + 16;
            g.setColor(Color.CYAN);
            g.drawString(levelNum, x, y);
            int levelNumWidth = w;

            // Level text
            String levelText = " Level ";
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
            w = g.getFontMetrics().stringWidth(levelText);
            x = width - 16 - levelNumWidth - w;
            g.setColor(Color.CYAN);
            g.drawString(levelText, x, y);
            int levelTextWidth = w;

            // Rank number
            String rankNum = "#" + xpUser.pos;
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 32));
            w = g.getFontMetrics().stringWidth(rankNum);
            x = width - 16 - levelNumWidth - levelTextWidth - w;
            g.setColor(Color.WHITE);
            g.drawString(rankNum, x, y);
            int rankNumWidth = w;

            // Rank text
            String rankText = "Rank ";
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
            w = g.getFontMetrics().stringWidth(rankText);
            x = width - 16 - levelNumWidth - levelTextWidth - rankNumWidth - w;
            g.setColor(Color.WHITE);
            g.drawString(rankText, x, y);

            g.dispose();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            Jarvis.getJda().getTextChannelById(channelId).sendFile(outputStream.toByteArray(), "rank.png").queue();

            System.out.println("Rank image created in " + (System.currentTimeMillis() - startTime) + "ms");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage getUserAvatar(String imageURL) throws IOException {
        BufferedImage avatar = getImageFromURL(imageURL);

        if (avatar.getWidth() == 128 && avatar.getHeight() == 128) {
            return avatar;
        } else {
            return getScaledImage(avatar, 128, 128);
        }
    }

    private static BufferedImage getImageFromURL(String imageURL) throws IOException {
        System.out.println(imageURL);
        URL url = new URL(imageURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(500);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2891.0 Safari/537.36");
        connection.connect();

        return ImageIO.read(connection.getInputStream());
    }

    private static BufferedImage getScaledImage(BufferedImage image, int width, int height) throws IOException {
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

    private static int getStringHeight(Graphics2D g, String s) {
        return (int) g.getFont().createGlyphVector(g.getFontRenderContext(), s).getVisualBounds().getHeight() + 1;
    }

    private static void drawOutlines(Graphics2D g, int x, int y, FontMetrics fm, String s) {
        g.setColor(Color.MAGENTA);
        g.drawRect(
                x,
                y - getStringHeight(g, s),
                fm.stringWidth(s),
                getStringHeight(g, s)
        );
    }
}
