package sr.will.jarvis.modules.levels.image;

import sr.will.jarvis.Jarvis;
import sr.will.jarvis.image.ImageTextGenerator;
import sr.will.jarvis.image.ImageUtilities;
import sr.will.jarvis.modules.levels.XPUser;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;


public class ImageMaker {
    private static final Color background = Color.decode("#111111");
    private static final Color highlightColor = Color.CYAN;
    private static final Color primaryColor = Color.WHITE;
    private static final Color secondaryColor = Color.GRAY;

    public static BufferedImage createLeaderboardImage(ArrayList<XPUser> leaderboard, int page, long maxXp, int totalUsers) throws IOException {
        int width = 800;
        int rowHeight = 100;
        int height = (rowHeight * leaderboard.size()) + 32;

        long startTime = System.currentTimeMillis();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Image background
        g.setColor(background);
        g.fillRoundRect(0, 0, width, height, 20, 20);

        // Determine number width
        int largestNumberWidth = new ImageTextGenerator(g, "#" + ((page * 10) + leaderboard.size()))
                .setFontSize(64)
                .width();

        for (int p = 0; p < leaderboard.size(); p += 1) {
            XPUser user = leaderboard.get(p);
            int yStart = rowHeight * p;

            // Position text
            ImageTextGenerator posText = new ImageTextGenerator(g, "#" + ((page * 10) + (p + 1)));
            posText.setFontSize(64).setColor(primaryColor)
                    .setPos(16 + largestNumberWidth - posText.width(),
                            yStart + rowHeight - ((rowHeight - posText.height()) / 2))
                    .draw();

            // User avatar
            int avatarSize = rowHeight - 2;
            int x = 16 + largestNumberWidth + 16;
            int y = yStart + ((rowHeight - avatarSize) / 2);
            BufferedImage avatarImage = ImageUtilities.getUserAvatar(user.getUser().getAvatarUrl(), avatarSize);
            Ellipse2D.Double avatar = new Ellipse2D.Double(x, y, avatarSize, avatarSize);
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
            int w = width - x - 16;
            int h = 32;
            y = yStart + rowHeight - h - 16;
            g.setColor(secondaryColor);
            RoundRectangle2D.Double levelBar = new RoundRectangle2D.Double(x, y, w, h, 30, 30);
            g.fill(levelBar);

            // Level bar fill
            g.setColor(highlightColor);
            w = Math.round((float) ((double) user.xp / (double) maxXp) * w);
            g.fillRoundRect(x - 16, y, w + 16, h, 30, 30);

            // Level bar clip
            Area levelBarClip = new Area(new Rectangle2D.Double(levelBar.x - 16, levelBar.y, levelBar.width + 16, levelBar.height));
            levelBarClip.subtract(new Area(levelBar));
            g.setColor(background);
            g.fill(levelBarClip);

            // User name
            ImageTextGenerator username = new ImageTextGenerator(g, user.getUser().getName());
            username.setFontSize(32).setColor(primaryColor)
                    .setPos(levelBar.x + 8,
                            levelBar.y - 8)
                    .draw();

            // User discriminator
            ImageTextGenerator userDiscrim = new ImageTextGenerator(g, " #" + user.getUser().getDiscriminator());
            userDiscrim.setFontSize(20).setColor(secondaryColor)
                    .setPos(username.X() + username.width(),
                            username.Y())
                    .draw();

            // Prevent overlapping of username and level
            g.setClip(userDiscrim.X() + userDiscrim.width(), yStart, width - userDiscrim.X() + userDiscrim.width(), rowHeight);

            // Level number
            ImageTextGenerator levelNum = new ImageTextGenerator(g, user.getLevel() + "");
            levelNum.setFontSize(32).setColor(highlightColor)
                    .setPos(width - 16 - levelNum.width(),
                            yStart + levelNum.height() + 16)
                    .draw();

            // Level text
            ImageTextGenerator levelText = new ImageTextGenerator(g, " Level ");
            levelText.setFontSize(20).setColor(highlightColor)
                    .setPos(levelNum.X() - levelText.width(),
                            levelNum.Y())
                    .draw();

            g.setClip(null);
        }

        ImageTextGenerator pageText = new ImageTextGenerator(g, "Page " + (page + 1) + " of " + (int) Math.ceil(totalUsers / 10D));
        pageText.setFontSize(26).setColor(primaryColor)
                .setPos(width - 16 - pageText.width(),
                        height - 16)
                .draw();

        g.dispose();

        Jarvis.getLogger().info("Leaderboard image created in {}ms", System.currentTimeMillis() - startTime);
        return image;
    }

    public static BufferedImage createRankImage(XPUser user) throws IOException {
        int width = 600;
        int height = 160;

        long startTime = System.currentTimeMillis();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Image background
        g.setColor(background);
        g.fillRoundRect(0, 0, width, height, 20, 20);

        // User avatar
        int avatarSize = 128;
        int x = (height - avatarSize) / 2;
        int y = (height - avatarSize) / 2;
        BufferedImage avatarImage = ImageUtilities.getUserAvatar(user.getUser().getAvatarUrl(), avatarSize);
        Ellipse2D.Double avatar = new Ellipse2D.Double(x, y, avatarSize, avatarSize);
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
        int w = width - x - 16;
        int h = 32;
        y = height - h - 16;
        g.setColor(secondaryColor);
        RoundRectangle2D.Double levelBar = new RoundRectangle2D.Double(x, y, w, h, 30, 30);
        g.fill(levelBar);

        // Level bar fill
        g.setColor(highlightColor);
        w = Math.round(((float) user.getUserLevelXp() / (float) user.getNeededXp()) * w);
        g.fillRoundRect(x - 16, y, w + 16, h, 30, 30);

        // Level bar clip
        Area levelBarClip = new Area(new Rectangle2D.Double(levelBar.x - 16, levelBar.y, levelBar.width + 16, levelBar.height));
        levelBarClip.subtract(new Area(levelBar));
        g.setColor(background);
        g.fill(levelBarClip);

        // User name
        ImageTextGenerator username = new ImageTextGenerator(g, user.getUser().getName());
        username.setFontSize(32).setColor(primaryColor)
                .setPos(levelBar.x + 16,
                        levelBar.y - 16)
                .draw();

        // User discriminator
        ImageTextGenerator userDiscrim = new ImageTextGenerator(g, " #" + user.getUser().getDiscriminator());
        userDiscrim.setFontSize(20).setColor(secondaryColor)
                .setPos(username.X() + username.width(),
                        username.Y())
                .draw();

        // Prevent overlapping of username and xp
        g.setClip(userDiscrim.X() + userDiscrim.width(), 0, width, height);

        // XP to next level
        ImageTextGenerator toNextLevel = new ImageTextGenerator(g, " / " + user.getNeededXp() + " XP");
        toNextLevel.setFontSize(20).setColor(secondaryColor)
                .setPos(levelBar.x + levelBar.width - toNextLevel.width(),
                        levelBar.y - 16)
                .draw();

        // XP in level
        ImageTextGenerator inLevel = new ImageTextGenerator(g, user.getUserLevelXp() + "");
        inLevel.setFontSize(20).setColor(primaryColor)
                .setPos(toNextLevel.X() - inLevel.width(),
                        toNextLevel.Y())
                .draw();

        g.setClip(null);

        // Level number
        ImageTextGenerator levelNum = new ImageTextGenerator(g, user.getLevel() + "");
        levelNum.setFontSize(32).setColor(highlightColor)
                .setPos(width - 16 - levelNum.width(),
                        levelNum.height() + 16)
                .draw();

        // Level text
        ImageTextGenerator levelText = new ImageTextGenerator(g, " Level ");
        levelText.setFontSize(20).setColor(highlightColor)
                .setPos(levelNum.X() - levelText.width(),
                        levelNum.Y())
                .draw();

        // Rank number
        ImageTextGenerator rankNum = new ImageTextGenerator(g, "#" + user.pos);
        rankNum.setFontSize(32).setColor(primaryColor)
                .setPos(levelText.X() - rankNum.width(),
                        levelNum.Y())
                .draw();

        // Rank text
        ImageTextGenerator rankText = new ImageTextGenerator(g, "Rank ");
        rankText.setFontSize(20).setColor(primaryColor)
                .setPos(rankNum.X() - rankText.width(),
                        levelNum.Y())
                .draw();

        g.dispose();

        Jarvis.getLogger().info("Rank image created in {}ms", System.currentTimeMillis() - startTime);
        return image;
    }
}
