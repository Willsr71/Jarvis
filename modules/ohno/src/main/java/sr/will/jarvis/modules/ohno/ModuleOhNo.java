package sr.will.jarvis.modules.ohno;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.noxal.common.util.StringUtils;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.image.ImageUtilities;
import sr.will.jarvis.module.Module;
import sr.will.jarvis.modules.ohno.event.EventHandlerOhNo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModuleOhNo extends Module {

    public void initialize() {
        setNeededPermissions(
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE
        );
        setDefaultEnabled(false);

        registerEventHandler(new EventHandlerOhNo(this));
    }

    public void finishStart() {

    }

    public void stop() {

    }

    public void reload() {

    }

    public void addImageToImage(String imageURL, BufferedImage image, int x, int y) throws IOException {
        BufferedImage authorImage = ImageUtilities.getUserAvatar(imageURL, 128);

        Shape circle = new Ellipse2D.Double(x, y, 128, 128);

        Graphics2D graphics = image.createGraphics();
        graphics.setClip(circle);
        graphics.drawImage(authorImage, x, y, null);
        graphics.dispose();
    }

    public void addTextToImage(String string, BufferedImage image, int x, int y) {
        Graphics2D graphics = image.createGraphics();
        graphics.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
        graphics.setPaint(Color.BLACK);
        FontMetrics fontMetrics = graphics.getFontMetrics();
        List<String> lines = StringUtils.wrap(string, fontMetrics, 325);

        int yVal = y;
        for (String line : lines) {
            graphics.drawString(line, x, fontMetrics.getHeight() + yVal);
            yVal += fontMetrics.getHeight() + 2;
        }

        graphics.dispose();
    }

    public void memeMessageHistory(ArrayList<Message> messages) {
        try {
            BufferedImage image = ImageIO.read(Jarvis.getInstance().getClass().getClassLoader().getResourceAsStream("ohno.png"));

            addTextToImage(messages.get(0).getContentDisplay(), image, 60, 90);
            addImageToImage(messages.get(0).getAuthor().getAvatarUrl(), image, 185, 275);

            addTextToImage(messages.get(1).getContentDisplay(), image, 470, 90);
            addImageToImage(messages.get(1).getAuthor().getAvatarUrl(), image, 590, 275);

            addImageToImage(messages.get(2).getAuthor().getAvatarUrl(), image, 885, 260);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            messages.get(0).getChannel().sendFile(outputStream.toByteArray(), "ohno.png").queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void memeMessage(Message originalMessage) {
        originalMessage.getChannel().getHistoryBefore(originalMessage, 2).queue(messageHistory -> {
            ArrayList<Message> messages = new ArrayList<>();
            List<Message> oldList = messageHistory.getRetrievedHistory();

            messages.add(oldList.get(1));
            messages.add(oldList.get(0));
            messages.add(originalMessage);

            memeMessageHistory(messages);
        });
    }
}
