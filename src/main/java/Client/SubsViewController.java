package Client;

import Shared.Models.Channel;
import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SubsViewController {

    public Button subsButton;
    public WebView myWebView;

    public void setChannel(Channel channel)
    {
        subsButton.setText(channel.getName());
        try {
            Path path = new File("src/main/resources/Client/profile.html").toPath();
            String htmlContent = new String(Files.readAllBytes(path));
            Rectangle rec = new Rectangle(24, 24);
            rec.setArcWidth(24);
            rec.setArcHeight(24);
            myWebView.setClip(rec);
            rec.widthProperty().bind(myWebView.widthProperty());
            rec.heightProperty().bind(myWebView.heightProperty());
            myWebView.getEngine().loadContent(htmlContent.replace("@id", channel.getChannelId() + ""));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
